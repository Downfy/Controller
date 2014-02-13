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

import com.downfy.common.AppCommon;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
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
@DatabaseSetup("appCreateDB-empty-app.xml")
public class AppCreateControllerTest {

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
    public void testViewAppCreateManagerAsAnonymous() throws Exception {
        mockMvc.perform(get("/backend/application/create"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void testViewAppCreateAsAccountNotRoleManager() throws Exception {
        mockMvc.perform(get("/backend/application/create")
                .with(userDeatilsService("test@test.com").userDetailsServiceBeanId("myUserDetailsService")))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("accessDenied"));
    }

    @Test
    public void testViewAppCreateAsAccountRoleManager() throws Exception {
        mockMvc.perform(get("/backend/application/create")
                .with(userDeatilsService("admin@admin.com").userDetailsServiceBeanId("myUserDetailsService")))
                .andExpect(status().isOk())
                .andExpect(view().name("home/backend/application/create"));
    }

    @Test
    public void testAddAppCreateAsAccountRoleManagerCheckParams() throws Exception {
        mockMvc.perform(post("/backend/application/create")
                .with(userDeatilsService("admin@admin.com").userDetailsServiceBeanId("myUserDetailsService")))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("applicationForm", "appName"))
                .andExpect(model().attributeHasFieldErrors("applicationForm", "appCategory"))
                .andExpect(model().attributeHasFieldErrors("applicationForm", "appCategoryParent"))
                .andExpect(view().name("home/backend/application/create"));

        mockMvc.perform(post("/backend/application/create")
                .param("appName", "App test")
                .with(userDeatilsService("admin@admin.com").userDetailsServiceBeanId("myUserDetailsService")))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("applicationForm", "appCategory"))
                .andExpect(model().attributeHasFieldErrors("applicationForm", "appCategoryParent"))
                .andExpect(view().name("home/backend/application/create"));

        mockMvc.perform(post("/backend/application/create")
                .param("appCategory", "App category test")
                .with(userDeatilsService("admin@admin.com").userDetailsServiceBeanId("myUserDetailsService")))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("applicationForm", "appName"))
                .andExpect(model().attributeHasFieldErrors("applicationForm", "appCategoryParent"))
                .andExpect(view().name("home/backend/application/create"));

        mockMvc.perform(post("/backend/application/create")
                .param("appCategoryParent", AppCommon.CATEGORY_APPLICATION)
                .with(userDeatilsService("admin@admin.com").userDetailsServiceBeanId("myUserDetailsService")))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("applicationForm", "appName"))
                .andExpect(model().attributeHasFieldErrors("applicationForm", "appCategory"))
                .andExpect(view().name("home/backend/application/create"));
    }

    @Test
    public void testAddAppCreateAsAccountRoleManager() throws Exception {
        mockMvc.perform(post("/backend/application/create")
                .param("appName", "App test")
                .param("appCategory", "App category test")
                .param("appCategoryParent", AppCommon.CATEGORY_APPLICATION)
                .with(userDeatilsService("admin@admin.com").userDetailsServiceBeanId("myUserDetailsService")))
                .andDo(print())
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/backend/application.html"));
    }
}
