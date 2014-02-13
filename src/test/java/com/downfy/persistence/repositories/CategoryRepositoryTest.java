/*
 * Copyright 2012 Hadoop Vietnam <admin@hadoopvietnam.com>.
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
package com.downfy.persistence.repositories;

import com.downfy.persistence.repositories.category.CategoryRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.downfy.persistence.domain.category.CategoryDomain;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/*
 * CategoryRepositoryTest.java
 *
 * Category repository test
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-mybatis.xml"})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository repository;

    @Test
    public void testConfigure() {
        Assert.assertNotNull(repository);
    }

    @Test
    @DatabaseSetup("repositoryDB.xml")
    public void testSave() {
        CategoryDomain domain = new CategoryDomain();
        domain.setName("Arcade & Action");
        domain.setUrl("Arcade-Action");
        repository.save(domain);
    }

    @Test
    @DatabaseSetup("repositoryDB.xml")
    public void testUpdateUrl() {
        CategoryDomain category = repository.findByUrl("action");
        Assert.assertNotNull(category);
        category.setName("New-Action");
        repository.update(category);
        category = repository.findByUrl("action");
        Assert.assertNotNull(category);
        Assert.assertEquals(category.getName(), "New-Action");
    }

    @Test
    @DatabaseSetup("repositoryDB.xml")
    public void testFindByUrl() {
        CategoryDomain domain = repository.findByUrl("action");
        Assert.assertEquals(domain.getName(), "Action");
        Assert.assertEquals(domain.getUrl(), "action");
    }
}
