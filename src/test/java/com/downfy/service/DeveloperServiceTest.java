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

import com.downfy.service.developer.DeveloperService;
import com.downfy.persistence.domain.developer.DeveloperDomain;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*
 * DeveloperServiceTest.java
 * 
 * Developer service test
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  03-Jan-2014     tuanta      Create first time
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-mybatis.xml",
    "classpath:META-INF/spring/applicationContext-redis.xml"})
public class DeveloperServiceTest {

    @Autowired
    DeveloperService service;

    @Test
    public void testConfigure() {
        Assert.assertNotNull(service);
    }

    @Test
    public void testRepository() {
        DeveloperDomain developer = new DeveloperDomain();
        developer.setDeveloperId("test");
        developer.setDeveloperName("test");
        developer.setDeveloperInfo("test");
        Assert.assertEquals(true, service.save(developer));
        developer = service.findById("test");
        Assert.assertEquals("test", developer.getDeveloperId());
        Assert.assertEquals(true, service.delete(developer.getKey()));
    }

    @Test
    public void testExsit() {
        Assert.assertEquals(false, service.isExsit("test"));
    }

    @Test
    public void testClearCache() {
        service.clearCache();
        List<DeveloperDomain> developers = service.getCacheObjects();
        Assert.assertTrue(developers.isEmpty());
    }
}
