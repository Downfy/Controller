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

import com.downfy.persistence.domain.backend.category.BackendCategoryDomain;
import com.downfy.persistence.repositories.backend.category.BackendCategoryRepository;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;

/*
 * AccountService.java
 * 
 * Account service
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  27-Nov-2013     tuanta      Create first time
 */
public class CategoryService implements CacheService<BackendCategoryDomain> {

    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    @Autowired
    private BackendCategoryRepository repository;
    @Autowired
    private RedisTemplate<String, BackendCategoryDomain> redisTemplate;

    public void setRepository(BackendCategoryRepository repository) {
        this.repository = repository;
    }

    public void setRedisTemplate(RedisTemplate<String, BackendCategoryDomain> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<BackendCategoryDomain> findAll() {
        List<BackendCategoryDomain> category = null;
        try {
            this.logger.debug("Find all categories in cache.");
            category = getCacheObjects();
            if (category.isEmpty()) {
                this.logger.debug("Find all categories in database.");
                category = this.repository.findAll();
                if (!category.isEmpty()) {
                    setCacheObjects(category);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all categories error: " + ex, ex);
        }
        if (category != null) {
            this.logger.debug("Total get " + category.size() + " categories.");
        }
        return category;
    }

    public BackendCategoryDomain findByURL(String url) {
        this.logger.debug("Find url " + url + " in cache.");
        return getCacheObject(url);
    }

    public boolean isExsit(String url) {
        BackendCategoryDomain category = getCacheObject(url);
        return category != null;
    }

    public boolean save(BackendCategoryDomain domain) {
        try {
            this.logger.debug("Save category " + domain.toString() + " to database");
            this.repository.save(domain);
            putCacheObject(domain);
            this.logger.debug("Save category " + domain.toString() + " to cache");
            return true;
        } catch (DuplicateKeyException dkex) {
            this.logger.warn("Email " + domain.toString() + " duplicate.");
        } catch (Exception ex) {
            this.logger.error("Can't save category " + domain.toString(), ex);
        }
        return false;
    }

    public boolean delete(String key) {
        try {
            this.logger.debug("Delete category " + key + " in cache.");
            removeCacheObject(key);
            this.logger.debug("Delete category " + key + " in database.");
            this.repository.delete(key);
        } catch (Exception ex) {
            this.logger.error("Can't delete category " + key, ex);
            return false;
        }
        return true;
    }

    public long count() {
        try {
            long count = countCacheObject();
            this.logger.debug("Total " + count + " category in cache.");
        } catch (Exception ex) {
            this.logger.error("Count total category in error.", ex);
        }
        return 0L;
    }

    @Override
    public void putCacheObject(BackendCategoryDomain domain) {
        try {
            this.redisTemplate.opsForHash().put(domain.getObjectKey(), domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    @Override
    public BackendCategoryDomain getCacheObject(String key) {
        BackendCategoryDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + BackendCategoryDomain.OBJECT_KEY + " in cache");
            domain = (BackendCategoryDomain) redisTemplate.opsForHash().get(BackendCategoryDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + BackendCategoryDomain.OBJECT_KEY + " in database");
                domain = repository.findByUrl(key);
                if (domain == null) {
                    this.logger.debug("Account " + key + " object " + BackendCategoryDomain.OBJECT_KEY + " not found");
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
            this.logger.debug("Remove key " + key + " object " + BackendCategoryDomain.OBJECT_KEY + " in cache");
            this.redisTemplate.opsForHash().delete(BackendCategoryDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    @Override
    public List<BackendCategoryDomain> getCacheObjects() {
        List<BackendCategoryDomain> users = new ArrayList<BackendCategoryDomain>();
        try {
            this.logger.debug("Get all objects " + BackendCategoryDomain.OBJECT_KEY + " in cache");
            for (Object user : redisTemplate.opsForHash().values(BackendCategoryDomain.OBJECT_KEY)) {
                users.add((BackendCategoryDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + BackendCategoryDomain.OBJECT_KEY + " from Redis", ex);
        }
        return users;
    }

    @Override
    public List<BackendCategoryDomain> getCacheLimitObjects(int start, int end) {
        List<BackendCategoryDomain> users = new ArrayList<BackendCategoryDomain>();
        try {
            this.logger.debug("Get objects from " + start + " to " + end + " " + BackendCategoryDomain.OBJECT_KEY + " in cache");
            long count = count();
            if (start > count) {
                this.logger.debug("Can't get objects outside list data.");
                return users;
            }
            List users_ = redisTemplate.opsForHash().values(BackendCategoryDomain.OBJECT_KEY);
            for (int i = start; i < end; i++) {
                if (end < count) {
                    users.add((BackendCategoryDomain) users_.get(i));
                }
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get objects from " + start + " to " + end + " " + BackendCategoryDomain.OBJECT_KEY + " from Redis", ex);
        }
        return users;
    }

    @Override
    public void setCacheObjects(List<BackendCategoryDomain> domains) {
        try {
            this.logger.debug("Set " + domains.size() + " objects " + BackendCategoryDomain.OBJECT_KEY + " to cache");
            for (BackendCategoryDomain domain : domains) {
                putCacheObject(domain);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + BackendCategoryDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    @Override
    public long countCacheObject() {
        try {
            this.logger.debug("Count objects " + BackendCategoryDomain.OBJECT_KEY + " in cache");
            return redisTemplate.opsForHash().size(BackendCategoryDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + BackendCategoryDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }
}
