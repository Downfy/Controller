/*
 * Copyright (C) 2013 Downfy Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.downfy.service.application;

import com.downfy.persistence.domain.application.AppUploadedDomain;
import com.downfy.persistence.table.AppUploadedTable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/*
 * AppUploadedService.java
 *
 * App uploaded service
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  1-Dec-2013      tuanta      Create first time
 *  20-Dec-2013     tuanta      Add Developer<->App Version
 */
@Service
public class AppUploadedService {

    private final Logger logger = LoggerFactory.getLogger(AppUploadedService.class);
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    private RedisTemplate<String, AppUploadedDomain> appUploadedRedisTemplate;
    private RedisTemplate<String, String> longRedisTemplate;

    public RedisTemplate<String, String> getLongRedisTemplate() {
        if (longRedisTemplate == null) {
            this.longRedisTemplate = new RedisTemplate<String, String>();
            this.longRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.longRedisTemplate.afterPropertiesSet();
        }
        return longRedisTemplate;
    }

    public RedisTemplate<String, AppUploadedDomain> getAppUploadedRedisTemplate() {
        if (appUploadedRedisTemplate == null) {
            this.appUploadedRedisTemplate = new RedisTemplate<String, AppUploadedDomain>();
            this.appUploadedRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.appUploadedRedisTemplate.afterPropertiesSet();
        }
        return appUploadedRedisTemplate;
    }

    public AppUploadedDomain findById(long id) {
        this.logger.info("Find app uploaded by " + id);
        return getCacheObject(id + "");
    }

    public List<AppUploadedDomain> findByType(long appId, int type) {
        this.logger.info("Find app uploaded by " + appId + " with type " + type);
        return getCacheObjects(appId, type);
    }

    public boolean save(AppUploadedDomain domain) {
        try {
            this.logger.info("Save app uploaded " + domain);
            putCacheObject(domain, domain.getAppId());
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't save app uploaded " + domain, ex);
        }

        return false;
    }

    public boolean delete(long key, long appId, int type) {
        try {
            this.logger.info("Delete app uploaded " + key);
            AppUploadedDomain domain = getCacheObject(key + "");
            FileUtils.deleteQuietly(new File(domain.getAppPath()));
            removeCacheObject(key + "", appId, type);
            this.logger.info("Delete success app uploaded " + key);
        } catch (Exception ex) {
            this.logger.error("Can't delete app uploaded " + key, ex);
            return false;
        }
        return true;
    }

    private void putCacheObject(AppUploadedDomain domain, long appId) {
        try {
            this.logger.debug("Put to cache " + AppUploadedTable.KEY + ":" + domain.getType() + ":" + appId);
            this.getLongRedisTemplate().opsForSet().add(AppUploadedTable.KEY + ":" + domain.getType() + ":" + appId, domain.getKey());
            this.getAppUploadedRedisTemplate().opsForHash().put(AppUploadedDomain.OBJECT_KEY, domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    private void removeCacheObject(String key, long appId, int type) {
        try {
            this.logger.debug("Remove key " + key + " object " + AppUploadedDomain.OBJECT_KEY + " in cache");
            this.getLongRedisTemplate().opsForSet().remove(AppUploadedTable.KEY + ":" + type + ":" + appId, key);
            this.getAppUploadedRedisTemplate().opsForHash().delete(AppUploadedDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private AppUploadedDomain getCacheObject(String key) {
        AppUploadedDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + AppUploadedDomain.OBJECT_KEY + " in cache");
            domain = (AppUploadedDomain) this.getAppUploadedRedisTemplate().opsForHash().get(AppUploadedDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("App " + key + " object " + AppUploadedDomain.OBJECT_KEY + " not found");
            }
        } catch (NumberFormatException ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    private List<AppUploadedDomain> getCacheObjects(long appId, int type) {
        Collection<Object> keys = getKeys(appId, type);
        List<AppUploadedDomain> apps = new ArrayList<AppUploadedDomain>();
        try {
            this.logger.debug("Get all objects " + AppUploadedDomain.OBJECT_KEY + " in cache");
            for (Object user : this.getAppUploadedRedisTemplate().opsForHash().multiGet(AppUploadedDomain.OBJECT_KEY, keys)) {
                apps.add((AppUploadedDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppUploadedDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    public void clearCache(long appId, int type) {
        try {
            this.logger.debug("Clear objects " + AppUploadedDomain.OBJECT_KEY + " in cache");
            List<AppUploadedDomain> objects = getCacheObjects(appId, type);
            for (AppUploadedDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey(), appId, type);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppUploadedDomain.OBJECT_KEY + " from Redis", ex);
        }
    }

    private Collection<Object> getKeys(long appId, int type) {
        logger.info("Get list file uploaded of app " + appId);
        Collection<Object> keys = new ArrayList<Object>();
        try {
            logger.debug("Load file uploaded of app from cache " + AppUploadedTable.KEY + ":" + type + ":" + appId);
            Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(AppUploadedTable.KEY + ":" + type + ":" + appId);
            List<String> myList = new ArrayList<String>(appIds);
            Collections.sort(myList, new Comparator<String>() {
                @Override
                public int compare(String id01, String id02) {
                    return id01.compareTo(id02);
                }
            });
            logger.debug("Get keys from app " + appId + " ==> " + appIds);
            keys.addAll(myList);
        } catch (NullPointerException ex) {
        }
        return keys;
    }
}
