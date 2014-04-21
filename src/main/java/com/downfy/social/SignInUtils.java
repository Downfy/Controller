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
package com.downfy.social;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SignInUtils {

    private static final Logger logger = LoggerFactory.getLogger(SignInUtils.class);

    /**
     * Programmatically signs in the user with the given the user ID.
     *
     * @param localUserId
     */
    public static void signin(String localUserId) {
        logger.info("Auto sign in " + localUserId);
        List<GrantedAuthority> granterdAuthorities = new ArrayList();
        granterdAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if ("tk1cntt@gmail.com".equals(localUserId)) {
            granterdAuthorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
        }

        // set user in secure context
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(localUserId, null, granterdAuthorities));
    }
}
