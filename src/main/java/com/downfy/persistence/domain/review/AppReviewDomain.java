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
package com.downfy.persistence.domain.review;

import java.util.Date;

/**
 *
 * @author Tran Anh Tuan <tk1cntt@gmail.com>
 */
public class AppReviewDomain {

    private long id;
    private long appId;
    private String appReviewDescription;
    private float appReviewPoint;
    private String appReviewTitle;
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

    public String getAppReviewDescription() {
        return appReviewDescription;
    }

    public void setAppReviewDescription(String appReviewDescription) {
        this.appReviewDescription = appReviewDescription;
    }

    public float getAppReviewPoint() {
        return appReviewPoint;
    }

    public void setAppReviewPoint(float appReviewPoint) {
        this.appReviewPoint = appReviewPoint;
    }

    public String getAppReviewTitle() {
        return appReviewTitle;
    }

    public void setAppReviewTitle(String appReviewTitle) {
        this.appReviewTitle = appReviewTitle;
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

}
