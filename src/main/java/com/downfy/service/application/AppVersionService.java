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
import com.downfy.common.Utils;
import com.downfy.persistence.domain.application.AppVersionDomain;
import com.downfy.persistence.repositories.application.AppVersionRepository;
import com.downfy.persistence.table.AppVersionTable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/*
 * AppVersionService.java
 *
 * App version service
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  1-Dec-2013     tuanta      Create first time
 *  20-Dec-2013     tuanta      Add Developer<->App Version
 */
@Service
public class AppVersionService {

    private final Logger logger = LoggerFactory.getLogger(AppVersionService.class);
    @Autowired
    AppVersionRepository repository;
    @Autowired
    ServletContext context;
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
            apps = this.getCacheObjects();
            if (apps.isEmpty()) {
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
        return getCacheObject(appId + "");
    }

    public List<AppVersionDomain> findByApp(long appId) {
        List<AppVersionDomain> apps = new ArrayList<AppVersionDomain>();
        try {
            apps = getCacheObjects(appId);
            if (apps.isEmpty()) {
                apps = this.repository.findByApp(appId);
                for (AppVersionDomain appVersionDomain : apps) {
                    File f = new File(Utils.getFolderData(context, "data", appVersionDomain.getAppPath()));
                    if (f.exists()) {
                        setCacheObject(appVersionDomain);
                    } else {
                        this.delete(appVersionDomain.getId(), appVersionDomain.getAppId());
                    }
                }
            }
            apps = getCacheObjects(appId);
        } catch (Exception ex) {
            this.logger.error("Find all apapp versionps of app " + appId + " error: " + ex, ex);
        }
        this.logger.info("Total get " + apps.size() + " app version of app " + appId + ".");
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
            AppVersionDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PUBLISHED);
                putCacheObject(app, app.getAppId());
                this.repository.publish(key);
                this.logger.info("Request publish success app version " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't Request publish app version " + key, ex);
        }
        return false;
    }

    public boolean approve(long key) {
        try {
            AppVersionDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PENDING);
                putCacheObject(app, app.getAppId());
                this.repository.approve(key);
                this.logger.info("Request approve success app version " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't requets approve app version " + key, ex);
        }
        return false;
    }

    public boolean block(long key) {
        try {
            AppVersionDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.BLOCKED);
                putCacheObject(app, app.getAppId());
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

    public boolean isExsit(long appId, String version) {
        Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(AppVersionTable.KEY + ":" + appId + ":" + version);
        return appIds != null && !appIds.isEmpty();
    }

    public long count(long appId) {
        try {
            long count = countCacheObject(appId);
            this.logger.info("Total " + count + " app version of app " + appId);
        } catch (Exception ex) {
            this.logger.error("Count total app version of app " + appId + " error.", ex);
        }
        return 0;
    }

    public long count() {
        try {
            long count = countCacheObject();
            this.logger.info("Total " + count + " app version");
        } catch (Exception ex) {
            this.logger.error("Count total app version error.", ex);
        }
        return 0;
    }

    public boolean save(AppVersionDomain domain) {
        try {
            this.repository.save(domain);
            putCacheObject(domain, domain.getAppId());
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't save app version " + domain, ex);
        }

        return false;
    }

    public boolean delete(long key, long appId) {
        try {
            removeCacheObject(key + "", appId);
            this.repository.delete(key);
            this.logger.info("Delete success app version " + key);
        } catch (Exception ex) {
            this.logger.error("Can't delete app version " + key, ex);
            return false;
        }
        return true;
    }

    private void putCacheObject(AppVersionDomain domain, long appId) {
        try {
            this.logger.debug("Put to cache " + domain);
            this.getLongRedisTemplate().opsForSet().add(AppVersionTable.KEY + ":" + appId, domain.getKey());
            this.getLongRedisTemplate().opsForSet().add(AppVersionTable.KEY + ":" + appId + ":" + domain.getAppVersion(), domain.getKey());
            this.getAppVersionRedisTemplate().opsForHash().put(AppVersionDomain.OBJECT_KEY, domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    private AppVersionDomain getCacheObject(String key) {
        AppVersionDomain domain = null;
        try {
            domain = (AppVersionDomain) this.getAppVersionRedisTemplate().opsForHash().get(AppVersionDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + AppVersionDomain.OBJECT_KEY + " in database");
                domain = repository.findById(Long.valueOf(key));
                if (domain == null) {
                    this.logger.debug("App " + key + " object " + AppVersionDomain.OBJECT_KEY + " not found");
                }
            }
        } catch (NumberFormatException ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    private void removeCacheObject(String key, long appId) {
        try {
            AppVersionDomain versionDomain = getCacheObject(key);
            if (versionDomain != null) {
                this.getAppVersionRedisTemplate().opsForHash().delete(AppVersionDomain.OBJECT_KEY, key);
                this.getLongRedisTemplate().opsForSet().remove(AppVersionTable.KEY + ":" + appId, key);
                this.getLongRedisTemplate().opsForSet().remove(AppVersionTable.KEY + ":" + appId + ":" + versionDomain.getAppVersion(), key);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private void removeCacheObject(String key) {
        try {
            this.getAppVersionRedisTemplate().opsForHash().delete(AppVersionDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private List<AppVersionDomain> getCacheObjects() {
        List<AppVersionDomain> apps = new ArrayList<AppVersionDomain>();
        try {
            for (Object user : this.getAppVersionRedisTemplate().opsForHash().values(AppVersionDomain.OBJECT_KEY)) {
                apps.add((AppVersionDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppVersionDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private List<AppVersionDomain> getCacheObjects(long appId) {
        Collection<Object> keys = getKeys(appId);
        logger.debug("Get list app version of app " + appId + " ==> " + keys);
        List<AppVersionDomain> apps = new ArrayList<AppVersionDomain>();
        try {
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
            return this.getAppVersionRedisTemplate().opsForHash().size(AppVersionDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppVersionDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private long countCacheObject(long appId) {
        try {
            return this.getLongRedisTemplate().opsForSet().size(AppVersionTable.KEY + ":" + appId);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppVersionDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private void setCacheObjects(List<AppVersionDomain> domains) {
        try {
            for (AppVersionDomain domain : domains) {
                putCacheObject(domain, domain.getAppId());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AppVersionDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    private void setCacheObject(AppVersionDomain domain) {
        try {
            putCacheObject(domain, domain.getAppId());
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AppVersionDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    public void clearCache(long appId) {
        try {
            this.logger.debug("Clear objects " + AppVersionDomain.OBJECT_KEY + " in cache");
            List<AppVersionDomain> objects = getCacheObjects();
            for (AppVersionDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey(), appId);
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

    private Collection<Object> getKeys(long appId) {
        Collection<Object> keys = new ArrayList<Object>();
        try {
            Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(AppVersionTable.KEY + ":" + appId);
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
}
