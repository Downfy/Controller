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
import java.util.Date;
import java.util.List;

/*
 * AppStatusDomain.java
 * 
 * App status domain
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
    private int appCategory;
    private int appView;
    private int appDownload;
    private String appCurrentVersion;
    private long appSize;
    private String appPath;
    private List<String> appScreenShoot;
    private String appIcon;
    private boolean published;
    private boolean deleted;
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

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(int appCategory) {
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

    public String getAppCurrentVersion() {
        return appCurrentVersion;
    }

    public void setAppCurrentVersion(String appCurrentVersion) {
        this.appCurrentVersion = appCurrentVersion;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public List<String> getAppScreenShoot() {
        return appScreenShoot;
    }

    public void setAppScreenShoot(List<String> appScreenShoot) {
        this.appScreenShoot = appScreenShoot;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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
        StringBuilder sb = new StringBuilder();
        sb.append("[App:").append(getAppId()).append(",");
        sb.append("Name:").append(getAppName()).append(",");
        sb.append("Category:").append(getAppCategory()).append("]");
        return sb.toString();
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
