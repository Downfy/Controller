/*
 * Copyright 2013 the original author or authors.
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
package com.downfy.controller.member;

import com.downfy.common.Utils;
import com.downfy.persistence.domain.AccountDomain;
import com.downfy.service.AccountService;
import com.downfy.social.SignInUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping("/autosignup")
public class SignupController {

    @Autowired
    RememberMeServices rememberMeServices;

    @Autowired
    AccountService accountService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String signupForm(NativeWebRequest request, WebRequest webRequest) {
        ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils();
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(webRequest);
        if (connection != null) {
            SignInUtils.signin(connection.fetchUserProfile().getEmail());
            providerSignInUtils.doPostSignUp(connection.fetchUserProfile().getEmail(), webRequest);
            AccountDomain account = accountService.findByEmail(connection.fetchUserProfile().getEmail());
            if (account == null) {
                account = new AccountDomain();
                account.setId(System.currentTimeMillis());
                account.setPassword(Utils.toMd5(connection.fetchUserProfile().getUsername() + ":" + connection.fetchUserProfile().getEmail()));
                account.setEmail(connection.fetchUserProfile().getEmail());
                account.setEnabled(true);
                accountService.save(account);
            }

            HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request.getNativeRequest(HttpServletRequest.class)) {
                @Override
                public String getParameter(String name) {
                    return "true";
                }
            };

            rememberMeServices.loginSuccess(wrapper, request.getNativeResponse(HttpServletResponse.class), SecurityContextHolder.getContext().getAuthentication());
            return "redirect:/member.html";
        }
        return "redirect:/login.html";
    }
}
