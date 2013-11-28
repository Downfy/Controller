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

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.downfy.persistence.domain.CategoryDomain;
import org.springframework.dao.DuplicateKeyException;

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
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository repository;

    @Test
    public void testConfigure() {
        Assert.assertNotNull(repository);
    }

    @Test
    public void testSave() {
        try {
            CategoryDomain domain = new CategoryDomain();
            domain.setName("Arcade & Action");
            domain.setUrl("Arcade-Action");
            repository.save(domain);
        } catch (DuplicateKeyException dkex) {
        }
    }

    @Test
    public void testUpdateUrl() {
        List<CategoryDomain> domains = repository.findAll();
        for (CategoryDomain category : domains) {
            category.setUrl(System.currentTimeMillis() + "");
            repository.update(category);
        }
    }
}
