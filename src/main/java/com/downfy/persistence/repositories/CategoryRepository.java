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

import com.downfy.persistence.domain.CategoryDomain;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author Hadoop Vietnam <admin@hadoopvietnam.com>
 */
public interface CategoryRepository {

    List<CategoryDomain> findAll();

    CategoryDomain findById(int id);

    CategoryDomain findByUrl(@Param(value = "url") String url);

    void save(CategoryDomain domain);

    void update(CategoryDomain domain);
    
    void delete(@Param(value = "url") String url);
}
