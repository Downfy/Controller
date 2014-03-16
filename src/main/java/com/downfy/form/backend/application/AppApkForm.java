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

import com.downfy.persistence.domain.application.AppVersionDomain;

/*
 * AppApkForm.java
 *
 * App apk form
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  15-Mar-2014     tuanta      Create first time
 */
public class AppApkForm {

    private long appId;
    private String appVersion;
    private String appPath;
    private long appSize;

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public AppVersionDomain toAppVersion() {
        AppVersionDomain versionDomain = new AppVersionDomain();
        versionDomain.setAppId(getAppId());
        versionDomain.setAppPath(getAppPath());
        versionDomain.setAppSize(getAppSize());
        versionDomain.setAppVersion(getAppVersion());
        return versionDomain;
    }

}
