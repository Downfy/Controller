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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
    private RedisTemplate<String, AccountDomain> redisTemplate;

    public void setRepository(AccountRepository repository) {
        this.repository = repository;
    }

    public void setRedisTemplate(RedisTemplate<String, AccountDomain> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<AccountDomain> findAll() {
        List<AccountDomain> account = null;
        try {
            this.logger.debug("Find all account in cache.");
            account = getCacheObjects();
            if (account.isEmpty()) {
                this.logger.debug("Find all account in database.");
                account = this.repository.findAll();
                if (!account.isEmpty()) {
                    setCacheObjects(account);
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
            this.logger.debug("Find list account from " + start + " to " + end + " in cache.");
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
            this.logger.debug("Find account " + email + " and password ********* in cache.");
            account = getCacheObject(email);
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
        this.logger.debug("Find email " + email + " in cache.");
        return getCacheObject(email);
    }

    public boolean isExsit(String email) {
        AccountDomain account = getCacheObject(email);
        return account != null;
    }

    public boolean save(AccountDomain domain) {
        try {
            this.logger.debug("Save account " + domain.toString() + " to database");
            this.repository.save(domain);
            putCacheObject(domain);
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
            this.logger.debug("Active account " + domain.getEmail() + " in cache.");
            domain.setEnabled(true);
            putCacheObject(domain);
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
            this.logger.debug("Block account " + domain.getEmail() + " in cache.");
            domain.setEnabled(false);
            putCacheObject(domain);
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
            this.logger.debug("Log time login account " + domain.getEmail() + " in cache");
            domain.setLastHostAddress(lastHostAddress);
            putCacheObject(domain);
            this.logger.debug("Log time login account " + domain.getEmail() + " to database");
            this.repository.login(domain.getEmail(), lastHostAddress, new Date());
        } catch (Exception ex) {
            this.logger.error("Can't update login account " + domain.getEmail(), ex);
            return false;
        }
        return true;
    }

    public boolean changePassword(String key, String password) {
        try {
            this.logger.debug("Change password account " + key + " to cache");
            AccountDomain domain = getCacheObject(key);
            if (domain != null) {
                domain.setPassword(password);
                putCacheObject(domain);
                this.logger.debug("Change password account " + domain.getEmail() + " to database");
                this.repository.changePassword(domain.getEmail(), password, new Date());
            }
        } catch (Exception ex) {
            this.logger.error("Can't change password account " + key, ex);
            return false;
        }
        return true;
    }

    public boolean delete(String key) {
        try {
            this.logger.debug("Delete account " + key + " in cache.");
            removeCacheObject(key);
            this.logger.debug("Delete account " + key + " in database.");
            this.repository.delete(key);
            this.logger.debug(getCacheObjects().toString());
        } catch (Exception ex) {
            this.logger.error("Can't delete account " + key, ex);
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

            this.redisTemplate.opsForHash().put(domain.getObjectKey(), domain.getKey(), domain);
        } catch (Exception ex) {
            this.logger.warn("Can't put data to cache", ex);
        }
    }

    @Override
    public AccountDomain getCacheObject(String key) {
        AccountDomain domain = null;
        try {
            this.logger.debug("Get key " + key + " object " + AccountDomain.OBJECT_KEY + " in cache");
            domain = (AccountDomain) redisTemplate.opsForHash().get(AccountDomain.OBJECT_KEY, key);
            if (domain == null) {
                this.logger.debug("Get key " + key + " object " + AccountDomain.OBJECT_KEY + " in database");
                domain = repository.findByEmail(key);
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
            this.redisTemplate.opsForHash().delete(AccountDomain.OBJECT_KEY, key);
        } catch (Exception ex) {
            this.logger.warn("Can't remove from Redis", ex);
        }
    }

    @Override
    public List<AccountDomain> getCacheObjects() {
        List<AccountDomain> users = new ArrayList<AccountDomain>();
        try {
            this.logger.debug("Get all objects " + AccountDomain.OBJECT_KEY + " in cache");
            for (Object user : redisTemplate.opsForHash().values(AccountDomain.OBJECT_KEY)) {
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
            List users_ = redisTemplate.opsForHash().values(AccountDomain.OBJECT_KEY);
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
                putCacheObject(domain);
            }
        } catch (Exception ex) {
            this.logger.warn("Can't set objects " + AccountDomain.OBJECT_KEY + " to Redis", ex);
        }
    }

    @Override
    public long countCacheObject() {
        try {
            this.logger.debug("Count objects " + AccountDomain.OBJECT_KEY + " in cache");
            return redisTemplate.opsForHash().size(AccountDomain.OBJECT_KEY);
        } catch (Exception ex) {
            this.logger.warn("Can't count objects " + AccountDomain.OBJECT_KEY + " from Redis", ex);
        }
        return 0;
    }
}
