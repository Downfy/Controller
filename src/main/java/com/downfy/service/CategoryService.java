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

import com.downfy.persistence.domain.admin.category.AdminCategoryDomain;
import com.downfy.persistence.repositories.admin.category.AdminCategoryRepository;
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
public class CategoryService implements CacheService<AdminCategoryDomain> {

    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    @Autowired
    private AdminCategoryRepository repository;
    @Autowired
    private RedisTemplate<String, AdminCategoryDomain> redisTemplate;

    public void setRepository(AdminCategoryRepository repository) {
        this.repository = repository;
    }

    public void setRedisTemplate(RedisTemplate<String, AdminCategoryDomain> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<AdminCategoryDomain> findAll() {
        List<AdminCategoryDomain> category = null;
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

    public AdminCategoryDomain findByURL(String url) {
        this.logger.debug("Find url " + url + " in cache.");
        return getCacheObject(url);
    }

    public boolean isExsit(String url) {
        AdminCategoryDomain category = getCacheObject(url);
        return category != null;
    }

    public boolean save(AdminCategoryDomain domain) {
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
    public void putCacheObject(AdminCategoryDomain domain) {
        try {
            this.redisTemplate.opsForHash().put(domain.getObjectKey(), domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    @Override
    public AdminCategoryDomain getCacheObject(String key) {
        AdminCategoryDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + AdminCategoryDomain.OBJECT_KEY + " in cache");
            domain = (AdminCategoryDomain) redisTemplate.opsForHash().get(AdminCategoryDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + AdminCategoryDomain.OBJECT_KEY + " in database");
                domain = repository.findByUrl(key);
                if (domain == null) {
                    this.logger.debug("Account " + key + " object " + AdminCategoryDomain.OBJECT_KEY + " not found");
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
            this.logger.debug("Remove key " + key + " object " + AdminCategoryDomain.OBJECT_KEY + " in cache");
            this.redisTemplate.opsForHash().delete(AdminCategoryDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    @Override
    public List<AdminCategoryDomain> getCacheObjects() {
        List<AdminCategoryDomain> users = new ArrayList<AdminCategoryDomain>();
        try {
            this.logger.debug("Get all objects " + AdminCategoryDomain.OBJECT_KEY + " in cache");
            for (Object user : redisTemplate.opsForHash().values(AdminCategoryDomain.OBJECT_KEY)) {
                users.add((AdminCategoryDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AdminCategoryDomain.OBJECT_KEY + " from Redis", ex);
        }
        return users;
    }

    @Override
    public List<AdminCategoryDomain> getCacheLimitObjects(int start, int end) {
        List<AdminCategoryDomain> users = new ArrayList<AdminCategoryDomain>();
        try {
            this.logger.debug("Get objects from " + start + " to " + end + " " + AdminCategoryDomain.OBJECT_KEY + " in cache");
            long count = count();
            if (start > count) {
                this.logger.debug("Can't get objects outside list data.");
                return users;
            }
            List users_ = redisTemplate.opsForHash().values(AdminCategoryDomain.OBJECT_KEY);
            for (int i = start; i < end; i++) {
                if (end < count) {
                    users.add((AdminCategoryDomain) users_.get(i));
                }
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get objects from " + start + " to " + end + " " + AdminCategoryDomain.OBJECT_KEY + " from Redis", ex);
        }
        return users;
    }

    @Override
    public void setCacheObjects(List<AdminCategoryDomain> domains) {
        try {
            this.logger.debug("Set " + domains.size() + " objects " + AdminCategoryDomain.OBJECT_KEY + " to cache");
            for (AdminCategoryDomain domain : domains) {
                putCacheObject(domain);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AdminCategoryDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    @Override
    public long countCacheObject() {
        try {
            this.logger.debug("Count objects " + AdminCategoryDomain.OBJECT_KEY + " in cache");
            return redisTemplate.opsForHash().size(AdminCategoryDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AdminCategoryDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }
}
