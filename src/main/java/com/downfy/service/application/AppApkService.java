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
import com.downfy.persistence.domain.application.AppApkDomain;
import com.downfy.persistence.domain.application.AppVersionDomain;
import com.downfy.persistence.repositories.application.AppVersionRepository;
import com.downfy.persistence.table.AppApkTable;
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
 * AppApkService.java
 *
 * App apk service
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  1-Dec-2013     tuanta      Create first time
 *  20-Dec-2013     tuanta      Add Developer<->App Version
 */
@Service
public class AppApkService {

    private final Logger logger = LoggerFactory.getLogger(AppApkService.class);
    @Autowired
    AppVersionRepository repository;
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    private RedisTemplate<String, AppApkDomain> appApkRedisTemplate;
    private RedisTemplate<String, String> longRedisTemplate;

    public RedisTemplate<String, String> getLongRedisTemplate() {
        if (longRedisTemplate == null) {
            this.longRedisTemplate = new RedisTemplate<String, String>();
            this.longRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.longRedisTemplate.afterPropertiesSet();
        }
        return longRedisTemplate;
    }

    public RedisTemplate<String, AppApkDomain> getAppApkRedisTemplate() {
        if (appApkRedisTemplate == null) {
            this.appApkRedisTemplate = new RedisTemplate<String, AppApkDomain>();
            this.appApkRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.appApkRedisTemplate.afterPropertiesSet();
        }
        return appApkRedisTemplate;
    }

    public AppApkDomain findById(String appId) {
        return getCacheObject(appId);
    }

    public List<AppApkDomain> findByApp(long appId) {
        List<AppApkDomain> apps = new ArrayList<AppApkDomain>();
        try {
            apps = getCacheObjects(appId);
            if (apps.isEmpty()) {
                List<AppVersionDomain> apps_ = this.repository.findByApp(appId);
                for (AppVersionDomain appVersionDomain : apps_) {
                    AppApkDomain apkDomain = new AppApkDomain();
                    apkDomain.fromAppVersion(appVersionDomain);
                    apps.add(apkDomain);
                }
                if (!apps.isEmpty()) {
                    setCacheObjects(apps);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all app apk of app " + appId + " error: " + ex, ex);
        }
        this.logger.info("Total get " + apps.size() + " app apk of app " + appId + ".");
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
            AppApkDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PUBLISHED);
                putCacheObject(app, app.getAppId());
                this.repository.publish(key);
                this.logger.info("Publish success app apk " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't publish app apk " + key, ex);
        }
        return false;
    }

    public boolean approve(long key) {
        try {
            AppApkDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PENDING);
                putCacheObject(app, app.getAppId());
                this.repository.publish(key);
                this.logger.info("Approve success app apk " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't approve app apk " + key, ex);
        }
        return false;
    }

    public boolean block(long key) {
        try {
            AppApkDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.BLOCKED);
                putCacheObject(app, app.getAppId());
                this.repository.block(key);
                this.logger.info("Block success app apk " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't block app apk " + key, ex);
        }
        return false;
    }

    public boolean isExsit(long appId) {
        AppApkDomain account = getCacheObject(appId + "");
        return account != null;
    }

    public long count(long appId) {
        try {
            long count = countCacheObject(appId);
            this.logger.info("Total " + count + " app apk of app " + appId);
        } catch (Exception ex) {
            this.logger.error("Count total app apk of app " + appId + " error.", ex);
        }
        return 0;
    }

    public long count() {
        try {
            long count = countCacheObject();
            this.logger.info("Total " + count + " app apk");
        } catch (Exception ex) {
            this.logger.error("Count total app apk error.", ex);
        }
        return 0;
    }

    public boolean save(AppApkDomain domain) {
        try {
            putCacheObject(domain, domain.getAppId());
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't save app apk " + domain, ex);
        }

        return false;
    }

    public boolean delete(long key, long appId) {
        try {
            removeCacheObject(key + "", appId);
            this.repository.delete(key);
            AppApkDomain domain = getCacheObject(key + "");
            if (domain != null) {
                FileUtils.deleteQuietly(new File(domain.getAppPath()));
            }
            this.logger.info("Delete success app apk " + key);
        } catch (Exception ex) {
            this.logger.error("Can't delete app apk " + key, ex);
            return false;
        }
        return true;
    }

    private void putCacheObject(AppApkDomain domain, long appId) {
        try {
            this.logger.debug("Put to cache " + domain);
            this.getLongRedisTemplate().opsForSet().add(AppApkTable.KEY + ":" + appId, domain.getKey());
            this.getAppApkRedisTemplate().opsForHash().put(AppApkDomain.OBJECT_KEY, domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    private AppApkDomain getCacheObject(String key) {
        AppApkDomain domain = null;
        try {
            domain = (AppApkDomain) this.getAppApkRedisTemplate().opsForHash().get(AppApkDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("App " + key + " object " + AppApkDomain.OBJECT_KEY + " not found");
            }
        } catch (NumberFormatException ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    private void removeCacheObject(String key, long appId) {
        try {
            this.logger.debug("Remove key " + key + " object " + AppApkDomain.OBJECT_KEY + " in cache");
            this.getAppApkRedisTemplate().opsForHash().delete(AppApkDomain.OBJECT_KEY, key);
            this.getLongRedisTemplate().opsForSet().remove(AppApkTable.KEY + ":" + appId, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private List<AppApkDomain> getCacheObjects() {
        List<AppApkDomain> apps = new ArrayList<AppApkDomain>();
        try {
            for (Object user : this.getAppApkRedisTemplate().opsForHash().values(AppApkDomain.OBJECT_KEY)) {
                apps.add((AppApkDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppApkDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private List<AppApkDomain> getCacheObjects(long appId) {
        Collection<Object> keys = getKeys(appId);
        List<AppApkDomain> apps = new ArrayList<AppApkDomain>();
        try {
            for (Object user : this.getAppApkRedisTemplate().opsForHash().multiGet(AppApkDomain.OBJECT_KEY, keys)) {
                apps.add((AppApkDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppApkDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private long countCacheObject() {
        try {
            this.logger.debug("Count objects " + AppApkDomain.OBJECT_KEY + " in cache");
            return this.getAppApkRedisTemplate().opsForHash().size(AppApkDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppApkDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private long countCacheObject(long appId) {
        try {
            this.logger.debug("Count objects " + AppApkDomain.OBJECT_KEY + " in cache");
            return this.getLongRedisTemplate().opsForSet().size(AppApkTable.KEY + ":" + appId);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppApkDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private void setCacheObjects(List<AppApkDomain> domains) {
        try {
            for (AppApkDomain domain : domains) {
                putCacheObject(domain, domain.getAppId());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AppApkDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    public void clearCache(long appId) {
        try {
            this.logger.debug("Clear objects " + AppApkDomain.OBJECT_KEY + " in cache");
            List<AppApkDomain> objects = getCacheObjects();
            for (AppApkDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey(), appId);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppApkDomain.OBJECT_KEY + " from Redis", ex);
        }
    }

    private Collection<Object> getKeys(long appId) {
        Collection<Object> keys = new ArrayList<Object>();
        try {
            Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(AppApkTable.KEY + ":" + appId);
            List<String> myList = new ArrayList<String>(appIds);
            Collections.sort(myList, new Comparator<String>() {
                @Override
                public int compare(String id01, String id02) {
                    return id01.compareTo(id02);
                }
            });
            logger.debug("Get keys from app " + appId + " ==> " + appIds);
            keys.addAll(myList);
        } catch (NullPointerException ex) {
        }
        return keys;
    }
}
