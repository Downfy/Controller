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
package com.downfy.persistence.domain.admin.developer;

import com.downfy.common.ObjectKey;
import com.downfy.persistence.domain.DomainObject;
import java.util.Date;

/*
 * AdminDeveloperDomain.java
 * 
 * Admin developer domain
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  27-Nov-2013     tuanta      Create first time
 */
public class AdminDeveloperDomain
        implements DomainObject {

    public static final String OBJECT_KEY = ObjectKey.USER_DEVELOPER;
    private long id;
    private String developerId;
    private String developerInfo;
    private String developerName;
    private Date created;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(String developerId) {
        this.developerId = developerId;
    }

    public String getDeveloperInfo() {
        return developerInfo;
    }

    public void setDeveloperInfo(String developerInfo) {
        this.developerInfo = developerInfo;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Developer Id:").append(getDeveloperId()).append("]");
        return sb.toString();
    }

    @Override
    public String getKey() {
        return getDeveloperId();
    }

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }
}
