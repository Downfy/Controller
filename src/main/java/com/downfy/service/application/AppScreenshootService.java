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
import com.downfy.persistence.domain.application.AppScreenshootDomain;
import com.downfy.persistence.repositories.application.AppScreenshootRepository;
import com.downfy.persistence.table.AppVersionTable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/*
 * AppScreenshootService.java
 *
 * App screen shoot service
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  1-Dec-2013     tuanta      Create first time
 *  20-Dec-2013     tuanta      Add Developer<->App Version
 */
@Service
public class AppScreenshootService {

    private final Logger logger = LoggerFactory.getLogger(AppScreenshootService.class);
    @Autowired
    AppScreenshootRepository repository;
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    private RedisTemplate<String, AppScreenshootDomain> appScreenshootRedisTemplate;
    private RedisTemplate<String, String> longRedisTemplate;

    public RedisTemplate<String, String> getLongRedisTemplate() {
        if (longRedisTemplate == null) {
            this.longRedisTemplate = new RedisTemplate<String, String>();
            this.longRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.longRedisTemplate.afterPropertiesSet();
        }
        return longRedisTemplate;
    }

    public RedisTemplate<String, AppScreenshootDomain> getAppVersionRedisTemplate() {
        if (appScreenshootRedisTemplate == null) {
            this.appScreenshootRedisTemplate = new RedisTemplate<String, AppScreenshootDomain>();
            this.appScreenshootRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.appScreenshootRedisTemplate.afterPropertiesSet();
        }
        return appScreenshootRedisTemplate;
    }

    public List<AppScreenshootDomain> findAll() {
        List<AppScreenshootDomain> apps = null;
        try {
            this.logger.info("Find all app screen shoot");
            this.logger.debug("Find all in cache.");
            apps = this.getCacheObjects();
            if (apps.isEmpty()) {
                this.logger.debug("Find all in database.");
                apps = this.repository.findAll();
                if (!apps.isEmpty()) {
                    this.setCacheObjects(apps);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all app screen shoot error: " + ex, ex);
        }
        if (apps != null) {
            this.logger.debug("Total get " + apps.size() + " apps.");
        }
        return apps;
    }

    public AppScreenshootDomain findById(long appId) {
        this.logger.info("Find app screen shoot " + appId);
        return getCacheObject(appId + "");
    }

    public List<AppScreenshootDomain> findByApp(long appId) {
        List<AppScreenshootDomain> apps = new ArrayList<AppScreenshootDomain>();
        try {
            this.logger.info("Find all app screen shoot of app " + appId);
            this.logger.debug("Find all version in cache.");
            apps = getCacheObjects(appId);
            if (apps.isEmpty()) {
                this.logger.debug("Find all version in database.");
                apps = this.repository.findByApp(appId);
                if (!apps.isEmpty()) {
                    setCacheObjects(apps);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all app screen shoot of app " + appId + " error: " + ex, ex);
        }
        this.logger.info("Total get " + apps.size() + " app screen shoot of app " + appId + ".");
        return apps;
    }

    /**
     * Step 1: Update status app in cache
     *
     * Step 2: Update information app in db
     *
     * Step 3: Move app in storage tmp version to origin version
     *
     * @param key Application ID
     * @return
     */
    public boolean publish(long key) {
        try {
            this.logger.info("Publish app screen shoot " + key);
            this.logger.debug("Publish to cache");
            AppScreenshootDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PUBLISHED);
                putCacheObject(app, app.getAppId());
                this.logger.debug("Publish to database");
                this.repository.publish(key);
                this.logger.info("Publish success app screen shoot " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't publish app screen shoot " + key, ex);
        }
        return false;
    }

    public boolean approve(long key) {
        try {
            this.logger.info("Approve app screen shoot " + key);
            this.logger.debug("Approve to cache");
            AppScreenshootDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PENDING);
                putCacheObject(app, app.getAppId());
                this.logger.debug("Approve to database");
                this.repository.publish(key);
                this.logger.info("Approve success app screen shoot " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't approve app screen shoot " + key, ex);
        }
        return false;
    }

    public boolean block(long key) {
        try {
            this.logger.info("Block app screen shoot " + key);
            AppScreenshootDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.BLOCKED);
                putCacheObject(app, app.getAppId());
                this.logger.debug("Block app screen shoot " + key + " to database");
                this.repository.block(key);
                this.logger.info("Block success app screen shoot " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't block app screen shoot " + key, ex);
        }
        return false;
    }

    public boolean isExsit(long appId) {
        AppScreenshootDomain account = getCacheObject(appId + "");
        return account != null;
    }

    public long count(long appId) {
        try {
            long count = countCacheObject(appId);
            this.logger.info("Total " + count + " app screen shoot of app " + appId);
        } catch (Exception ex) {
            this.logger.error("Count total app screen shoot of app " + appId + " error.", ex);
        }
        return 0;
    }

    public long count() {
        try {
            long count = countCacheObject();
            this.logger.info("Total " + count + " app screen shoot");
        } catch (Exception ex) {
            this.logger.error("Count total app screen shoot error.", ex);
        }
        return 0;
    }

    public boolean save(AppScreenshootDomain domain) {
        try {
            this.logger.info("Save app screen shoot " + domain);
            this.logger.debug("Save to database");
            this.repository.save(domain);
            putCacheObject(domain, domain.getAppId());
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't save app screen shoot " + domain, ex);
        }

        return false;
    }

    public boolean delete(long key, long appId) {
        try {
            this.logger.info("Delete app screen shoot " + key);
            this.logger.debug("Delete in cache.");
            removeCacheObject(key + "", appId);
            this.logger.debug("Delete in database.");
            this.repository.delete(key);
            this.logger.info("Delete success app screen shoot " + key);
        } catch (Exception ex) {
            this.logger.error("Can't delete app screen shoot " + key, ex);
            return false;
        }
        return true;
    }

    private void putCacheObject(AppScreenshootDomain domain, long appId) {
        try {
            this.logger.debug("Put to cache " + domain);
            this.getLongRedisTemplate().opsForSet().add(AppVersionTable.KEY + ":" + appId, domain.getKey());
            this.getAppVersionRedisTemplate().opsForHash().put(AppScreenshootDomain.OBJECT_KEY, domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    private AppScreenshootDomain getCacheObject(String key) {
        AppScreenshootDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + AppScreenshootDomain.OBJECT_KEY + " in cache");
            domain = (AppScreenshootDomain) this.getAppVersionRedisTemplate().opsForHash().get(AppScreenshootDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + AppScreenshootDomain.OBJECT_KEY + " in database");
                domain = repository.findById(Long.valueOf(key));
                if (domain == null) {
                    this.logger.debug("App " + key + " object " + AppScreenshootDomain.OBJECT_KEY + " not found");
                }
            }
        } catch (NumberFormatException ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    private void removeCacheObject(String key, long appId) {
        try {
            this.logger.debug("Remove key " + key + " object " + AppScreenshootDomain.OBJECT_KEY + " in cache");
            this.getAppVersionRedisTemplate().opsForHash().delete(AppScreenshootDomain.OBJECT_KEY, key);
            this.getLongRedisTemplate().opsForSet().pop(AppVersionTable.KEY + ":" + appId);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private void removeCacheObject(String key) {
        try {
            this.logger.debug("Remove key " + key + " object " + AppScreenshootDomain.OBJECT_KEY + " in cache");
            this.getAppVersionRedisTemplate().opsForHash().delete(AppScreenshootDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private List<AppScreenshootDomain> getCacheObjects() {
        List<AppScreenshootDomain> apps = new ArrayList<AppScreenshootDomain>();
        try {
            this.logger.debug("Get all objects " + AppScreenshootDomain.OBJECT_KEY + " in cache");
            for (Object user : this.getAppVersionRedisTemplate().opsForHash().values(AppScreenshootDomain.OBJECT_KEY)) {
                apps.add((AppScreenshootDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppScreenshootDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private List<AppScreenshootDomain> getCacheObjects(long appId) {
        Collection<Object> keys = getKeys(appId);
        logger.debug("Get list app screen shoot of app " + appId + " ==> " + keys);
        List<AppScreenshootDomain> apps = new ArrayList<AppScreenshootDomain>();
        try {
            this.logger.debug("Get all objects " + AppScreenshootDomain.OBJECT_KEY + " in cache");
            for (Object user : this.getAppVersionRedisTemplate().opsForHash().multiGet(AppScreenshootDomain.OBJECT_KEY, keys)) {
                apps.add((AppScreenshootDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppScreenshootDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private long countCacheObject() {
        try {
            this.logger.debug("Count objects " + AppScreenshootDomain.OBJECT_KEY + " in cache");
            return this.getAppVersionRedisTemplate().opsForHash().size(AppScreenshootDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppScreenshootDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private long countCacheObject(long appId) {
        try {
            this.logger.debug("Count objects " + AppScreenshootDomain.OBJECT_KEY + " in cache");
            return this.getLongRedisTemplate().opsForSet().size(AppVersionTable.KEY + ":" + appId);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppScreenshootDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private void setCacheObjects(List<AppScreenshootDomain> domains) {
        try {
            this.logger.debug("Set " + domains.size() + " objects " + AppScreenshootDomain.OBJECT_KEY + " to cache");
            for (AppScreenshootDomain domain : domains) {
                putCacheObject(domain, domain.getAppId());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AppScreenshootDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    public void clearCache(long appId) {
        try {
            this.logger.debug("Clear objects " + AppScreenshootDomain.OBJECT_KEY + " in cache");
            List<AppScreenshootDomain> objects = getCacheObjects();
            for (AppScreenshootDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey(), appId);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppScreenshootDomain.OBJECT_KEY + " from Redis", ex);
        }
    }

    public void clearCache() {
        try {
            this.logger.debug("Clear objects " + AppScreenshootDomain.OBJECT_KEY + " in cache");
            List<AppScreenshootDomain> objects = getCacheObjects();
            for (AppScreenshootDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppScreenshootDomain.OBJECT_KEY + " from Redis", ex);
        }
    }

    private Collection<Object> getKeys(long appId) {
        logger.info("Get list version of app " + appId);
        Collection<Object> keys = new ArrayList<Object>();
        try {
            logger.debug("Load app screen shoot from cache");
            Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(AppVersionTable.KEY + ":" + appId);
            keys.addAll(appIds);
        } catch (NullPointerException ex) {
        }
        return keys;
    }
}
