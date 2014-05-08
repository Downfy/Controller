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
package com.downfy.form.application;

/**
 *
 * @author Tran Anh Tuan <tk1cntt@gmail.com>
 */
public class AppReviewForm {

    private long appId;
    private String appReviewDescription;
    private float appReviewPoint;

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

}
