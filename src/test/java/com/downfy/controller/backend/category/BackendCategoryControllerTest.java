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
package com.downfy.controller.backend.category;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
//import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import org.springframework.test.web.servlet.samples.context.SecurityRequestPostProcessors;
//import static com.downfy.common.SecurityRequestPostProcessors.user;
import static com.downfy.common.SecurityRequestPostProcessors.userDeatilsService;
import com.downfy.persistence.domain.category.CategoryDomain;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Tran Anh Tuan <tk1cntt@gmail.com>
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
@DatabaseSetup("db.xml")
public class BackendCategoryControllerTest {

    @Resource
    private FilterChainProxy springSecurityFilterChain;
    @Resource
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    public void testViewCategoryManagerAsAnonymous() throws Exception {
        mockMvc.perform(get("/backend/category"))
                .andDo(print())
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void testViewCategoryManagerAsAccountNotRoleManager() throws Exception {
        mockMvc.perform(get("/backend/category")
                .with(userDeatilsService("test@test.com").userDetailsServiceBeanId("myUserDetailsService")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("accessDenied"));
    }

    @Test
    public void testViewCategoryManagerAsRoleManager() throws Exception {
        mockMvc.perform(get("/backend/category")
                .with(userDeatilsService("tk1cntt@gmail.com").userDetailsServiceBeanId("myUserDetailsService")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("categoryForm", hasProperty("id", is(0))))
                .andExpect(model().attribute("categoryForm", hasProperty("parent", nullValue())))
                .andExpect(model().attribute("categoryForm", hasProperty("name", nullValue())))
                .andExpect(model().attribute("cats", hasSize(2)));
    }
}
