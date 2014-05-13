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
package com.downfy.service.application.news;

import com.downfy.common.AppCommon;
import com.downfy.persistence.domain.news.AppNewsDomain;
import com.downfy.persistence.repositories.news.AppNewsRepository;
import com.downfy.persistence.table.AppVersionTable;
import java.util.ArrayList;
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
public class AppNewsService {

    private final Logger logger = LoggerFactory.getLogger(AppNewsService.class);
    @Autowired
    AppNewsRepository repository;
    @Autowired
    ServletContext context;
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    private RedisTemplate<String, AppNewsDomain> appNewsRedisTemplate;
    private RedisTemplate<String, String> longRedisTemplate;

    public RedisTemplate<String, String> getLongRedisTemplate() {
        if (longRedisTemplate == null) {
            this.longRedisTemplate = new RedisTemplate<String, String>();
            this.longRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.longRedisTemplate.afterPropertiesSet();
        }
        return longRedisTemplate;
    }

    public RedisTemplate<String, AppNewsDomain> getAppVersionRedisTemplate() {
        if (appNewsRedisTemplate == null) {
            this.appNewsRedisTemplate = new RedisTemplate<String, AppNewsDomain>();
            this.appNewsRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.appNewsRedisTemplate.afterPropertiesSet();
        }
        return appNewsRedisTemplate;
    }

    public List<AppNewsDomain> findAll() {
        List<AppNewsDomain> apps = null;
        try {
            apps = this.getCacheObjects();
            if (apps.isEmpty()) {
                apps = this.repository.findAll();
                if (!apps.isEmpty()) {
                    this.setCacheObjects(apps);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all app news error: " + ex, ex);
        }
        if (apps != null) {
            this.logger.debug("Total get " + apps.size() + " apps.");
        }
        return apps;
    }

    public AppNewsDomain findById(long appId) {
        return getCacheObject(appId + "");
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
            AppNewsDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PUBLISHED);
                putCacheObject(app);
                this.repository.publish(key);
                this.logger.info("Request publish success app news " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't Request publish app news " + key, ex);
        }
        return false;
    }

    public boolean approve(long key) {
        try {
            AppNewsDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PENDING);
                putCacheObject(app);
                this.repository.approve(key);
                this.logger.info("Request approve success app news " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't requets approve app news " + key, ex);
        }
        return false;
    }

    public boolean block(long key) {
        try {
            AppNewsDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.BLOCKED);
                putCacheObject(app);
                this.logger.debug("Block app news " + key + " to database");
                this.repository.block(key);
                this.logger.info("Block success app news " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't block app news " + key, ex);
        }
        return false;
    }

    public boolean isExsit(long appId) {
        AppNewsDomain account = getCacheObject(appId + "");
        return account != null;
    }

    public boolean isExsit(long appId, String version) {
        Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(AppVersionTable.KEY + ":" + appId + ":" + version);
        return appIds != null && !appIds.isEmpty();
    }

    public long count() {
        try {
            long count = countCacheObject();
            this.logger.info("Total " + count + " app news");
        } catch (Exception ex) {
            this.logger.error("Count total app news error.", ex);
        }
        return 0;
    }

    public boolean save(AppNewsDomain domain) {
        try {
            this.repository.save(domain);
            putCacheObject(domain);
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't save app news " + domain, ex);
        }

        return false;
    }

    public boolean delete(long key, long appId) {
        try {
            removeCacheObject(key + "");
            this.repository.delete(key);
            this.logger.info("Delete success app news " + key);
        } catch (Exception ex) {
            this.logger.error("Can't delete app news " + key, ex);
            return false;
        }
        return true;
    }

    private void putCacheObject(AppNewsDomain domain) {
        try {
            this.logger.debug("Put to cache " + domain);
            this.getAppVersionRedisTemplate().opsForHash().put(AppNewsDomain.OBJECT_KEY, domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    private AppNewsDomain getCacheObject(String key) {
        AppNewsDomain domain = null;
        try {
            domain = (AppNewsDomain) this.getAppVersionRedisTemplate().opsForHash().get(AppNewsDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + AppNewsDomain.OBJECT_KEY + " in database");
                domain = repository.findById(Long.valueOf(key));
                if (domain == null) {
                    this.logger.debug("App " + key + " object " + AppNewsDomain.OBJECT_KEY + " not found");
                }
            }
        } catch (NumberFormatException ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    private void removeCacheObject(String key) {
        try {
            this.getAppVersionRedisTemplate().opsForHash().delete(AppNewsDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private List<AppNewsDomain> getCacheObjects() {
        List<AppNewsDomain> apps = new ArrayList<AppNewsDomain>();
        try {
            for (Object user : this.getAppVersionRedisTemplate().opsForHash().values(AppNewsDomain.OBJECT_KEY)) {
                apps.add((AppNewsDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AppNewsDomain.OBJECT_KEY + " from Redis", ex);
        }
        return apps;
    }

    private long countCacheObject() {
        try {
            return this.getAppVersionRedisTemplate().opsForHash().size(AppNewsDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppNewsDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private void setCacheObjects(List<AppNewsDomain> domains) {
        try {
            for (AppNewsDomain domain : domains) {
                putCacheObject(domain);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AppNewsDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    public void clearCache() {
        try {
            this.logger.debug("Clear objects " + AppNewsDomain.OBJECT_KEY + " in cache");
            List<AppNewsDomain> objects = getCacheObjects();
            for (AppNewsDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AppNewsDomain.OBJECT_KEY + " from Redis", ex);
        }
    }
}
