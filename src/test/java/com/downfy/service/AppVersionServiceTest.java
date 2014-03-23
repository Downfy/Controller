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

import com.downfy.persistence.domain.application.AppVersionDomain;
import com.downfy.service.application.AppVersionService;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*
 * AppServiceTest.java
 *
 * App service test
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  2-Dec-2013     tuanta      Create first time
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-mybatis.xml",
    "classpath:META-INF/spring/applicationContext-redis.xml"})
public class AppVersionServiceTest {

    @Autowired
    AppVersionService service;

    @Before
    public void clearCache() {
        service.clearCache(1234567890);
    }

    @Test
    public void testConfigure() {
        Assert.assertNotNull(service);
    }

    @Test
    public void testFindById() {
        AppVersionDomain app = service.findById(10000l);
        Assert.assertEquals(null, app);
    }

    @Test
    public void testRepository() {
        long time = System.currentTimeMillis() - 138600000000l;
        AppVersionDomain app = new AppVersionDomain();
        app.setId(time);
        app.setAppId(time);
        app.setAppPath("test");
        app.setAppSize(1l);
        app.setAppVersion("1.0.0");
        app.setCreater(1234567890);
        app.setCreated(new Date());
        app.setUpdated(new Date());
        List<AppVersionDomain> apps = service.findByApp(1234567890);
        Assert.assertTrue(apps.isEmpty());
        Assert.assertTrue(service.save(app));
        apps = service.findByApp(1234567890);
        Assert.assertTrue(!apps.isEmpty());
        for (AppVersionDomain appDomain : apps) {
            Assert.assertTrue(service.delete(appDomain.getId(), 1234567890));
        }
        apps = service.findByApp(1234567890);
        Assert.assertTrue(apps.isEmpty());
    }

    @Test
    public void testExsit() {
        Assert.assertEquals(false, service.isExsit(1234567890));
    }

    @Test
    public void testPublish() {
        Assert.assertEquals(false, service.publish(1234567890));
    }

    @Test
    public void testBlock() {
        Assert.assertEquals(false, service.block(1234567890));
    }

    @Test
    public void testDelete() {
        Assert.assertEquals(true, service.delete(1234567890l, 1234567890));
    }
}
