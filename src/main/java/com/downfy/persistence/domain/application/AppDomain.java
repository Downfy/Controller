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
package com.downfy.persistence.domain.application;

import com.downfy.common.ObjectKey;
import com.downfy.persistence.domain.DomainObject;
import com.google.common.base.Objects;
import java.util.Date;

/*
 * AppDomain.java
 *
 * App domain
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  29-Nov-2013     tuanta      Create first time
 */
public class AppDomain implements DomainObject {

    public static final String OBJECT_KEY = ObjectKey.APP;
    private long appId;
    private String appName;
    private String appDescription;
    private String appReviewTitle;
    private String appReviewDescription;
    private String appAuthor;
    private String appCategory;
    private int appView;
    private int appDownload;
    private String appPackage;
    private String appIcon;
    private int status;
    private Date created;
    private Date updated;
    private long creater;
    private long updater;

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public String getAppAuthor() {
        return appAuthor;
    }

    public void setAppAuthor(String appAuthor) {
        this.appAuthor = appAuthor;
    }

    public String getAppReviewTitle() {
        return appReviewTitle;
    }

    public void setAppReviewTitle(String appReviewTitle) {
        this.appReviewTitle = appReviewTitle;
    }

    public String getAppReviewDescription() {
        return appReviewDescription;
    }

    public void setAppReviewDescription(String appReviewDescription) {
        this.appReviewDescription = appReviewDescription;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    public int getAppView() {
        return appView;
    }

    public void setAppView(int appView) {
        this.appView = appView;
    }

    public int getAppDownload() {
        return appDownload;
    }

    public void setAppDownload(int appDownload) {
        this.appDownload = appDownload;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public long getCreater() {
        return creater;
    }

    public void setCreater(long creater) {
        this.creater = creater;
    }

    public long getUpdater() {
        return updater;
    }

    public void setUpdater(long updater) {
        this.updater = updater;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("App", getAppId())
                .add("Name", getAppName())
                .add("Description", getAppDescription())
                .add("Status", getStatus())
                .add("Category", getAppCategory())
                .toString();
    }

    @Override
    public String getKey() {
        return getAppId() + "";
    }

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }
}
