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

import com.downfy.persistence.domain.application.AppDomain;
import com.downfy.persistence.domain.application.AppVersionDomain;

/**
 *
 * @author Tran Anh Tuan <tk1cntt@gmail.com>
 */
public class AppVersionDownloadForm {

    private long appId;
    private String appName;
    private long appDownloaded;
    private String appPath;
    private String appVersion;
    private int status;

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

    public long getAppDownloaded() {
        return appDownloaded;
    }

    public void setAppDownloaded(long appDownloaded) {
        this.appDownloaded = appDownloaded;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void fromAppDomain(AppDomain domain) {
        setAppId(domain.getAppId());
        setAppName(domain.getAppName());
    }

    public void fromAppVersionDomain(AppVersionDomain domain) {
        setAppPath(domain.getAppPath());
        setAppVersion(domain.getAppVersion());
        setStatus(domain.getStatus());
    }
}
