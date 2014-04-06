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

import com.downfy.persistence.domain.AccountDomain;
import com.downfy.persistence.repositories.AccountRepository;
import com.downfy.persistence.table.AccountTable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/*
 * AccountService.java
 *
 * Account service
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
@Service
public class AccountService {

    private final Logger logger = LoggerFactory.getLogger(AccountService.class);
    @Autowired
    private AccountRepository repository;
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    private RedisTemplate<String, AccountDomain> accountRedisTemplate;
    private RedisTemplate<String, Long> longRedisTemplate;

    private RedisTemplate<String, Long> getLongRedisTemplate() {
        if (longRedisTemplate == null) {
            this.longRedisTemplate = new RedisTemplate<String, Long>();
            this.longRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.longRedisTemplate.afterPropertiesSet();
        }
        return longRedisTemplate;
    }

    private RedisTemplate<String, AccountDomain> getAccountRedisTemplate() {
        if (accountRedisTemplate == null) {
            this.accountRedisTemplate = new RedisTemplate<String, AccountDomain>();
            this.accountRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.accountRedisTemplate.afterPropertiesSet();
        }
        return accountRedisTemplate;
    }

    public List<AccountDomain> findAll() {
        List<AccountDomain> account = null;
        try {
            this.logger.info("Find all account in cache.");
            account = this.getCacheObjects();
            if (account.isEmpty()) {
                this.logger.debug("Find all account in database.");
                account = this.repository.findAll();
                if (!account.isEmpty()) {
                    this.setCacheObjects(account);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Find all account error: " + ex, ex);
        }
        if (account != null) {
            this.logger.debug("Total get " + account.size() + " accounts.");
        }
        return account;
    }

    public AccountDomain findByEmail(String email) {
        this.logger.info("Find account by " + email);
        long id = getKey(email);
        if (id > 0) {
            return getCacheObject(id + "");
        } else {
            AccountDomain domain = this.repository.findByEmail(email);
            if (domain != null) {
                this.putCacheObject(domain);
            } else {
                this.logger.info("Can't find email " + email);
            }
            return domain;
        }
    }

    public AccountDomain findById(long id) {
        this.logger.info("Find account by id " + id);
        AccountDomain account = getCacheObject(id + "");
        if (account != null) {
            return account;
        } else {
            AccountDomain domain = this.repository.findById(id);
            if (domain != null) {
                this.putCacheObject(domain);
            } else {
                this.logger.info("Can't find account id " + id);
            }
            return domain;
        }
    }

    public boolean isExsit(long id) {
        this.logger.info("Check exist account by id " + id);
        AccountDomain account = getCacheObject(id + "");
        if (account != null) {
            return true;
        } else {
            account = repository.findById(id);
            return account != null;
        }
    }

    public boolean isExsit(String email) {
        this.logger.info("Check exist account by email " + email);
        AccountDomain account = findByEmail(email);
        return account != null;
    }

    public boolean save(AccountDomain domain) {
        try {
            this.logger.info("Save account " + domain.toString());
            this.putCacheObject(domain);
            this.repository.save(domain);
            return true;
        } catch (DuplicateKeyException dkex) {
            this.logger.warn("Email " + domain.toString() + " duplicate.");
        } catch (Exception ex) {
            this.logger.error("Can't save account " + domain.toString(), ex);
        }
        return false;
    }

    public boolean active(AccountDomain domain) {
        try {
            this.logger.info("Active account " + domain.getEmail());
            domain.setEnabled(true);
            this.putCacheObject(domain);
            this.repository.active(domain.getId());
        } catch (Exception ex) {
            this.logger.error("Can't active account " + domain.getEmail(), ex);
            return false;
        }
        return true;
    }

    public boolean block(AccountDomain domain) {
        try {
            this.logger.info("Block account " + domain.getEmail());
            domain.setEnabled(false);
            this.putCacheObject(domain);
            this.repository.block(domain.getId());
        } catch (Exception ex) {
            this.logger.error("Can't block account " + domain.getEmail(), ex);
            return false;
        }
        return true;
    }

    public long count() {
        try {
            long count = countCacheObject();
            this.logger.debug("Total " + count + " account in cache.");
        } catch (Exception ex) {
            this.logger.error("Count total account in error.", ex);
        }
        return 0L;
    }

    private void putCacheObject(AccountDomain domain) {
        try {
            String key = AccountTable.KEY + ":" + domain.getEmail();
            this.logger.debug("Put email " + key + " and id " + domain.getId());
            this.getLongRedisTemplate().opsForSet().add(key, domain.getId());
            this.getAccountRedisTemplate().opsForHash().put(AccountDomain.OBJECT_KEY, domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    private AccountDomain getCacheObject(String key) {
        AccountDomain domain = null;
        try {
            domain = (AccountDomain) this.getAccountRedisTemplate().opsForHash().get(AccountDomain.OBJECT_KEY, key);
            if (domain == null) {
                domain = repository.findById(Long.valueOf(key));
                if (domain == null) {
                    this.logger.debug("Account " + key + " object " + AccountDomain.OBJECT_KEY + " not found");
                }
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get from Redis", ex);
        }
        return domain;
    }

    private void removeCacheObject(String key) {
        try {
            AccountDomain domain = getCacheObject(key);
            if (domain != null) {
                this.getLongRedisTemplate().opsForSet().remove(AccountTable.KEY + ":" + domain.getEmail(), domain.getId());
                this.getAccountRedisTemplate().opsForHash().delete(AccountDomain.OBJECT_KEY, domain.getKey());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    private List<AccountDomain> getCacheObjects() {
        List<AccountDomain> users = new ArrayList<AccountDomain>();
        try {
            this.logger.debug("Get all objects " + AccountDomain.OBJECT_KEY + " in cache");
            for (Object user : this.getAccountRedisTemplate().opsForHash().values(AccountDomain.OBJECT_KEY)) {
                users.add((AccountDomain) user);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get all objects " + AccountDomain.OBJECT_KEY + " from Redis", ex);
        }
        return users;
    }

    private void setCacheObjects(List<AccountDomain> domains) {
        try {
            this.logger.debug("Set " + domains.size() + " objects " + AccountDomain.OBJECT_KEY + " to cache");
            for (AccountDomain domain : domains) {
                this.putCacheObject(domain);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AccountDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    private long countCacheObject() {
        try {
            this.logger.debug("Count objects " + AccountDomain.OBJECT_KEY + " in cache");
            return getCacheObjects().size();
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AccountDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }

    private void clearCache() {
        try {
            this.logger.debug("Clear objects " + AccountDomain.OBJECT_KEY + " in cache");
            List<AccountDomain> objects = getCacheObjects();
            for (AccountDomain account : objects) {
                removeCacheObject(account.getKey());
            }
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AccountDomain.OBJECT_KEY + " from Redis", ex);
        }
    }

    private long getKey(String email) {
        try {
            Set<Long> appIds = this.getLongRedisTemplate().opsForSet().members(AccountTable.KEY + ":" + email);
            if (null != appIds && !appIds.isEmpty()) {
                List<Long> myList = new ArrayList<Long>(appIds);
                return myList.get(0);
            }
        } catch (Exception ex) {
        }
        return 0;
    }
}
