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
package com.downfy.persistence.repositories.application;

import com.downfy.persistence.domain.application.AppDomain;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/*
 * AppRepository.java
 *
 * App repository
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  1-Dec-2013     tuanta      Create first time
 */
public interface AppRepository {

    public List<AppDomain> findAll();

    public AppDomain findById(long appId);

    public List<AppDomain> findByDeveloper(@Param("developerId") long developerId);

    public long count();

    public long countByDeveloper(@Param("developerId") long developerId);

    public void save(AppDomain domain);

    public void update(AppDomain domain);

    public void updateAppView(AppDomain domain);

    public void updateAppDownload(AppDomain domain);

    public void publish(long appId);

    public void block(long appId);

    public void delete(long appId);
}
