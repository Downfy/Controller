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
import com.google.api.client.repackaged.com.google.common.base.Objects;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import java.util.Date;

/*
 * AppUploadedDomain.java
 *
 * App uploaded domain
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  29-Nov-2013     tuanta      Create first time
 */
public class AppUploadedDomain implements DomainObject {

    public static final String OBJECT_KEY = ObjectKey.APP_UPLOADED;
    private long id;
    private long appId;
    private String appPath;
    private String appRealPath;
    private String appVersion;
    private String appPackage;
    private long size;
    private int type;
    private Date created;
    private long creater;

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

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public String getAppRealPath() {
        return appRealPath;
    }

    public void setAppRealPath(String appRealPath) {
        this.appRealPath = appRealPath;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getCreater() {
        return creater;
    }

    public void setCreater(long creater) {
        this.creater = creater;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass())
                .add("appId", getAppId())
                .add("appPath", getAppPath())
                .add("type", getType())
                .toString();
    }

    @Override
    public String getKey() {
        if (Strings.isNullOrEmpty(getAppPackage()) || Strings.isNullOrEmpty(getAppVersion())) {
            return getId() + "";
        }
        return getAppPackage() + ":" + getAppVersion();
    }

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }
}
