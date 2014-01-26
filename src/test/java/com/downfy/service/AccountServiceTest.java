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
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

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
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
public class AccountServiceTest {

    @Autowired
    AccountService service;

    @Test
    public void testConfigure() {
        Assert.assertNotNull(service);
    }

    @Test
    @DatabaseSetup("userDB.xml")
    public void testFindAll() {
        service.clearCache();
        List<AccountDomain> accounts = service.findAll();
        Assert.assertEquals(accounts.size(), 3);
    }

//    @Test
//    @DatabaseSetup("userDB.xml")
//    public void testFindByLimit() {
//        service.clearCache();
//        List<AccountDomain> accounts = service.findAll();
//        Assert.assertEquals(3, accounts.size());
//        accounts = service.findByLimit(0, 2);
//        Assert.assertEquals(2, accounts.size());
//    }
    @Test
    @DatabaseSetup("userDB.xml")
    @ExpectedDatabase(value = "userDB-add-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testSave() {
        service.clearCache();
        List<AccountDomain> accounts = service.findAll();
        Assert.assertEquals(3, accounts.size());
        AccountDomain account = new AccountDomain();
        account.setId(4);
        account.setPassword("new");
        account.setEmail("new@new.com");
        account.setEnabled(true);
        Assert.assertTrue(service.save(account));
        accounts = service.findAll();
        Assert.assertEquals(4, accounts.size());
        Assert.assertTrue(service.isExsit(4l));
    }

    @Test
    @DatabaseSetup("userDB.xml")
    public void testFindByEmail() {
        AccountDomain domain = service.findByEmail("test@test.com");
        Assert.assertNotNull(domain);
        Assert.assertEquals("test@test.com", domain.getEmail());
    }

    @Test
    @DatabaseSetup("userDB.xml")
    @ExpectedDatabase(value = "userDB-changepassword-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testChangePassword() {
        Assert.assertTrue(service.changePassword("test@test.com", "test001"));
    }

    @Test
    @DatabaseSetup("userDB.xml")
    public void testFindByEmailAndPassword() {
        service.clearCache();
        Assert.assertNotNull(service.findByEmailAndPassword("test@test.com", "test"));
    }

    @Test
    @DatabaseSetup("userDB.xml")
    @ExpectedDatabase(value = "userDB.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testActive() {
        AccountDomain domain = service.findByEmail("test@test.com");
        service.active(domain);
    }

    @Test
    @DatabaseSetup("userDB.xml")
    @ExpectedDatabase(value = "userDB-block-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testBlock() {
        AccountDomain domain = service.findByEmail("test@test.com");
        service.block(domain);
    }

    @Test
    @DatabaseSetup("userDB.xml")
    @ExpectedDatabase(value = "userDB-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testDelete() {
        service.delete("test@test.com");
    }

    @Test
    @DatabaseSetup("userDB.xml")
    public void testExsit() {
        Assert.assertEquals(false, service.isExsit(0l));
        Assert.assertEquals(true, service.isExsit(1l));
        Assert.assertEquals(true, service.isExsit(2l));
        Assert.assertEquals(true, service.isExsit(3l));
    }
}
