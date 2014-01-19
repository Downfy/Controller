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
package com.downfy.service.developer;

import com.downfy.persistence.domain.developer.DeveloperDomain;
import com.downfy.persistence.repositories.developer.DeveloperRepository;
import com.downfy.service.CacheService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/*
 * DeveloperService.java
 * 
 * Developer service
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  03-Jan-2014     tuanta      Create first time
 */
@Service
public class DeveloperService implements CacheService<DeveloperDomain> {

    private final Logger logger = LoggerFactory.getLogger(DeveloperService.class);
    @Autowired
    private DeveloperRepository repository;
    @Autowired
    private RedisTemplate<String, DeveloperDomain> redisTemplate;

    public void setRepository(DeveloperRepository repository) {
        this.repository = repository;
    }

    public void setRedisTemplate(RedisTemplate<String, DeveloperDomain> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<DeveloperDomain> findAll() {
        List<DeveloperDomain> developer = null;
        try {
            this.logger.debug("Find all developers in cache.");
            developer = getCacheObjects();
            if (developer.isEmpty()) {
                this.logger.debug("Find all developers in database.");
                developer = this.repository.findAll();
                if (!developer.isEmpty()) {
                    setCacheObjects(developer);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all developers error: " + ex, ex);
        }
        if (developer != null) {
            this.logger.debug("Total get " + developer.size() + " developers.");
        }
        return developer;
    }

    public DeveloperDomain findById(String id) {
        this.logger.debug("Find url " + id + " in cache.");
        return getCacheObject(id);
    }

    public boolean isExsit(String url) {
        DeveloperDomain developer = getCacheObject(url);
        return developer != null;
    }

    public boolean save(DeveloperDomain domain) {
        try {
            this.logger.debug("Save developer " + domain.toString() + " to database");
            this.repository.save(domain);
            putCacheObject(domain);
            this.logger.debug("Save developer " + domain.toString() + " to cache");
            return true;
        } catch (DuplicateKeyException dkex) {
            this.logger.warn("Id " + domain.toString() + " duplicate.");
        } catch (Exception ex) {
            this.logger.error("Can't save developer " + domain.toString(), ex);
        }
        return false;
    }

    public boolean delete(String key) {
        try {
            this.logger.debug("Delete developer " + key + " in cache.");
            removeCacheObject(key);
            this.logger.debug("Delete developer " + key + " in database.");
            this.repository.delete(key);
        } catch (Exception ex) {
            this.logger.error("Can't delete developer " + key, ex);
            return false;
        }
        return true;
    }

    public long count() {
        try {
            long count = countCacheObject();
            this.logger.debug("Total " + count + " developer in cache.");
        } catch (Exception ex) {
            this.logger.error("Count total developer in error.", ex);
        }
        return 0L;
    }

    @Override
    public void putCacheObject(DeveloperDomain domain) {
        try {
            this.redisTemplate.opsForHash().put(domain.getObjectKey(), domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    @Override
    public DeveloperDomain getCacheObject(String key) {
        DeveloperDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + DeveloperDomain.OBJECT_KEY + " in cache");
            domain = (DeveloperDomain) redisTemplate.opsForHash().get(DeveloperDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + DeveloperDomain.OBJECT_KEY + " in database");
                domain = repository.findById(key);
                if (domain == null) {
                    this.logger.debug("Account " + key + " object " + DeveloperDomain.OBJECT_KEY + " not found");
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
            this.logger.debug("Remove key " + key + " object " + DeveloperDomain.OBJECT_KEY + " in cache");
            this.redisTemplate.opsForHash().delete(DeveloperDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    @Override
    public List<DeveloperDomain> getCacheObjects() {
        List<DeveloperDomain> users = new ArrayList<DeveloperDomain>();
        try {
            this.logger.debug("Get all objects " + DeveloperDomain.OBJECT_KEY + " in cache");
            for (Object user : redisTemplate.opsForHash().values(DeveloperDomain.OBJECT_KEY)) {
                users.add((DeveloperDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + DeveloperDomain.OBJECT_KEY + " from Redis", ex);
        }
        return users;
    }

    @Override
    public List<DeveloperDomain> getCacheLimitObjects(int start, int end) {
        List<DeveloperDomain> users = new ArrayList<DeveloperDomain>();
        try {
            this.logger.debug("Get objects from " + start + " to " + end + " " + DeveloperDomain.OBJECT_KEY + " in cache");
            long count = count();
            if (start > count) {
                this.logger.debug("Can't get objects outside list data.");
                return users;
            }
            List users_ = redisTemplate.opsForHash().values(DeveloperDomain.OBJECT_KEY);
            for (int i = start; i < end; i++) {
                if (end < count) {
                    users.add((DeveloperDomain) users_.get(i));
                }
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get objects from " + start + " to " + end + " " + DeveloperDomain.OBJECT_KEY + " from Redis", ex);
        }
        return users;
    }

    @Override
    public void setCacheObjects(List<DeveloperDomain> domains) {
        try {
            this.logger.debug("Set " + domains.size() + " objects " + DeveloperDomain.OBJECT_KEY + " to cache");
            for (DeveloperDomain domain : domains) {
                putCacheObject(domain);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + DeveloperDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    @Override
    public long countCacheObject() {
        try {
            this.logger.debug("Count objects " + DeveloperDomain.OBJECT_KEY + " in cache");
            return redisTemplate.opsForHash().size(DeveloperDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + DeveloperDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    public void clearCache() {
        try {
            this.logger.debug("Clear objects " + DeveloperDomain.OBJECT_KEY + " in cache");
            List<DeveloperDomain> objects = getCacheObjects();
            for (DeveloperDomain developerDomain : objects) {
                removeCacheObject(developerDomain.getKey());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + DeveloperDomain.OBJECT_KEY + " from Redis", ex);
        }
    }
}
