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
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class AccountService implements CacheService<AccountDomain> {

    private final Logger logger = LoggerFactory.getLogger(AccountService.class);
    @Autowired
    private AccountRepository repository;
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    private RedisTemplate<String, AccountDomain> accountRedisTemplate;
    private RedisTemplate<String, Long> longRedisTemplate;

    public RedisTemplate<String, Long> getLongRedisTemplate() {
        if (longRedisTemplate == null) {
            this.longRedisTemplate = new RedisTemplate<String, Long>();
            this.longRedisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.longRedisTemplate.afterPropertiesSet();
        }
        return longRedisTemplate;
    }

    public RedisTemplate<String, AccountDomain> getAccountRedisTemplate() {
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

    public List<AccountDomain> findByLimit(int start, int end) {
        List<AccountDomain> account = null;
        try {
            this.logger.info("Find list account from " + start + " to " + end + " in cache.");
            account = getCacheLimitObjects(start, end);
        } catch (Exception ex) {
            this.logger.error("Find list account error: " + ex, ex);
        }
        if ((account != null) && (!account.isEmpty())) {
            this.logger.debug("Total get " + account.size() + " accounts.");
            return account;
        }
        return new ArrayList();
    }

    public AccountDomain findByEmailAndPassword(String email, String password) {
        AccountDomain account = null;
        try {
            this.logger.info("Find account " + email + " and password ********* in cache.");
            account = findByEmail(email);
            if (account != null && !account.getPassword().equals(password)) {
                this.logger.debug("Account " + email + " and password not match.");
                return null;
            }
        } catch (Exception ex) {
            this.logger.error("Find by username " + email + " and password ******** error: ", ex);
        }
        return account;
    }

    public AccountDomain findByEmail(String email) {
        long id = findIdByEmail(email);
        if (id > 0) {
            this.logger.debug("Find account by " + id + " in cache.");
            return getCacheObject(id + "");
        }
        return null;
    }

    public long findIdByEmail(String email) {
        try {
            String key = AccountTable.EMAIL + ":" + email;
            this.logger.debug("Find email " + key + " in cache.");
            return this.getLongRedisTemplate().opsForSet().randomMember(key);
        } catch (NullPointerException null_) {
        } catch (Exception ex) {
            this.logger.error("Can't email " + email + " in cache.");
        }
        return 0;
    }

    public boolean isExsit(long id) {
        AccountDomain account = getCacheObject(id + "");
        return account != null;
    }

    public boolean save(AccountDomain domain) {
        try {
            this.logger.info("Save account " + domain.toString() + " to database");
            this.putCacheObject(domain);
            this.repository.save(domain);
            this.logger.debug("Save account " + domain.toString() + " to cache");
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
            this.logger.info("Active account " + domain.getEmail() + " in cache.");
            domain.setEnabled(true);
            this.putCacheObject(domain);
            this.logger.debug("Active account " + domain.getEmail() + " in database.");
            this.repository.active(domain.getEmail());
        } catch (Exception ex) {
            this.logger.error("Can't active account " + domain.getEmail(), ex);
            return false;
        }
        return true;
    }

    public boolean block(AccountDomain domain) {
        try {
            this.logger.info("Block account " + domain.getEmail() + " in cache.");
            domain.setEnabled(false);
            this.putCacheObject(domain);
            this.logger.debug("Block account " + domain.getEmail() + " in database.");
            this.repository.block(domain.getEmail());
        } catch (Exception ex) {
            this.logger.error("Can't block account " + domain.getEmail(), ex);
            return false;
        }
        return true;
    }

    public boolean login(AccountDomain domain, String lastHostAddress) {
        try {
            this.logger.info("Log time login account " + domain.getEmail() + " in cache");
            domain.setLastHostAddress(lastHostAddress);
            this.putCacheObject(domain);
            this.logger.debug("Log time login account " + domain.getEmail() + " to database");
            this.repository.login(domain.getEmail(), lastHostAddress, new Date());
        } catch (Exception ex) {
            this.logger.error("Can't update login account " + domain.getEmail(), ex);
            return false;
        }
        return true;
    }

    public boolean changePassword(String email, String password) {
        try {
            this.logger.info("Change password account " + email + " to cache");
            AccountDomain domain = findByEmail(email);
            Preconditions.checkNotNull(domain, "Can't find account by " + email + " in cache");
            domain.setPassword(password);
            this.putCacheObject(domain);
            this.logger.debug("Change password account " + domain.getEmail() + " to database");
            this.repository.changePassword(email, password, new Date());
            return true;
        } catch (NullPointerException null_) {
            this.logger.info(null_.getMessage());
        } catch (Exception ex) {
            this.logger.error("Can't change password account " + email, ex);
        }
        return false;
    }

    public boolean delete(String email) {
        try {
            this.logger.info("Delete account " + email + " in cache.");
            AccountDomain domain = findByEmail(email);
            Preconditions.checkNotNull(domain, "Can't find account by " + email + " in cache");
            this.removeCacheObject(domain.getKey());
            this.getLongRedisTemplate().opsForSet().pop(AccountTable.EMAIL + ":" + email);
            this.logger.debug("Delete account " + email + " in database.");
            this.repository.delete(domain.getKey());
            this.logger.debug(getCacheObjects().toString());
        } catch (NullPointerException null_) {
            this.logger.info(null_.getMessage());
        } catch (Exception ex) {
            this.logger.error("Can't delete account " + email, ex);
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

    @Override
    public void putCacheObject(AccountDomain domain) {
        try {
            String key = AccountTable.EMAIL + ":" + domain.getEmail();
            this.logger.debug("Put email " + key + " and id " + domain.getId() + " in cache.");
            this.getLongRedisTemplate().opsForSet().add(key, domain.getId());
            this.logger.debug("Put account " + key + " in cache.");
            this.getAccountRedisTemplate().opsForHash().put(domain.getObjectKey(), domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    @Override
    public AccountDomain getCacheObject(String key) {
        AccountDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + AccountDomain.OBJECT_KEY + " in cache");
            domain = (AccountDomain) this.getAccountRedisTemplate().opsForHash().get(AccountDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + AccountDomain.OBJECT_KEY + " in database");
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

    @Override
    public void removeCacheObject(String key) {
        try {
            this.logger.debug("Remove key " + key + " object " + AccountDomain.OBJECT_KEY + " in cache");
            this.getAccountRedisTemplate().opsForHash().delete(AccountDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    @Override
    public List<AccountDomain> getCacheObjects() {
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

    @Override
    public List<AccountDomain> getCacheLimitObjects(int start, int end) {
        List<AccountDomain> users = new ArrayList<AccountDomain>();
        try {
            this.logger.debug("Get objects from " + start + " to " + end + " " + AccountDomain.OBJECT_KEY + " in cache");
            long count = count();
            if (start > count) {
                this.logger.debug("Can't get objects outside list data.");
                return users;
            }
            List users_ = this.getAccountRedisTemplate().opsForHash().values(AccountDomain.OBJECT_KEY);
            for (int i = start; i < end; i++) {
                if (end < count) {
                    users.add((AccountDomain) users_.get(i));
                }
            }
        } catch (Exception ex) {
            this.logger.warn("Can't get objects from " + start + " to " + end + " " + AccountDomain.OBJECT_KEY + " from Redis", ex);
        }
        return users;
    }

    @Override
    public void setCacheObjects(List<AccountDomain> domains) {
        try {
            this.logger.debug("Set " + domains.size() + " objects " + AccountDomain.OBJECT_KEY + " to cache");
            for (AccountDomain domain : domains) {
                this.putCacheObject(domain);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AccountDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    @Override
    public long countCacheObject() {
        try {
            this.logger.debug("Count objects " + AccountDomain.OBJECT_KEY + " in cache");
            return this.getAccountRedisTemplate().opsForHash().size(AccountDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AccountDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }
}
