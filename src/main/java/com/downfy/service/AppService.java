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
package com.downfy.service;

import com.downfy.common.AppCommon;
import com.downfy.persistence.domain.application.AppDomain;
import com.downfy.persistence.repositories.application.AppRepository;
import com.downfy.persistence.table.AppTable;
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
 * AppDownloadService.java
 *
 * App download service
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  1-Dec-2013     tuanta      Create first time
 *  21-Dec-2013    tuanta      Add Developer <--> App
 */
@Service
public class AppService {

    private final Logger logger = LoggerFactory.getLogger(AppService.class);
    @Autowired
    AppRepository repository;
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    private RedisTemplate<String, AppDomain> appVersionRedisTemplate;
    private RedisTemplate<String, String> longRedisTemplate;

    public RedisTemplate<String, String> getLongRedisTemplate() {
        if (longRedisTemplate == null) {
            this.longRedisTemplate = new RedisTemplate<String, String>();
            this.longRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.longRedisTemplate.afterPropertiesSet();
        }
        return longRedisTemplate;
    }

    public RedisTemplate<String, AppDomain> getAppVersionRedisTemplate() {
        if (appVersionRedisTemplate == null) {
            this.appVersionRedisTemplate = new RedisTemplate<String, AppDomain>();
            this.appVersionRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.appVersionRedisTemplate.afterPropertiesSet();
        }
        return appVersionRedisTemplate;
    }

    public List<AppDomain> findAll() {
        List<AppDomain> apps = null;
        try {
            this.logger.info("Find all app in cache.");
            apps = this.getCacheObjects();
            if (apps.isEmpty()) {
                this.logger.debug("Find all app in database.");
                apps = this.repository.findAll();
                if (!apps.isEmpty()) {
                    this.setCacheObjects(apps);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all app error: " + ex, ex);
        }
        if (apps != null) {
            this.logger.debug("Total get " + apps.size() + " apps.");
        }
        return apps;
    }

    public AppDomain findById(long appId) {
        this.logger.info("Find app " + appId + " in cache.");
        return getCacheObject(appId + "");
    }

    public List<AppDomain> findByDeveloper(long developerId) {
        List<AppDomain> apps = null;
        try {
            this.logger.info("Find all app of developer " + developerId + " in cache.");
            apps = getCacheObjects(developerId);
            if (apps.isEmpty()) {
                this.logger.debug("Find all app of developer " + developerId + " in database.");
                apps = this.repository.findByDeveloper(developerId);
                if (!apps.isEmpty()) {
                    setCacheObjects(apps);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all app of developer " + developerId + " error: " + ex, ex);
        }
        if (apps != null) {
            this.logger.info("Total get " + apps.size() + " app of developer " + developerId + ".");
        }
        return apps;
    }

    public boolean updateApp(AppDomain domain, long developerId) {
        try {
            this.logger.info("Update app " + domain + " to cache");
            putCacheObject(domain, developerId);
            this.logger.debug("Update app " + domain + " to database");
            repository.updateAppInfo(domain);
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't update app " + domain, ex);
        }
        return false;
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
    public boolean publishApp(long key) {
        try {
            this.logger.info("Publish app " + key + " to cache");
            AppDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PUBLISHED);
                putCacheObject(app, app.getCreater());
                this.logger.debug("Publish app " + key + " to database");
                this.repository.publish(key);
                this.logger.info("Publish success app " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't publish app " + key, ex);
        }
        return false;
    }

    public boolean blockApp(long key) {
        try {
            this.logger.info("Block app " + key + " to cache");
            AppDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.BLOCKED);
                putCacheObject(app, app.getCreater());
                this.logger.debug("Block app " + key + " to database");
                this.repository.block(key);
                this.logger.info("Block success app " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't block app " + key, ex);
        }
        return false;
    }

    public boolean isExsit(long appId) {
        AppDomain account = getCacheObject(appId + "");
        return account != null;
    }

    public long count(long developerId) {
        try {
            long count = countCacheObject(developerId);
            this.logger.info("Total " + count + " app of developer " + developerId + " in cache.");
        } catch (Exception ex) {
            this.logger.error("Count total app of developer " + developerId + " error.", ex);
        }
        return 0;
    }

    public long count() {
        try {
            long count = countCacheObject();
            this.logger.info("Total " + count + " app in cache.");
        } catch (Exception ex) {
            this.logger.error("Count total app error.", ex);
        }
        return 0;
    }

    public boolean save(AppDomain domain) {
        try {
            this.logger.info("Save app " + domain + " to database");
            this.repository.save(domain);
            this.logger.debug("Save app " + domain + " to cache");
            putCacheObject(domain, domain.getCreater());
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't save app " + domain, ex);
        }
        return false;
    }

    public boolean delete(long key, long developerId) {
        try {
            this.logger.info("Delete app " + key + " in cache.");
            removeCacheObject(key + "", developerId);
            this.logger.debug("Delete app " + key + " in database.");
            this.repository.delete(key);
            this.logger.info("Delete success app " + key);
        } catch (Exception ex) {
            this.logger.error("Can't delete app " + key, ex);
            return false;
        }
        return true;
    }

    private void putCacheObject(AppDomain domain, long developerId) {
        try {
            this.logger.debug("Put app " + domain + " to cache");
            this.getLongRedisTemplate().opsForSet().add(AppTable.KEY + ":" + developerId, domain.getKey());
            this.getAppVersionRedisTemplate().opsForHash().put(AppDomain.OBJECT_KEY, domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    private AppDomain getCacheObject(String key) {
        AppDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + AppDomain.OBJECT_KEY + " in cache");
            domain = (AppDomain) this.getAppVersionRedisTemplate().opsForHash().get(AppDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + AppDomain.OBJECT_KEY + " in database");
                domain = repository.findById(Long.valueOf(key));
                if (domain == null) {
                    this.logger.debug("App " + key + " object " + AppDomain.OBJECT_KEY + " not found");
                }
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    private void removeCacheObject(String key, long developerId) {
        try {
            this.logger.debug("Remove key " + key + " object " + AppDomain.OBJECT_KEY + " in cache");
            this.getAppVersionRedisTemplate().opsForHash().delete(AppDomain.OBJECT_KEY, key);
            this.getLongRedisTemplate().opsForSet().pop(AppTable.KEY + ":" + developerId);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private void removeCacheObject(String key) {
        try {
            this.logger.debug("Remove key " + key + " object " + AppDomain.OBJECT_KEY + " in cache");
            this.getAppVersionRedisTemplate().opsForHash().delete(AppDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private List<AppDomain> getCacheObjects() {
        List<AppDomain> apps = new ArrayList<AppDomain>();
        try {
            this.logger.debug("Get all objects " + AppDomain.OBJECT_KEY + " in cache");
            for (Object user : this.getAppVersionRedisTemplate().opsForHash().values(AppDomain.OBJECT_KEY)) {
                apps.add((AppDomain) user);
                logger.debug("==> Get object " + ((AppDomain) user).toString());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private List<AppDomain> getCacheObjects(long developerId) {
        Collection<Object> keys = getKeys(developerId);
        logger.debug("All app from developer id " + developerId + " ==> " + keys);
        List<AppDomain> apps = new ArrayList<AppDomain>();
        try {
            this.logger.debug("Get all objects " + AppDomain.OBJECT_KEY + " in cache");
            for (Object user : this.getAppVersionRedisTemplate().opsForHash().multiGet(AppDomain.OBJECT_KEY, keys)) {
                apps.add((AppDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private long countCacheObject() {
        try {
            this.logger.debug("Count objects " + AppDomain.OBJECT_KEY + " in cache");
            return this.getAppVersionRedisTemplate().opsForHash().size(AppDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private long countCacheObject(long developerId) {
        try {
            this.logger.debug("Count objects " + AppDomain.OBJECT_KEY + " in cache");
            return this.getLongRedisTemplate().opsForSet().size(AppTable.KEY + ":" + developerId);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private void setCacheObjects(List<AppDomain> domains) {
        try {
            this.logger.debug("Set " + domains.size() + " objects " + AppDomain.OBJECT_KEY + " to cache");
            for (AppDomain domain : domains) {
                putCacheObject(domain, domain.getCreater());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AppDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    public void clearCache(long developerId) {
        try {
            this.logger.debug("Clear objects " + AppDomain.OBJECT_KEY + " in cache");
            List<AppDomain> objects = getCacheObjects();
            for (AppDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey(), developerId);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppDomain.OBJECT_KEY + " from Redis", ex);
        }
    }

    public void clearCache() {
        try {
            this.logger.debug("Clear objects " + AppDomain.OBJECT_KEY + " in cache");
            List<AppDomain> objects = getCacheObjects();
            for (AppDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppDomain.OBJECT_KEY + " from Redis", ex);
        }
    }

    private Collection<Object> getKeys(long developerId) {
        Collection<Object> keys = new ArrayList<Object>();
        try {
            Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(AppTable.KEY + ":" + developerId);
            keys.addAll(appIds);
        } catch (NullPointerException ex) {
        }
        return keys;
    }
}
