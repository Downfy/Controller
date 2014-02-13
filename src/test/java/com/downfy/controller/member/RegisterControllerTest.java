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
package com.downfy.controller.member;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
@DatabaseSetup("registerDB.xml")
public class RegisterControllerTest {

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
    public void testRegisterAccountGetForm() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/member/create"));
    }

    @Test
    public void testRegisterAccountCheckParams() throws Exception {
        accountService.clearCache();
        mockMvc.perform(post("/signup"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("registerForm", "email"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "rePassword"));

        mockMvc.perform(post("/signup")
                .param("email", "aaaaaaaaa"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("registerForm", "email"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "rePassword"));

        mockMvc.perform(post("/signup")
                .param("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("registerForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "rePassword"));

        mockMvc.perform(post("/signup")
                .param("email", "test1@test.com")
                .param("password", "test"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("registerForm", "rePassword"));

        mockMvc.perform(post("/signup")
                .param("email", "test1@test.com")
                .param("rePassword", "test"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("registerForm", "password"));

        mockMvc.perform(post("/signup")
                .param("password", "test")
                .param("rePassword", "test"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("registerForm", "email"));
    }

    @Test
    public void testRegisterAccountSuccess() throws Exception {
        mockMvc.perform(post("/signup")
                .param("email", "new@new.com")
                .param("password", "new")
                .param("rePassword", "new"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/member/successregister"));
    }
}
