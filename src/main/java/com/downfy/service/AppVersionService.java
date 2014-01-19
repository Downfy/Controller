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
public class AppVersionService implements CacheDeveloperService<AppVersionDomain> {

    private final Logger logger = LoggerFactory.getLogger(AppService.class);
    @Autowired
    AppVersionRepository repository;
    @Autowired
    private RedisTemplate<String, AppVersionDomain> redisTemplate;

    public void setRepository(AppVersionRepository repository) {
        this.repository = repository;
    }

    public void setRedisTemplate(RedisTemplate<String, AppVersionDomain> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public AppVersionDomain findById(long appId, long developerId) {
        this.logger.debug("Find app version " + appId + " of developer " + developerId + " in cache.");
        return getCacheObject(appId + "", developerId);
    }

    public List<AppVersionDomain> findByDeveloper(long developerId) {
        List<AppVersionDomain> apps = null;
        try {
            this.logger.debug("Find all app version of developer " + developerId + " in cache.");
            apps = getCacheObjects(developerId);
            if (apps.isEmpty()) {
                this.logger.debug("Find all app version of developer " + developerId + " in database.");
                apps = this.repository.findByDeveloper(developerId);
                if (!apps.isEmpty()) {
                    setCacheObjects(apps, developerId);
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
            this.logger.debug("Update app version " + domain.toString() + " to cache");
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
    public boolean publishApp(long key, long developerId) {
        try {
            this.logger.debug("Publish app version " + key + " of developer " + developerId + " to cache");
            AppVersionDomain app = getCacheObject(key + "", developerId);
            if (app != null) {
                app.setStatus(AppStatus.PUBLISHED);
                putCacheObject(app, developerId);
                this.logger.debug("Publish app version " + key + " of developer " + developerId + " to database");
                this.repository.publish(key);
                this.logger.info("Publish success app version " + key + " of developer " + developerId);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't publish app version " + key + " of developer " + developerId, ex);
        }
        return false;
    }

    public boolean blockApp(long key, long developerId) {
        try {
            this.logger.debug("Block app version " + key + " of developer " + developerId + " to cache");
            AppVersionDomain app = getCacheObject(key + "", developerId);
            if (app != null) {
                app.setStatus(AppStatus.BLOCKED);
                putCacheObject(app, developerId);
                this.logger.debug("Block app version " + key + " of developer " + developerId + " to database");
                this.repository.block(key);
                this.logger.info("Block success app version " + key + " of developer " + developerId);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't block app version " + key + " of developer " + developerId, ex);
        }
        return false;
    }

    public boolean isExsit(long appId, long developerId) {
        AppVersionDomain account = getCacheObject(appId + "", developerId);
        return account != null;
    }

    public long count(long developerId) {
        try {
            long count = countCacheObject(developerId);
            this.logger.debug("Total " + count + " app version in cache.");
        } catch (Exception ex) {
            this.logger.error("Count total app version error.", ex);
        }
        return 0;
    }

    public boolean save(AppVersionDomain domain) {
        try {
            this.logger.debug("Save app version " + domain.toString() + " to database");
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
            this.logger.debug("Delete app version " + key + " of developer " + developerId + " in cache.");
            removeCacheObject(key + "", developerId);
            this.logger.debug("Delete app version " + key + " of developer " + developerId + " in database.");
            this.repository.delete(key);
            this.logger.info("Delete success app version " + key + " of developer " + developerId);
            this.logger.debug(getCacheObjects(developerId).toString());
            this.logger.debug(redisTemplate.opsForHash().keys(getObjectDeveloperKey(developerId)).toString());
        } catch (Exception ex) {
            this.logger.error("Can't delete app version " + key, ex);
            return false;
        }
        return true;
    }

    @Override
    public void putCacheObject(AppVersionDomain domain, long developerId) {
        try {
            this.redisTemplate.opsForHash().put(getObjectDeveloperKey(developerId), domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    @Override
    public AppVersionDomain getCacheObject(String key, long developerId) {
        AppVersionDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + getObjectMessageKey(developerId) + " in cache");
            domain = (AppVersionDomain) redisTemplate.opsForHash().get(getObjectDeveloperKey(developerId), key);
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
    public List<AppVersionDomain> getCacheObjects(long developerId) {
        List<AppVersionDomain> apps = new ArrayList<AppVersionDomain>();
        try {
            this.logger.debug("Get all objects " + getObjectMessageKey(developerId) + " in cache");
            for (Object user : redisTemplate.opsForHash().values(getObjectDeveloperKey(developerId))) {
                apps.add((AppVersionDomain) user);
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
    public void setCacheObjects(List<AppVersionDomain> domains, long developerId) {
        try {
            this.logger.debug("Set " + domains.size() + " objects " + AppVersionDomain.OBJECT_KEY + " to cache");
            for (AppVersionDomain domain : domains) {
                putCacheObject(domain, developerId);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AppVersionDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    public void clearCache(long developerId) {
        try {
            this.logger.debug("Clear objects " + getObjectMessageKey(developerId) + " in cache");
            List<AppVersionDomain> objects = getCacheObjects(developerId);
            for (AppVersionDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey(), developerId);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + getObjectMessageKey(developerId) + " from Redis", ex);
        }
    }

    private String getObjectDeveloperKey(long developerId) {
        return AppVersionDomain.OBJECT_KEY + "-" + developerId;
    }

    private String getObjectMessageKey(long developerId) {
        return AppVersionDomain.OBJECT_KEY + " of developer id " + developerId;
    }
}
