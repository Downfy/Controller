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

import com.downfy.common.AppStatus;
import com.downfy.persistence.domain.application.AppVersionDomain;
import com.downfy.persistence.repositories.application.AppVersionRepository;
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
 * AppDownloadService.java
 *
 * App download service
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  1-Dec-2013     tuanta      Create first time
 *  20-Dec-2013     tuanta      Add Developer<->App Version
 */
@Service
public class AppVersionService {

    private final Logger logger = LoggerFactory.getLogger(AppService.class);
    @Autowired
    AppVersionRepository repository;
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    private RedisTemplate<String, AppVersionDomain> appVersionRedisTemplate;
    private RedisTemplate<String, String> longRedisTemplate;

    public RedisTemplate<String, String> getLongRedisTemplate() {
        if (longRedisTemplate == null) {
            this.longRedisTemplate = new RedisTemplate<String, String>();
            this.longRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.longRedisTemplate.afterPropertiesSet();
        }
        return longRedisTemplate;
    }

    public RedisTemplate<String, AppVersionDomain> getAppVersionRedisTemplate() {
        if (appVersionRedisTemplate == null) {
            this.appVersionRedisTemplate = new RedisTemplate<String, AppVersionDomain>();
            this.appVersionRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.appVersionRedisTemplate.afterPropertiesSet();
        }
        return appVersionRedisTemplate;
    }

    public List<AppVersionDomain> findAll() {
        List<AppVersionDomain> apps = null;
        try {
            this.logger.info("Find all app version in cache.");
            apps = this.getCacheObjects();
            if (apps.isEmpty()) {
                this.logger.debug("Find all app version in database.");
                apps = this.repository.findAll();
                if (!apps.isEmpty()) {
                    this.setCacheObjects(apps);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all app version error: " + ex, ex);
        }
        if (apps != null) {
            this.logger.debug("Total get " + apps.size() + " apps.");
        }
        return apps;
    }

    public AppVersionDomain findById(long appId) {
        this.logger.info("Find app version " + appId + " in cache.");
        return getCacheObject(appId + "");
    }

    public List<AppVersionDomain> findByDeveloper(long developerId) {
        List<AppVersionDomain> apps = null;
        try {
            this.logger.info("Find all app version of developer " + developerId + " in cache.");
            apps = getCacheObjects(developerId);
            if (apps.isEmpty()) {
                this.logger.debug("Find all app version of developer " + developerId + " in database.");
                apps = this.repository.findByDeveloper(developerId);
                if (!apps.isEmpty()) {
                    setCacheObjects(apps);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all apapp versionps of developer " + developerId + " error: " + ex, ex);
        }
        if (apps != null) {
            this.logger.info("Total get " + apps.size() + " app version of developer " + developerId + ".");
        }
        return apps;
    }

    public boolean updateApp(AppVersionDomain domain, long developerId) {
        try {
            this.logger.info("Update app version " + domain.toString() + " to cache");
            putCacheObject(domain, developerId);
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't update app version " + domain.toString(), ex);
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
            this.logger.info("Publish app version " + key + " to cache");
            AppVersionDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppStatus.PUBLISHED);
                putCacheObject(app, app.getCreater());
                this.logger.debug("Publish app version " + key + " to database");
                this.repository.publish(key);
                this.logger.info("Publish success app version " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't publish app version " + key, ex);
        }
        return false;
    }

    public boolean blockApp(long key) {
        try {
            this.logger.info("Block app version " + key + " to cache");
            AppVersionDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppStatus.BLOCKED);
                putCacheObject(app, app.getCreater());
                this.logger.debug("Block app version " + key + " to database");
                this.repository.block(key);
                this.logger.info("Block success app version " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't block app version " + key, ex);
        }
        return false;
    }

    public boolean isExsit(long appId) {
        AppVersionDomain account = getCacheObject(appId + "");
        return account != null;
    }

    public long count(long developerId) {
        try {
            long count = countCacheObject(developerId);
            this.logger.info("Total " + count + " app version of developer " + developerId + " in cache.");
        } catch (Exception ex) {
            this.logger.error("Count total app version of developer " + developerId + " error.", ex);
        }
        return 0;
    }

    public long count() {
        try {
            long count = countCacheObject();
            this.logger.info("Total " + count + " app version in cache.");
        } catch (Exception ex) {
            this.logger.error("Count total app version error.", ex);
        }
        return 0;
    }

    public boolean save(AppVersionDomain domain) {
        try {
            this.logger.info("Save app version " + domain.toString() + " to database");
            this.repository.save(domain);
            this.logger.debug("Save app version " + domain.toString() + " to cache");
            putCacheObject(domain, domain.getCreater());
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't save app version " + domain.toString(), ex);
        }
        return false;
    }

    public boolean delete(long key, long developerId) {
        try {
            this.logger.info("Delete app version " + key + " in cache.");
            removeCacheObject(key + "", developerId);
            this.logger.debug("Delete app version " + key + " in database.");
            this.repository.delete(key);
            this.logger.info("Delete success app version " + key);
        } catch (Exception ex) {
            this.logger.error("Can't delete app version " + key, ex);
            return false;
        }
        return true;
    }

    private void putCacheObject(AppVersionDomain domain, long developerId) {
        try {
            this.getLongRedisTemplate().opsForSet().add(AppVersionTable.KEY + ":" + developerId, domain.getKey());
            this.getAppVersionRedisTemplate().opsForHash().put(AppVersionDomain.OBJECT_KEY, domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    private AppVersionDomain getCacheObject(String key) {
        AppVersionDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + AppVersionDomain.OBJECT_KEY + " in cache");
            domain = (AppVersionDomain) this.getAppVersionRedisTemplate().opsForHash().get(AppVersionDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + AppVersionDomain.OBJECT_KEY + " in database");
                domain = repository.findById(Long.valueOf(key));
                if (domain == null) {
                    this.logger.debug("App " + key + " object " + AppVersionDomain.OBJECT_KEY + " not found");
                }
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    private void removeCacheObject(String key, long developerId) {
        try {
            this.logger.debug("Remove key " + key + " object " + AppVersionDomain.OBJECT_KEY + " in cache");
            this.getAppVersionRedisTemplate().opsForHash().delete(AppVersionDomain.OBJECT_KEY, key);
            this.getLongRedisTemplate().opsForSet().pop(AppVersionTable.KEY + ":" + developerId);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private void removeCacheObject(String key) {
        try {
            this.logger.debug("Remove key " + key + " object " + AppVersionDomain.OBJECT_KEY + " in cache");
            this.getAppVersionRedisTemplate().opsForHash().delete(AppVersionDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private List<AppVersionDomain> getCacheObjects() {
        List<AppVersionDomain> apps = new ArrayList<AppVersionDomain>();
        try {
            this.logger.debug("Get all objects " + AppVersionDomain.OBJECT_KEY + " in cache");
            for (Object user : this.getAppVersionRedisTemplate().opsForHash().values(AppVersionDomain.OBJECT_KEY)) {
                apps.add((AppVersionDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppVersionDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private List<AppVersionDomain> getCacheObjects(long developerId) {
        Collection<Object> keys = getKeys(developerId);
        List<AppVersionDomain> apps = new ArrayList<AppVersionDomain>();
        try {
            this.logger.debug("Get all objects " + AppVersionDomain.OBJECT_KEY + " in cache");
            for (Object user : this.getAppVersionRedisTemplate().opsForHash().multiGet(AppVersionDomain.OBJECT_KEY, keys)) {
                apps.add((AppVersionDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppVersionDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private long countCacheObject() {
        try {
            this.logger.debug("Count objects " + AppVersionDomain.OBJECT_KEY + " in cache");
            return this.getAppVersionRedisTemplate().opsForHash().size(AppVersionDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppVersionDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private long countCacheObject(long developerId) {
        try {
            this.logger.debug("Count objects " + AppVersionDomain.OBJECT_KEY + " in cache");
            return this.getLongRedisTemplate().opsForSet().size(AppVersionTable.KEY + ":" + developerId);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppVersionDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private void setCacheObjects(List<AppVersionDomain> domains) {
        try {
            this.logger.debug("Set " + domains.size() + " objects " + AppVersionDomain.OBJECT_KEY + " to cache");
            for (AppVersionDomain domain : domains) {
                putCacheObject(domain, domain.getCreater());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AppVersionDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    public void clearCache(long developerId) {
        try {
            this.logger.debug("Clear objects " + AppVersionDomain.OBJECT_KEY + " in cache");
            List<AppVersionDomain> objects = getCacheObjects();
            for (AppVersionDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey(), developerId);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppVersionDomain.OBJECT_KEY + " from Redis", ex);
        }
    }

    public void clearCache() {
        try {
            this.logger.debug("Clear objects " + AppVersionDomain.OBJECT_KEY + " in cache");
            List<AppVersionDomain> objects = getCacheObjects();
            for (AppVersionDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppVersionDomain.OBJECT_KEY + " from Redis", ex);
        }
    }

    private Collection<Object> getKeys(long developerId) {
        Collection<Object> keys = new ArrayList<Object>();
        try {
            Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(developerId + "");
            keys.addAll(appIds);
        } catch (Exception ex) {
        }
        return keys;
    }
}
