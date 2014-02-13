/*
 * Copyright (C) 2014 Downfy Team
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
package com.downfy.controller.backend.app;

import static com.downfy.common.SecurityRequestPostProcessors.userDeatilsService;
import com.downfy.service.AccountService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Tran Anh Tuan<tk1cntt@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:applicationContext-mybatis.xml",
    "classpath:META-INF/spring/applicationContext-redis.xml",
    "classpath:META-INF/spring/webmvc-config.xml",
    "classpath:META-INF/spring/applicationContext-security.xml"
})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
@WebAppConfiguration
public class AppControllerTest {

    @Resource
    private FilterChainProxy springSecurityFilterChain;
    @Resource
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @Autowired
    AccountService accountService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    public void testViewAppManagerAsAnonymous() throws Exception {
        mockMvc.perform(get("/backend/application"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void testViewAppAsAccountNotRoleManager() throws Exception {
        mockMvc.perform(get("/backend/application")
                .with(userDeatilsService("test@test.com").userDetailsServiceBeanId("myUserDetailsService")))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("accessDenied"));
    }

    @Test
    @DatabaseSetup("appCreateDB.xml")
    public void testViewAppAsAccountRoleManager() throws Exception {
        mockMvc.perform(get("/backend/application")
                .with(userDeatilsService("admin@admin.com").userDetailsServiceBeanId("myUserDetailsService")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("apps", hasSize(0)))
                .andExpect(view().name("home/backend/application"));
    }

    @Test
    @DatabaseSetup("appCreateDB.xml")
    public void testViewDetailAppAsAccountRoleManager() throws Exception {
        mockMvc.perform(get("/backend/application/1234567890")
                .with(userDeatilsService("admin@admin.com").userDetailsServiceBeanId("myUserDetailsService")))
                .andExpect(status().isOk())
                .andExpect(view().name("home/backend/application/detail"));
    }
//    @Test
//    @DatabaseSetup("appCreateDB.xml")
//    public void testUploadIconOfAppAsAccountRoleManager() throws Exception {
//        FileInputStream fin = new FileInputStream("icon.png");
//        MockMultipartFile multipartFile = new MockMultipartFile("appIconFile", fin);
//        mockMvc.perform(fileUpload("/backend/application/1234567890/icon")
//                .file(multipartFile)
//                .with(userDeatilsService("admin@admin.com").userDetailsServiceBeanId("myUserDetailsService"))
//                .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
}
