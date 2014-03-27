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

import com.downfy.common.AppCommon;
import com.downfy.common.Utils;
import com.downfy.persistence.domain.application.AppApkDomain;
import com.downfy.persistence.domain.application.AppUploadedDomain;
import com.downfy.persistence.domain.application.AppVersionDomain;
import java.util.Date;
import net.dongliu.apk.parser.bean.ApkMeta;

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

    private long apkId;
    private long appId;
    private String appVersion;
    private String appPackage;
    private String appPath;
    private long appSize;

    public long getApkId() {
        return apkId;
    }

    public void setApkId(long apkId) {
        this.apkId = apkId;
    }

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

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
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

    public AppApkDomain toAppApk(String localPath, AppUploadedDomain uploadedDomain) {
        ApkMeta apkMeta = Utils.getApkMeta(localPath);
        if (apkMeta != null) {
            AppApkDomain apkDomain = new AppApkDomain();
            apkDomain.setId(System.currentTimeMillis());
            apkDomain.setAppId(uploadedDomain.getAppId());
            apkDomain.setAppPath(uploadedDomain.getAppPath());
            apkDomain.setStatus(AppCommon.CREATED);
            apkDomain.setGlEsVersion(apkMeta.getGlEsVersion());
            apkDomain.setLabel(apkMeta.getLabel());
            apkDomain.setMaxSdkVersion(apkMeta.getMaxSdkVersion());
            apkDomain.setMinSdkVersion(apkMeta.getMinSdkVersion());
            apkDomain.setTargetSdkVersion(apkMeta.getTargetSdkVersion());
            apkDomain.setPackageName(apkMeta.getPackageName());
            apkDomain.setPermissions(apkMeta.getPermissions());
            apkDomain.setUseFeatures(apkMeta.getUseFeatures());
            apkDomain.setVersionCode(apkMeta.getVersionCode());
            apkDomain.setVersionName(apkMeta.getVersionName());
            return apkDomain;
        }
        return null;
    }

    public AppVersionDomain toAppVersion(String localPath, AppUploadedDomain uploadedDomain) {
        ApkMeta apkMeta = Utils.getApkMeta(localPath);
        if (apkMeta != null) {
            AppVersionDomain versionDomain = new AppVersionDomain();
            versionDomain.setId(System.currentTimeMillis());
            versionDomain.setAppId(uploadedDomain.getAppId());
            versionDomain.setAppPath(uploadedDomain.getAppPath());
            versionDomain.setStatus(AppCommon.CREATED);
            versionDomain.setAppVersion(apkMeta.getVersionName());
            versionDomain.setCreated(new Date());
            return versionDomain;
        }
        return null;
    }
}
