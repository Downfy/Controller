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
import com.downfy.persistence.table.AppScreenshootTable;
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

    public RedisTemplate<String, AppScreenshootDomain> getAppScreenshootRedisTemplate() {
        if (appScreenshootRedisTemplate == null) {
            this.appScreenshootRedisTemplate = new RedisTemplate<String, AppScreenshootDomain>();
            this.appScreenshootRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.appScreenshootRedisTemplate.afterPropertiesSet();
        }
        return appScreenshootRedisTemplate;
    }

    public List<AppScreenshootDomain> findByApp(long appId) {
        List<AppScreenshootDomain> apps = new ArrayList<AppScreenshootDomain>();
        try {
            apps = getCacheObjects(appId);
            if (apps.isEmpty()) {
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
            AppScreenshootDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PUBLISHED);
                putCacheObject(app, app.getAppId());
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
            AppScreenshootDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PENDING);
                putCacheObject(app, app.getAppId());
                this.repository.approve(key);
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
            AppScreenshootDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.BLOCKED);
                putCacheObject(app, app.getAppId());
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
            removeCacheObject(key + "", appId);
            this.repository.delete(key);
            AppScreenshootDomain domain = getCacheObject(key + "");
            if (domain != null) {
                FileUtils.deleteQuietly(new File(domain.getAppScreenShoot()));
            }
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
            this.getLongRedisTemplate().opsForSet().add(AppScreenshootTable.KEY + ":" + appId, domain.getKey());
            this.getAppScreenshootRedisTemplate().opsForHash().put(AppScreenshootDomain.OBJECT_KEY, domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    private AppScreenshootDomain getCacheObject(String key) {
        AppScreenshootDomain domain = null;
        try {
            domain = (AppScreenshootDomain) this.getAppScreenshootRedisTemplate().opsForHash().get(AppScreenshootDomain.OBJECT_KEY, key);
            if (domain == null) {
                domain = repository.findById(Long.valueOf(key));
                if (domain == null) {
                    this.logger.debug("App " + key + " object " + AppScreenshootDomain.OBJECT_KEY + " not found");
                }
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    private void removeCacheObject(String key, long appId) {
        try {
            this.logger.debug("Remove key " + key + " object " + AppScreenshootDomain.OBJECT_KEY + " in cache");
            this.getAppScreenshootRedisTemplate().opsForHash().delete(AppScreenshootDomain.OBJECT_KEY, key);
            this.getLongRedisTemplate().opsForSet().remove(AppScreenshootTable.KEY + ":" + appId, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private List<AppScreenshootDomain> getCacheObjects() {
        List<AppScreenshootDomain> apps = new ArrayList<AppScreenshootDomain>();
        try {
            for (Object user : this.getAppScreenshootRedisTemplate().opsForHash().values(AppScreenshootDomain.OBJECT_KEY)) {
                apps.add((AppScreenshootDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppScreenshootDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private List<AppScreenshootDomain> getCacheObjects(long appId) {
        Collection<Object> keys = getKeys(appId);
        List<AppScreenshootDomain> apps = new ArrayList<AppScreenshootDomain>();
        try {
            for (Object user : this.getAppScreenshootRedisTemplate().opsForHash().multiGet(AppScreenshootDomain.OBJECT_KEY, keys)) {
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
            return this.getAppScreenshootRedisTemplate().opsForHash().size(AppScreenshootDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppScreenshootDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private long countCacheObject(long appId) {
        try {
            this.logger.debug("Count objects " + AppScreenshootDomain.OBJECT_KEY + " in cache");
            return this.getLongRedisTemplate().opsForSet().size(AppScreenshootTable.KEY + ":" + appId);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppScreenshootDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private void setCacheObjects(List<AppScreenshootDomain> domains) {
        try {
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

    private Collection<Object> getKeys(long appId) {
        Collection<Object> keys = new ArrayList<Object>();
        try {
            Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(AppScreenshootTable.KEY + ":" + appId);
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
