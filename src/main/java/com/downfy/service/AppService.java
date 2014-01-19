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
import com.downfy.persistence.domain.application.AppDomain;
import com.downfy.persistence.repositories.application.AppRepository;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 */
@Service
public class AppService implements CacheDeveloperService<AppDomain> {

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

    public AppDomain findById(long appId, long developerId) {
        this.logger.debug("Find app " + appId + " in cache.");
        return getCacheObject(appId + "", developerId);
    }

    public List<AppDomain> findByDeveloper(long developerId) {
        List<AppDomain> apps = null;
        try {
            this.logger.debug("Find all apps of developer " + developerId + " in cache.");
            apps = getCacheObjects(developerId);
            if (apps.isEmpty()) {
                this.logger.debug("Find all apps of developer " + developerId + " in database.");
                apps = this.repository.findByDeveloper(developerId);
                if (!apps.isEmpty()) {
                    setCacheObjects(apps, developerId);
                } else {
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all apps of developer " + developerId + " error: " + ex, ex);
        }
        if (apps != null) {
            this.logger.debug("Total get " + apps.size() + " apps of developer " + developerId + ".");
        }
        return apps;
    }

    public boolean updateApp(AppDomain domain) {
        try {
            this.logger.debug("Update app " + domain.toString() + " to cache");
            putCacheObject(domain, domain.getCreater());
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't update app " + domain.toString(), ex);
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
     * @param appId Application ID
     * @return
     */
    public boolean publishApp(long appId, long developerId) {
        try {
            this.logger.debug("Publish app " + appId + " to cache");
            AppDomain app = getCacheObject(appId + "", developerId);
            if (app != null) {
                app.setStatus(AppStatus.PUBLISHED);
                putCacheObject(app, developerId);
                this.logger.debug("Publish app " + appId + " to database");
                this.repository.publish(appId);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't publish app " + appId, ex);
        }
        return false;
    }

    public boolean blockApp(long appId, long developerId) {
        try {
            this.logger.debug("Block app " + appId + " to cache");
            AppDomain app = getCacheObject(appId + "", developerId);
            if (app != null) {
                app.setStatus(AppStatus.BLOCKED);
                putCacheObject(app, developerId);
                this.logger.debug("Block app " + appId + " to database");
                this.repository.block(appId);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't block app " + appId, ex);
        }
        return false;
    }

    public boolean isExsit(long appId, long developerId) {
        AppDomain account = getCacheObject(appId + "", developerId);
        return account != null;
    }

    public long count(long developerId) {
        try {
            long count = countCacheObject(developerId);
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
            this.logger.debug("Save app " + domain.toString() + " to cache");
            putCacheObject(domain, domain.getCreater());
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't save app " + domain.toString(), ex);
        }
        return false;
    }

    public boolean delete(long appId, long developerId) {
        try {
            this.logger.debug("Delete app " + appId + " in cache.");
            removeCacheObject(appId + "", developerId);
            this.logger.debug("Delete app " + appId + " in database.");
            this.repository.delete(appId);
        } catch (Exception ex) {
            this.logger.error("Can't delete account " + appId, ex);
            return false;
        }
        return true;
    }

    @Override
    public void putCacheObject(AppDomain domain, long developerId) {
        try {
            this.redisTemplate.opsForHash().put(getObjectMessageKey(developerId), domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    @Override
    public AppDomain getCacheObject(String key, long developerId) {
        AppDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + getObjectMessageKey(developerId) + " in cache");
            domain = (AppDomain) redisTemplate.opsForHash().get(getObjectDeveloperKey(developerId), key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + getObjectMessageKey(developerId) + " in database");
                domain = repository.findById(Long.valueOf(key));
                if (domain == null) {
                    this.logger.debug("App " + key + " object " + getObjectMessageKey(developerId) + " not found");
                }
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    @Override
    public void removeCacheObject(String key, long developerId) {
        try {
            this.logger.debug("Remove key " + key + " object " + getObjectMessageKey(developerId) + " in cache");
            this.redisTemplate.opsForHash().delete(getObjectDeveloperKey(developerId), key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    @Override
    public List<AppDomain> getCacheObjects(long developerId) {
        List<AppDomain> apps = new ArrayList<AppDomain>();
        try {
            this.logger.debug("Get all objects " + getObjectMessageKey(developerId) + " in cache");
            for (Object user : redisTemplate.opsForHash().values(getObjectDeveloperKey(developerId))) {
                apps.add((AppDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + getObjectMessageKey(developerId) + " from Redis", ex);
        }
        return apps;
    }

    @Override
    public long countCacheObject(long developerId) {
        try {
            this.logger.debug("Count objects " + getObjectMessageKey(developerId) + " in cache");
            return redisTemplate.opsForHash().size(getObjectDeveloperKey(developerId));
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + getObjectMessageKey(developerId) + " from Redis", ex);
        }
        return 0;
    }

    @Override
    public void setCacheObjects(List<AppDomain> domains, long developerId) {
        try {
            this.logger.debug("Set " + domains.size() + " objects " + getObjectMessageKey(developerId) + " to cache");
            for (AppDomain domain : domains) {
                putCacheObject(domain, developerId);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + getObjectMessageKey(developerId) + " to Redis", ex);
        }
    }

    public void clearCache(long developerId) {
        try {
            this.logger.debug("Clear objects " + getObjectMessageKey(developerId) + " in cache");
            List<AppDomain> objects = getCacheObjects(developerId);
            for (AppDomain appDomain : objects) {
                removeCacheObject(appDomain.getKey(), developerId);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + getObjectMessageKey(developerId) + " from Redis", ex);
        }
    }

    private String getObjectDeveloperKey(long developerId) {
        return AppDomain.OBJECT_KEY + "-" + developerId;
    }

    private String getObjectMessageKey(long developerId) {
        return AppDomain.OBJECT_KEY + " of developer id " + developerId;
    }
}
