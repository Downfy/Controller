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
package com.downfy.persistence.repositories.developer;

import com.downfy.persistence.domain.developer.DeveloperDomain;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/*
 * DeveloperRepository.java
 * 
 * Developer repository
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  3-Dec-2013     tuanta      Create first time
 */
public interface DeveloperRepository {

    List<DeveloperDomain> findAll();

    DeveloperDomain findById(@Param(value = "id") String developerId);

    void save(DeveloperDomain domain);

    void update(DeveloperDomain domain);

    void delete(@Param(value = "id") String developerId);
}
