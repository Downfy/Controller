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

import com.downfy.persistence.domain.category.CategoryDomain;
import com.downfy.persistence.repositories.category.CategoryRepository;
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
public class CategoryService implements CacheService<CategoryDomain> {

    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    @Autowired
    private CategoryRepository repository;
    @Autowired
    private RedisTemplate<String, CategoryDomain> redisTemplate;

    public void setRepository(CategoryRepository repository) {
        this.repository = repository;
    }

    public void setRedisTemplate(RedisTemplate<String, CategoryDomain> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<CategoryDomain> findAll() {
        List<CategoryDomain> category = null;
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

    public CategoryDomain findByURL(String url) {
        this.logger.debug("Find url " + url + " in cache.");
        return getCacheObject(url);
    }

    public boolean isExsit(String url) {
        CategoryDomain category = getCacheObject(url);
        return category != null;
    }

    public boolean save(CategoryDomain domain) {
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
    public void putCacheObject(CategoryDomain domain) {
        try {
            this.redisTemplate.opsForHash().put(domain.getObjectKey(), domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    @Override
    public CategoryDomain getCacheObject(String key) {
        CategoryDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + CategoryDomain.OBJECT_KEY + " in cache");
            domain = (CategoryDomain) redisTemplate.opsForHash().get(CategoryDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + CategoryDomain.OBJECT_KEY + " in database");
                domain = repository.findByUrl(key);
                if (domain == null) {
                    this.logger.debug("Account " + key + " object " + CategoryDomain.OBJECT_KEY + " not found");
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
            this.logger.debug("Remove key " + key + " object " + CategoryDomain.OBJECT_KEY + " in cache");
            this.redisTemplate.opsForHash().delete(CategoryDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    @Override
    public List<CategoryDomain> getCacheObjects() {
        List<CategoryDomain> users = new ArrayList<CategoryDomain>();
        try {
            this.logger.debug("Get all objects " + CategoryDomain.OBJECT_KEY + " in cache");
            for (Object user : redisTemplate.opsForHash().values(CategoryDomain.OBJECT_KEY)) {
                users.add((CategoryDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + CategoryDomain.OBJECT_KEY + " from Redis", ex);
        }
        return users;
    }

    @Override
    public List<CategoryDomain> getCacheLimitObjects(int start, int end) {
        List<CategoryDomain> users = new ArrayList<CategoryDomain>();
        try {
            this.logger.debug("Get objects from " + start + " to " + end + " " + CategoryDomain.OBJECT_KEY + " in cache");
            long count = count();
            if (start > count) {
                this.logger.debug("Can't get objects outside list data.");
                return users;
            }
            List users_ = redisTemplate.opsForHash().values(CategoryDomain.OBJECT_KEY);
            for (int i = start; i < end; i++) {
                if (end < count) {
                    users.add((CategoryDomain) users_.get(i));
                }
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get objects from " + start + " to " + end + " " + CategoryDomain.OBJECT_KEY + " from Redis", ex);
        }
        return users;
    }

    @Override
    public void setCacheObjects(List<CategoryDomain> domains) {
        try {
            this.logger.debug("Set " + domains.size() + " objects " + CategoryDomain.OBJECT_KEY + " to cache");
            for (CategoryDomain domain : domains) {
                putCacheObject(domain);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + CategoryDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    @Override
    public long countCacheObject() {
        try {
            this.logger.debug("Count objects " + CategoryDomain.OBJECT_KEY + " in cache");
            return redisTemplate.opsForHash().size(CategoryDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + CategoryDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }
}
