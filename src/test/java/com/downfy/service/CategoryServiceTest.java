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

import com.downfy.service.category.CategoryService;
import com.downfy.persistence.domain.category.CategoryDomain;
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
 * CategoryServiceTest.java
 * 
 * Account service test
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  27-Nov-2013     tuanta      Create first time
 *  26-Jan-2014     tuanta      Add intergration test using dbunit 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-mybatis.xml",
    "classpath:META-INF/spring/applicationContext-redis.xml"})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
public class CategoryServiceTest {

    @Autowired
    CategoryService service;

    @Test
    public void testConfigure() {
        Assert.assertNotNull(service);
    }

    @Test
    @DatabaseSetup("categoryData.xml")
    public void testFindAll() {
        service.clearCache();
        List<CategoryDomain> categories = service.findAll();
        Assert.assertEquals(2, categories.size());
    }

    @Test
    @DatabaseSetup("categoryData-expected.xml")
    public void testFindByUrl() {
        service.clearCache();
        CategoryDomain category = service.findByURL("test");
        Assert.assertEquals("test", category.getName());
        Assert.assertEquals("test", category.getUrl());
        Assert.assertEquals("TEST", category.getParent());
    }

    @Test
    @DatabaseSetup("categoryData.xml")
    @ExpectedDatabase(value = "categoryData-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testSave() {
        service.clearCache();
        List<CategoryDomain> categories = service.findAll();
        Assert.assertEquals(2, categories.size());
        CategoryDomain category = new CategoryDomain();
        category.setName("test");
        category.setUrl("test");
        category.setParent("TEST");
        Assert.assertEquals(true, service.save(category));
        categories = service.findAll();
        Assert.assertEquals(3, categories.size());
    }

    @Test
    @DatabaseSetup("categoryData-expected.xml")
    @ExpectedDatabase(value = "categoryData.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testDelete() {
        Assert.assertEquals(true, service.isExsit("test"));
        Assert.assertEquals(true, service.delete("test"));
    }

    @Test
    public void testClearCache() {
        service.clearCache();
        List<CategoryDomain> categorys = service.getCacheObjects();
        Assert.assertTrue(categorys.isEmpty());
    }
}
