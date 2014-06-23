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
package com.downfy.service.application.article;

import com.downfy.common.AppCommon;
import com.downfy.persistence.domain.application.AppScreenshootDomain;
import com.downfy.persistence.domain.article.ArticleDomain;
import com.downfy.persistence.repositories.article.ArticleRepository;
import com.downfy.persistence.table.AppScreenshootTable;
import com.downfy.persistence.table.ArticleTable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
 * ArticleService.java
 *
 * App article service
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  1-Dec-2013     tuanta      Create first time
 *  20-Dec-2013     tuanta      Add Developer<->App Version
 */
@Service
public class ArticleService {

    private final Logger logger = LoggerFactory.getLogger(ArticleService.class);
    @Autowired
    ArticleRepository repository;
    @Autowired
    ServletContext context;
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    private RedisTemplate<String, ArticleDomain> appNewsRedisTemplate;
    private RedisTemplate<String, String> longRedisTemplate;

    public RedisTemplate<String, ArticleDomain> getArticleRedisTemplate() {
        if (appNewsRedisTemplate == null) {
            this.appNewsRedisTemplate = new RedisTemplate<String, ArticleDomain>();
            this.appNewsRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.appNewsRedisTemplate.afterPropertiesSet();
        }
        return appNewsRedisTemplate;
    }

    public RedisTemplate<String, String> getLongRedisTemplate() {
        if (longRedisTemplate == null) {
            this.longRedisTemplate = new RedisTemplate<String, String>();
            this.longRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.longRedisTemplate.afterPropertiesSet();
        }
        return longRedisTemplate;
    }

    public List<ArticleDomain> findAll() {
        List<ArticleDomain> articles = null;
        try {
            articles = this.getCacheObjects();
            if (articles.isEmpty()) {
                articles = this.repository.findAll();
                if (!articles.isEmpty()) {
                    this.setCacheObjects(articles);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all article error: " + ex, ex);
        }
        if (articles != null) {
            this.logger.debug("Total get " + articles.size() + " articles.");
        }
        return articles;
    }

    public ArticleDomain findById(long id) {
        return getCacheObject(id + "");
    }

    public List<ArticleDomain> findByCreater(long creater) {
        List<ArticleDomain> articles = null;
        try {
            articles = getCacheObjectsByCreater(creater);
            if (articles.isEmpty()) {
                articles = this.repository.findByCreater(creater);
                if (!articles.isEmpty()) {
                    this.setCacheObjects(articles);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find by creater " + creater + " article error: " + ex, ex);
        }
        if (articles != null) {
            this.logger.debug("Total get " + articles.size() + " articles.");
        }
        return articles;
    }

    public List<ArticleDomain> findByType(int type) {
        List<ArticleDomain> articles = null;
        try {
            articles = getCacheObjectsByType(type);
            if (articles.isEmpty()) {
                articles = this.repository.findByType(type);
                if (!articles.isEmpty()) {
                    this.setCacheObjects(articles);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find by type " + type + " article error: " + ex, ex);
        }
        if (articles != null) {
            this.logger.debug("Total get " + articles.size() + " articles.");
        }
        return articles;
    }

    public List<ArticleDomain> findByCreaterAndType(long creater, int type) {
        List<ArticleDomain> articles = null;
        try {
            articles = getCacheObjectsByCreaterAndType(creater, type);
            if (articles.isEmpty()) {
                articles = this.repository.findByCreaterAndType(creater, type);
                if (!articles.isEmpty()) {
                    this.setCacheObjects(articles);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find by creater " + creater + " and type " + type + " article error: " + ex, ex);
        }
        if (articles != null) {
            this.logger.debug("Total get " + articles.size() + " articles.");
        }
        return articles;
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
            ArticleDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PUBLISHED);
                putCacheObject(app);
                this.repository.publish(key);
                this.logger.info("Request publish success article " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't Request publish article " + key, ex);
        }
        return false;
    }

    public boolean approve(long key) {
        try {
            ArticleDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.PENDING);
                putCacheObject(app);
                this.repository.approve(key);
                this.logger.info("Request approve success article " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't requets approve article " + key, ex);
        }
        return false;
    }

    public boolean block(long key) {
        try {
            ArticleDomain app = getCacheObject(key + "");
            if (app != null) {
                app.setStatus(AppCommon.BLOCKED);
                putCacheObject(app);
                this.logger.debug("Block article " + key + " to database");
                this.repository.block(key);
                this.logger.info("Block success article " + key);
                return true;
            }
        } catch (Exception ex) {
            this.logger.error("Can't block article " + key, ex);
        }
        return false;
    }

    public boolean isExsit(long key) {
        ArticleDomain account = getCacheObject(key + "");
        return account != null;
    }

    public long count() {
        try {
            long count = countCacheObject();
            this.logger.info("Total " + count + " article");
        } catch (Exception ex) {
            this.logger.error("Count total article error.", ex);
        }
        return 0;
    }

    public long count(long creater) {
        try {
            long count = countCacheObject(creater);
            this.logger.info("Total user " + creater + " have " + count + " article");
        } catch (Exception ex) {
            this.logger.error("Count total article error.", ex);
        }
        return 0;
    }

    public boolean save(ArticleDomain domain) {
        try {
            this.repository.save(domain);
            putCacheObject(domain);
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't save article " + domain, ex);
        }

        return false;
    }
    
    public boolean update(ArticleDomain domain) {
        try {
            this.repository.update(domain);
            putCacheObject(domain);
            return true;
        } catch (Exception ex) {
            this.logger.error("Can't update article " + domain, ex);
        }

        return false;
    }

    public boolean delete(long key) {
        try {
            removeCacheObject(key + "");
            this.repository.delete(key);
            this.logger.info("Delete success article " + key);
        } catch (Exception ex) {
            this.logger.error("Can't delete article " + key, ex);
            return false;
        }
        return true;
    }

    private void putCacheObject(ArticleDomain domain) {
        try {
            this.logger.debug("Put to cache " + domain);
            this.getArticleRedisTemplate().opsForHash().put(ArticleDomain.OBJECT_KEY, domain.getKey(), domain);
            this.getLongRedisTemplate().opsForSet().add(ArticleTable.KEY + ":" + domain.getCreater(), domain.getKey());
            this.getLongRedisTemplate().opsForSet().add(ArticleTable.KEY + ":" + domain.getType(), domain.getKey());
            this.getLongRedisTemplate().opsForSet().add(ArticleTable.KEY + ":" + domain.getCreater() + ":" + domain.getType(), domain.getKey());
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    private ArticleDomain getCacheObject(String key) {
        ArticleDomain domain = null;
        try {
            domain = (ArticleDomain) this.getArticleRedisTemplate().opsForHash().get(ArticleDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + ArticleDomain.OBJECT_KEY + " in database");
                domain = repository.findById(Long.valueOf(key));
                if (domain == null) {
                    this.logger.debug("App " + key + " object " + ArticleDomain.OBJECT_KEY + " not found");
                }
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    private void removeCacheObject(String key) {
        try {
            this.getArticleRedisTemplate().opsForHash().delete(ArticleDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private List<ArticleDomain> getCacheObjects() {
        List<ArticleDomain> articles = new ArrayList<ArticleDomain>();
        try {
            for (Object user : this.getArticleRedisTemplate().opsForHash().values(ArticleDomain.OBJECT_KEY)) {
                articles.add((ArticleDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + ArticleDomain.OBJECT_KEY + " from Redis", ex);
        }
        return articles;
    }

    private List<ArticleDomain> getCacheObjectsByCreater(long creater) {
        Collection<Object> keys = getKeysByCreater(creater);
        List<ArticleDomain> articles = new ArrayList<ArticleDomain>();
        try {
            for (Object user : this.getArticleRedisTemplate().opsForHash().multiGet(ArticleDomain.OBJECT_KEY, keys)) {
                articles.add((ArticleDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + ArticleDomain.OBJECT_KEY + " from Redis", ex);
        }
        return articles;
    }

    private List<ArticleDomain> getCacheObjectsByType(int type) {
        Collection<Object> keys = getKeysByType(type);
        List<ArticleDomain> articles = new ArrayList<ArticleDomain>();
        try {
            for (Object user : this.getArticleRedisTemplate().opsForHash().multiGet(ArticleDomain.OBJECT_KEY, keys)) {
                articles.add((ArticleDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + ArticleDomain.OBJECT_KEY + " from Redis", ex);
        }
        return articles;
    }

    private List<ArticleDomain> getCacheObjectsByCreaterAndType(long creater, int type) {
        Collection<Object> keys = getKeysByCreaterAndType(creater, type);
        List<ArticleDomain> articles = new ArrayList<ArticleDomain>();
        try {
            for (Object user : this.getArticleRedisTemplate().opsForHash().multiGet(ArticleDomain.OBJECT_KEY, keys)) {
                articles.add((ArticleDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + ArticleDomain.OBJECT_KEY + " from Redis", ex);
        }
        return articles;
    }

    private long countCacheObject() {
        try {
            return this.getArticleRedisTemplate().opsForHash().size(ArticleDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + ArticleDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private long countCacheObject(long creater) {
        try {
            this.logger.debug("Count objects " + ArticleDomain.OBJECT_KEY + " in cache");
            return this.getLongRedisTemplate().opsForSet().size(ArticleTable.KEY + ":" + creater);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + ArticleDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private void setCacheObjects(List<ArticleDomain> domains) {
        try {
            for (ArticleDomain domain : domains) {
                putCacheObject(domain);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + ArticleDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    public void clearCache() {
        try {
            this.logger.debug("Clear objects " + ArticleDomain.OBJECT_KEY + " in cache");
            List<ArticleDomain> objects = getCacheObjects();
            for (ArticleDomain appVersionDomain : objects) {
                removeCacheObject(appVersionDomain.getKey());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + ArticleDomain.OBJECT_KEY + " from Redis", ex);
        }
    }

    private Collection<Object> getKeysByCreater(long creater) {
        Collection<Object> keys = new ArrayList<Object>();
        try {
            Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(ArticleTable.KEY + ":" + creater);
            List<String> myList = new ArrayList<String>(appIds);
            Collections.sort(myList, new Comparator<String>() {
                @Override
                public int compare(String id01, String id02) {
                    return id01.compareTo(id02);
                }
            });
            logger.debug("Get keys from app " + creater + " ==> " + appIds);
            keys.addAll(myList);
        } catch (Exception ex) {
        }
        return keys;
    }

    private Collection<Object> getKeysByType(int type) {
        Collection<Object> keys = new ArrayList<Object>();
        try {
            Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(ArticleTable.KEY + ":" + type);
            List<String> myList = new ArrayList<String>(appIds);
            Collections.sort(myList, new Comparator<String>() {
                @Override
                public int compare(String id01, String id02) {
                    return id01.compareTo(id02);
                }
            });
            logger.debug("Get keys from app " + type + " ==> " + appIds);
            keys.addAll(myList);
        } catch (Exception ex) {
        }
        return keys;
    }

    private Collection<Object> getKeysByCreaterAndType(long creater, int type) {
        Collection<Object> keys = new ArrayList<Object>();
        try {
            Set<String> appIds = this.getLongRedisTemplate().opsForSet().members(ArticleTable.KEY + ":" + creater + ":" + type);
            List<String> myList = new ArrayList<String>(appIds);
            Collections.sort(myList, new Comparator<String>() {
                @Override
                public int compare(String id01, String id02) {
                    return id01.compareTo(id02);
                }
            });
            logger.debug("Get keys from app " + creater + " ==> " + appIds);
            keys.addAll(myList);
        } catch (Exception ex) {
        }
        return keys;
    }
}
