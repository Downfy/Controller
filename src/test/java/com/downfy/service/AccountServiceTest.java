/*
 * Copyright 2013 Downfy Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.downfy.service;

import com.downfy.persistence.domain.AccountDomain;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*
 * AccountServiceTest.java
 *
 * Account service test
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-mybatis.xml",
    "classpath:META-INF/spring/applicationContext-redis.xml"})
public class AccountServiceTest {

    @Autowired
    AccountService service;

    @Test
    public void testConfigure() {
        Assert.assertNotNull(service);
    }

    @Test
    public void testFindAll() {
        List<AccountDomain> accounts = service.findAll();
        Assert.assertTrue(accounts.size() > 0);
    }

    @Test
    public void testFindByLimit() {
        List<AccountDomain> accounts = service.findByLimit(0, 10);
        Assert.assertTrue(accounts.isEmpty());
    }

    @Test
    public void testRepository() {
        AccountDomain account = new AccountDomain();
        account.setId(System.currentTimeMillis());
        account.setPassword("test");
        account.setEmail("test@test.com");
        service.save(account);
        account = service.findByEmail("test@test.com");
        Assert.assertNotNull(account);
        Assert.assertEquals(true, service.changePassword("test@test.com", "test001"));
        account = service.findByEmailAndPassword("test@test.com", "test001");
        Assert.assertNotNull(account);
        Assert.assertEquals("test@test.com", account.getEmail());
        Assert.assertEquals(true, service.delete("test@test.com"));
    }

    @Test
    public void testFindByEmail() {
        service.findByEmail("test@test.com");
    }

    @Test
    public void testChangePassword() {
        service.changePassword("test@test.com", "test001");
    }

    @Test
    public void testFindByEmailAndPassword() {
        service.findByEmailAndPassword("test@test.com", "test001");
    }

    @Test
    public void testDelete() {
        service.delete("test@test.com");
    }

    @Test
    public void testExsit() {
        Assert.assertEquals(false, service.isExsit(1234567890));
    }
}
