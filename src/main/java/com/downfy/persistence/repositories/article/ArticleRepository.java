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
package com.downfy.persistence.repositories.article;

import com.downfy.persistence.domain.article.ArticleDomain;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/*
 * ArticleRepository.java
 *
 * App article repository
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  1-Dec-2013     tuanta      Create first time
 */
public interface ArticleRepository {

    public List<ArticleDomain> findAll();

    public List<ArticleDomain> findByCreater(@Param("creater") long creater);

    public ArticleDomain findById(@Param("id") long id);

    public long count();

    public long save(ArticleDomain domain);

    public void publish(@Param("id") long id);

    public void approve(@Param("id") long id);

    public void block(@Param("id") long id);

    public void delete(@Param("id") long id);
}
