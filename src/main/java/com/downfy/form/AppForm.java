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
package com.downfy.form;

import com.downfy.persistence.domain.application.AppDomain;

/*
 * AppForm.java
 *
 * App form
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  05-Jan-2014     tuanta      Create first time
 */
public class AppForm {

    private long appId;
    private String appName;
    private String appDescription;
    private int appCategory;

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public int getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(int appCategory) {
        this.appCategory = appCategory;
    }

    public AppDomain toAppDomain() {
        AppDomain domain = new AppDomain();
        domain.setAppName(getAppName());
        domain.setAppDescription(getAppDescription());
        domain.setAppCategory(getAppCategory());
        return domain;
    }
}
