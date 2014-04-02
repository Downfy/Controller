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

import com.downfy.common.AppCommon;
import com.downfy.persistence.domain.application.AppUploadedDomain;
import com.downfy.persistence.table.AppUploadedTable;
import com.google.api.client.repackaged.com.google.common.base.Objects;
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
        return getCacheObject(id + "");
    }

    public AppUploadedDomain findById(String appPackage, String appVersion) {
        return getCacheObject(appPackage + ":" + appVersion);
    }

    public List<AppUploadedDomain> findByType(long appId, int type) {
        return getCacheObjects(appId, type);
    }

    public boolean isExsit(long appId) {
        AppUploadedDomain account = getCacheObject(appId + "");
        return account != null;
    }

    public boolean isExsit(String appPackage, long appId) {
        List<AppUploadedDomain> apks = findByType(appId, AppCommon.FILE_APK);
        if (apks.isEmpty()) {
            return false;
        } else {
            return Objects.equal(apks.get(0).getAppPackage(), appPackage);
        }
    }

    public boolean isExsit(String appPackage, String appVersion) {
        AppUploadedDomain account = getCacheObject(appPackage + ":" + appVersion);
        return account != null;
    }

    public boolean isExsit(String appPackage, int type) {
        List<AppUploadedDomain> apps = getCacheObjects(appPackage, type);
        return apps.isEmpty();
    }

    public long getCreater(String appPackage, int type) {
        return getCreaterByPackage(appPackage, type);
    }

    public boolean save(AppUploadedDomain domain) {
        try {
            putCacheObject(domain, domain.getAppId());
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't save app uploaded " + domain, ex);
        }

        return false;
    }

    public boolean delete(String key, long appId, int type) {
        try {
            this.logger.info("Delete app uploaded " + key);
            AppUploadedDomain domain = getCacheObject(key);
            if (domain != null) {
                FileUtils.deleteQuietly(new File(domain.getAppPath()));
            }
            removeCacheObject(key + "", appId, type);
        } catch (Exception ex) {
            this.logger.error("Can't delete app uploaded " + key, ex);
            return false;
        }
        return true;
    }

    private void putCacheObject(AppUploadedDomain domain, long appId) {
        try {
            this.logger.debug("Put to cache " + AppUploadedTable.KEY + ":" + domain.getType() + ":" + appId + " ==> " + domain.getKey());
            this.getLongRedisTemplate().opsForSet().add(AppUploadedTable.KEY + ":" + domain.getType() + ":" + appId, domain.getKey());
            this.getLongRedisTemplate().opsForSet().add(AppUploadedTable.KEY + ":" + domain.getType() + ":" + domain.getAppPackage(), domain.getCreater() + "");
            this.getAppUploadedRedisTemplate().opsForHash().put(AppUploadedDomain.OBJECT_KEY, domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    private void removeCacheObject(String key, long appId, int type) {
        try {
            AppUploadedDomain uploadedDomain = getCacheObject(key);
            this.logger.debug("Remove key " + key + " object " + AppUploadedDomain.OBJECT_KEY + " in cache");
            this.getLongRedisTemplate().opsForSet().remove(AppUploadedTable.KEY + ":" + type + ":" + appId, key);
            this.getLongRedisTemplate().opsForSet().remove(AppUploadedTable.KEY + ":" + type + ":" + uploadedDomain.getAppPackage(), uploadedDomain.getCreater() + "");
            this.getAppUploadedRedisTemplate().opsForHash().delete(AppUploadedDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private AppUploadedDomain getCacheObject(String key) {
        AppUploadedDomain domain = null;
        try {
            domain = (AppUploadedDomain) this.getAppUploadedRedisTemplate().opsForHash().get(AppUploadedDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("App " + key + " object " + AppUploadedDomain.OBJECT_KEY + " not found");
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    private List<AppUploadedDomain> getCacheObjects(long appId, int type) {
        Collection<Object> keys = getKeys(appId, type);
        List<AppUploadedDomain> apps = new ArrayList<AppUploadedDomain>();
        try {
            for (Object user : this.getAppUploadedRedisTemplate().opsForHash().multiGet(AppUploadedDomain.OBJECT_KEY, keys)) {
                apps.add((AppUploadedDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppUploadedDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private List<AppUploadedDomain> getCacheObjects(String appPackapge, int type) {
        Collection<Object> keys = getKeys(appPackapge, type);
        List<AppUploadedDomain> apps = new ArrayList<AppUploadedDomain>();
        try {
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
        Collection<Object> keys = new ArrayList<Object>();
        try {
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
        } catch (Exception ex) {
        }
        return keys;
    }

    private Collection<Object> getKeys(String appPackage, int type) {
        Collection<Object> keys = new ArrayList<Object>();
        try {
            Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(AppUploadedTable.KEY + ":" + type + ":" + appPackage);
            List<String> myList = new ArrayList<String>(appIds);
            Collections.sort(myList, new Comparator<String>() {
                @Override
                public int compare(String id01, String id02) {
                    return id01.compareTo(id02);
                }
            });
            logger.debug("Get keys from package " + appPackage + " ==> " + appIds);
            keys.addAll(myList);
        } catch (Exception ex) {
        }
        return keys;
    }

    private long getCreaterByPackage(String appPackage, int type) {
        try {
            Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(AppUploadedTable.KEY + ":" + type + ":" + appPackage);
            logger.debug("Get keys from package " + appPackage + " ==> " + appIds);
            if (null != appIds && !appIds.isEmpty()) {
                List<String> myList = new ArrayList<String>(appIds);
                return Long.valueOf(myList.get(0));
            }
        } catch (Exception ex) {
        }
        return 0;
    }
}
