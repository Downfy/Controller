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
package com.downfy.persistence.domain.application;

import com.downfy.common.ObjectKey;
import com.downfy.common.Utils;
import com.downfy.persistence.domain.DomainObject;
import net.dongliu.apk.parser.bean.ApkMeta;

/**
 *
 * @author Tran Anh Tuan<tk1cntt@gmail.com>
 */
public class AppApkDomain extends ApkMeta implements DomainObject {

    public static final String OBJECT_KEY = ObjectKey.APP_APK;
    private long id;
    private long appId;
    private int status;
    private String appPath;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public AppApkDomain fromAppVersion(AppVersionDomain versionDomain) {
        ApkMeta apkMeta = Utils.getApkMeta(versionDomain.getAppPath());
        if (apkMeta != null) {
            AppApkDomain apkDomain = new AppApkDomain();
            apkDomain.setAppId(versionDomain.getAppId());
            apkDomain.setAppPath(versionDomain.getAppPath());
            apkDomain.setStatus(versionDomain.getStatus());
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

    @Override
    public String getKey() {
        return getId() + "";
    }

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }
}
