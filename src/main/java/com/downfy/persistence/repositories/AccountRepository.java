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

/*
 * AccountRepository.java
 * 
 * Account repository
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  1-Dec-2013     tuanta      Create first time
 */
public interface AccountRepository {

    public List<AccountDomain> findAll();

    public List<AccountDomain> findByLimit(@Param("orderBy") String orderBy, @Param("start") int start, @Param("end") int end);

    public AccountDomain findById(@Param("id") long id);

    public AccountDomain findByEmail(@Param("email") String email);

    public long count();

    public void save(AccountDomain domain);

    public void active(@Param("id") long id);

    public void block(@Param("id") long id);

    public void login(@Param("id") long id, @Param("lastHostAddress") String lastHostAddress, @Param("lastLoginTime") Date lastLoginTime);

    public void changePassword(@Param("id") long id, @Param("password") String password, @Param("lastPasswordChangeTime") Date lastPasswordChangeTime);

    public void delete(@Param("id") long id);
}
