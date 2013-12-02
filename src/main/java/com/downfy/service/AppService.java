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

import com.downfy.persistence.domain.AppDomain;
import com.downfy.persistence.repositories.AppRepository;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;

/*
 * AppDownloadService.java
 * 
 * App download service
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  1-Dec-2013     tuanta      Create first time
 */
public class AppService implements CacheService<AppDomain> {

    private final Logger logger = LoggerFactory.getLogger(AppService.class);
    @Autowired
    AppRepository repository;
    @Autowired
    private RedisTemplate<String, AppDomain> redisTemplate;

    public void setRepository(AppRepository repository) {
        this.repository = repository;
    }

    public void setRedisTemplate(RedisTemplate<String, AppDomain> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<AppDomain> findAll() {
        List<AppDomain> apps = null;
        try {
            this.logger.debug("Find all apps in cache.");
            apps = getCacheObjects();
            if (apps.isEmpty()) {
                this.logger.debug("Find all apps in database.");
                apps = this.repository.findAll();
                setCacheObjects(apps);
            }
        } catch (Exception ex) {
            this.logger.error("Find all apps error: " + ex, ex);
        }
        if (apps != null && !apps.isEmpty()) {
            this.logger.debug("Total get " + apps.size() + " apps.");
            return apps;
        }
        return new ArrayList<AppDomain>();
    }

    public AppDomain findById(long appId) {
        this.logger.debug("Find app " + appId + " in cache.");
        return getCacheObject(appId + "");
    }

    public List<AppDomain> findByDeveloper(long developerId) {
        List<AppDomain> apps = null;
        try {
            this.logger.debug("Find all apps of developer " + developerId + " in cache.");
            apps = getCacheObjects();
            if (apps.isEmpty()) {
                this.logger.debug("Find all apps of developer " + developerId + " in database.");
                apps = this.repository.findByDeveloper(developerId);
                if (!apps.isEmpty()) {
                    setCacheObjects(apps);
                } else {
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all apps of developer " + developerId + " error: " + ex, ex);
        }
        this.logger.debug("Total get " + apps.size() + " apps of developer " + developerId + ".");
        return apps;
    }

    public void updateApp(AppDomain domain) {
        putCacheObject(domain);
    }

    /**
     * Step 1: Update status app in cache Step 2: Update information app in db
     * Step 3: Move app in storage tmp version to origin version
     *
     * @param appId Application ID
     * @return
     */
    public boolean publishApp(long appId) {
        return false;
    }

    public boolean isExsit(long appId) {
        AppDomain account = getCacheObject(appId + "");
        return account != null;
    }

    public long count() {
        try {
            long count = countCacheObject();
            this.logger.debug("Total " + count + " apps in cache.");
        } catch (Exception ex) {
            this.logger.error("Count total apps error.", ex);
        }
        return 0;
    }
    
    public boolean save(AppDomain domain) {
        try {
            this.logger.debug("Save app " + domain.toString() + " to database");
            this.repository.save(domain);
            putCacheObject(domain);
            this.logger.debug("Save app " + domain.toString() + " to cache");
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't save account " + domain.toString(), ex);
        }
        return false;
    }

    public boolean delete(long appId) {
        try {
            this.logger.debug("Delete app " + appId + " in cache.");
            removeCacheObject(appId + "");
            this.logger.debug("Delete app " + appId + " in database.");
            this.repository.delete(appId);
        } catch (Exception ex) {
            this.logger.error("Can't delete account " + appId, ex);
            return false;
        }
        return true;
    }

    @Override
    public void putCacheObject(AppDomain domain) {
        try {
            this.redisTemplate.opsForHash().put(domain.getObjectKey(), domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    @Override
    public AppDomain getCacheObject(String key) {
        AppDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + AppDomain.OBJECT_KEY + " in cache");
            domain = (AppDomain) redisTemplate.opsForHash().get(AppDomain.OBJECT_KEY, key);
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

    @Override
    public void removeCacheObject(String key) {
        try {
            this.logger.debug("Remove key " + key + " object " + AppDomain.OBJECT_KEY + " in cache");
            this.redisTemplate.opsForHash().delete(AppDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    @Override
    public List<AppDomain> getCacheObjects() {
        List<AppDomain> apps = new ArrayList<AppDomain>();
        try {
            this.logger.debug("Get all objects " + AppDomain.OBJECT_KEY + " in cache");
            for (Object user : redisTemplate.opsForHash().values(AppDomain.OBJECT_KEY)) {
                apps.add((AppDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    @Override
    public List<AppDomain> getCacheLimitObjects(int start, int end) {
        List<AppDomain> users = new ArrayList<AppDomain>();
        try {
            this.logger.debug("Get objects from " + start + " to " + end + " " + AppDomain.OBJECT_KEY + " in cache");
            long count = count();
            if (start > count) {
                this.logger.debug("Can't get objects outside list data.");
                return users;
            }
            List users_ = redisTemplate.opsForHash().values(AppDomain.OBJECT_KEY);
            for (int i = start; i < end; i++) {
                if (end < count) {
                    users.add((AppDomain) users_.get(i));
                }
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get objects from " + start + " to " + end + " " + AppDomain.OBJECT_KEY + " from Redis", ex);
        }
        return users;
    }

    @Override
    public long countCacheObject() {
        try {
            this.logger.debug("Count objects " + AppDomain.OBJECT_KEY + " in cache");
            return redisTemplate.opsForHash().size(AppDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    @Override
    public void setCacheObjects(List<AppDomain> domains) {
        try {
            this.logger.debug("Set " + domains.size() + " objects " + AppDomain.OBJECT_KEY + " to cache");
            for (AppDomain domain : domains) {
                putCacheObject(domain);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AppDomain.OBJECT_KEY + " to Redis", ex);
        }
    }
}
