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
package com.downfy.persistence.repositories;

import com.downfy.persistence.domain.AccountDomain;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AccountRepository {

    public List<AccountDomain> findAll();

    public List<AccountDomain> findByLimit(@Param("orderBy") String orderBy, @Param("start") int start, @Param("end") int end);

    public List<AccountDomain> findByEnable(@Param("orderBy") String orderBy, @Param("enabled") boolean enabled, @Param("start") int start, @Param("end") int end);

    public AccountDomain findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    public AccountDomain findById(@Param("id") long id);

    public AccountDomain findByProfileId(@Param("profileId") long profileId);

    public AccountDomain findByUsername(@Param("username") String username);

    public AccountDomain findByEmail(@Param("email") String email);

    public AccountDomain findByMobile(@Param("mobile") String mobile);

    public AccountDomain findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    public long count();

    public void save(AccountDomain domain);

    public void active(@Param("username") String username);

    public void block(@Param("username") String username);

    public void mainProfile(@Param("mainProfile") long mainProfile, @Param("username") String username);

    public void login(@Param("username") String username, @Param("lastHostAddress") String lastHostAddress, @Param("lastLoginTime") Date lastLoginTime);

    public void changePassword(@Param("username") String username, @Param("password") String password, @Param("lastPasswordChangeTime") Date lastPasswordChangeTime);

    public void delete(@Param("username") String username);
}