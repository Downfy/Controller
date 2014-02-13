/*
 * Copyright (C) 2013 Downfy Team
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
package com.downfy.security;

import com.downfy.common.RegexpUtils;
import com.downfy.persistence.domain.AccountDomain;
import com.downfy.service.AccountService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.GrantedAuthority;

/*
 * MyUserDetailsService.java
 *
 * Version 1.0
 *
 * Date 26/11/2013
 *
 * User details service
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
public class MyUserDetailsService
        implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException, DataAccessException {
        List<GrantedAuthority> granterdAuthorities = new ArrayList();
        granterdAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if ("admin@admin.com".equals(username)) {
            granterdAuthorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
        }

        AccountDomain account = null;
        if (RegexpUtils.validateEmail(username)) {
            account = this.accountService.findByEmail(username);
        }
        if (account == null) {
            throw new UsernameNotFoundException("Account not found.");
        }
        return new User(account.getEmail(), account.getPassword(), account.isEnabled(), true, true, true, granterdAuthorities);
    }
}
