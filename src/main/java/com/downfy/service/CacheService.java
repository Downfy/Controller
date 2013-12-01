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
package com.downfy.service;

/**
 *
 * @author Tran Anh Tuan <tk1cntt@gmail.com>
 */
import com.downfy.persistence.domain.DomainObject;
import java.util.List;

public interface CacheService<V extends DomainObject> {

    void putCacheObject(V obj);

    V getCacheObject(String key);

    void removeCacheObject(String key);

    List<V> getCacheObjects();

    List<V> getCacheLimitObjects(int start, int end);

    long countCacheObject();

    void setCacheObjects(List<V> objects);
}
