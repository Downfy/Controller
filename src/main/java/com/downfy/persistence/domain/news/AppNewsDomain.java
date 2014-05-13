/*
 * Copyright (C) 2014 Downfy Team
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
package com.downfy.persistence.domain.news;

import com.downfy.common.ObjectKey;
import com.downfy.persistence.domain.DomainObject;
import static com.downfy.persistence.domain.category.AppCategoryDomain.OBJECT_KEY;
import java.util.Date;

/**
 *
 * @author Tran Anh Tuan <tk1cntt@gmail.com>
 */
public class AppNewsDomain implements DomainObject {

    public static final String OBJECT_KEY = ObjectKey.APP_NEWS;
    private long id;
    private long appId;
    private float appPoint;
    private String appTitle;
    private String appDescription;
    private int type;
    private int status;
    private long creater;
    private Date created;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public float getAppPoint() {
        return appPoint;
    }

    public void setAppPoint(float appPoint) {
        this.appPoint = appPoint;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreater() {
        return creater;
    }

    public void setCreater(long creater) {
        this.creater = creater;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String getKey() {
        return getId() + "";
    }

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }
}
