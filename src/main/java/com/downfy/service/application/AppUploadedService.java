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
import java.util.ArrayList;
import java.util.List;
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
 *  1-Dec-2013     tuanta      Create first time
 *  20-Dec-2013     tuanta      Add Developer<->App Version
 */
@Service
public class AppUploadedService {

    private final Logger logger = LoggerFactory.getLogger(AppService.class);
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    private RedisTemplate<String, AppUploadedDomain> appScreenshootRedisTemplate;

    public RedisTemplate<String, AppUploadedDomain> getAppVersionRedisTemplate() {
        if (appScreenshootRedisTemplate == null) {
            this.appScreenshootRedisTemplate = new RedisTemplate<String, AppUploadedDomain>();
            this.appScreenshootRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.appScreenshootRedisTemplate.afterPropertiesSet();
        }
        return appScreenshootRedisTemplate;
    }

    public List<AppUploadedDomain> findAll() {
        List<AppUploadedDomain> apps = null;
        try {
            this.logger.info("Find all app uploaded");
            this.logger.debug("Find all in cache.");
            apps = this.getCacheObjects();
        } catch (Exception ex) {
            this.logger.error("Find all app uploaded error: " + ex, ex);
        }
        if (apps != null) {
            this.logger.debug("Total get " + apps.size() + " apps.");
        }
        return apps;
    }

    public AppUploadedDomain findById(long appId) {
        this.logger.info("Find app uploaded " + appId);
        return getCacheObject(appId + "");
    }

    public boolean isExsit(long appId) {
        AppUploadedDomain account = getCacheObject(appId + "");
        return account != null;
    }

    public long count() {
        try {
            long count = countCacheObject();
            this.logger.info("Total " + count + " app uploaded");
        } catch (Exception ex) {
            this.logger.error("Count total app uploaded error.", ex);
        }
        return 0;
    }

    public boolean save(AppUploadedDomain domain) {
        try {
            this.logger.info("Save app uploaded " + domain);
            putCacheObject(domain, domain.getId());
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't save app uploaded " + domain, ex);
        }

        return false;
    }

    public boolean delete(long key, long developerId) {
        try {
            this.logger.info("Delete app uploaded " + key);
            this.logger.debug("Delete in cache.");
            removeCacheObject(key + "", developerId);
            this.logger.info("Delete success app uploaded " + key);
        } catch (Exception ex) {
            this.logger.error("Can't delete app uploaded " + key, ex);
            return false;
        }
        return true;
    }

    private void putCacheObject(AppUploadedDomain domain, long appId) {
        try {
            this.logger.debug("Put to cache " + domain);
            this.getAppVersionRedisTemplate().opsForHash().put(AppUploadedDomain.OBJECT_KEY, domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    private AppUploadedDomain getCacheObject(String key) {
        AppUploadedDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + AppUploadedDomain.OBJECT_KEY + " in cache");
            domain = (AppUploadedDomain) this.getAppVersionRedisTemplate().opsForHash().get(AppUploadedDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("App " + key + " object " + AppUploadedDomain.OBJECT_KEY + " not found");
            }
        } catch (NumberFormatException ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    private void removeCacheObject(String key, long appId) {
        try {
            this.logger.debug("Remove key " + key + " object " + AppUploadedDomain.OBJECT_KEY + " in cache");
            this.getAppVersionRedisTemplate().opsForHash().delete(AppUploadedDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private void removeCacheObject(String key) {
        try {
            this.logger.debug("Remove key " + key + " object " + AppUploadedDomain.OBJECT_KEY + " in cache");
            this.getAppVersionRedisTemplate().opsForHash().delete(AppUploadedDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private List<AppUploadedDomain> getCacheObjects() {
        List<AppUploadedDomain> apps = new ArrayList<AppUploadedDomain>();
        try {
            this.logger.debug("Get all objects " + AppUploadedDomain.OBJECT_KEY + " in cache");
            for (Object user : this.getAppVersionRedisTemplate().opsForHash().values(AppUploadedDomain.OBJECT_KEY)) {
                apps.add((AppUploadedDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppUploadedDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private long countCacheObject() {
        try {
            this.logger.debug("Count objects " + AppUploadedDomain.OBJECT_KEY + " in cache");
            return this.getAppVersionRedisTemplate().opsForHash().size(AppUploadedDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppUploadedDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    public void clearCache(long appId) {
        try {
            this.logger.debug("Clear objects " + AppUploadedDomain.OBJECT_KEY + " in cache");
            List<AppUploadedDomain> objects = getCacheObjects();
            for (AppUploadedDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey(), appId);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppUploadedDomain.OBJECT_KEY + " from Redis", ex);
        }
    }

    public void clearCache() {
        try {
            this.logger.debug("Clear objects " + AppUploadedDomain.OBJECT_KEY + " in cache");
            List<AppUploadedDomain> objects = getCacheObjects();
            for (AppUploadedDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppUploadedDomain.OBJECT_KEY + " from Redis", ex);
        }
    }
}
