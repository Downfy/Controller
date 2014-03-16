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
package com.downfy.form.backend.application;

import com.downfy.form.CategorySelectorForm;
import com.downfy.persistence.domain.application.AppDomain;
import java.util.List;

/*
 * AppDetailForm.java
 *
 * App detail form
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  08-Jan-2014     tuanta      Create first time
 */
public class AppDetailForm {

    private long appId;
    private String appName;
    private String appDescription;
    private String appCategory;
    private String appCategoryParent;
    private List<CategorySelectorForm> appCategories;
    private String appIcon;

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

    public String getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    public String getAppCategoryParent() {
        return appCategoryParent;
    }

    public void setAppCategoryParent(String appCategoryParent) {
        this.appCategoryParent = appCategoryParent;
    }

    public List<CategorySelectorForm> getAppCategories() {
        return appCategories;
    }

    public void setAppCategories(List<CategorySelectorForm> appCategories) {
        this.appCategories = appCategories;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public AppDomain toAppDomain() {
        AppDomain domain = new AppDomain();
        domain.setAppName(getAppName());
        domain.setAppDescription(getAppDescription());
        domain.setAppCategory(getAppCategory());
        domain.setAppIcon(getAppIcon());
        return domain;
    }

    public void fromAppDomain(AppDomain domain) {
        setAppId(domain.getAppId());
        setAppName(domain.getAppName());
        setAppDescription(domain.getAppDescription());
        setAppCategory(domain.getAppCategory());
        setAppIcon(domain.getAppIcon());
    }
}
