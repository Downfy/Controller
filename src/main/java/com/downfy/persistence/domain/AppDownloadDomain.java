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
package com.downfy.persistence.domain;

import com.downfy.common.ObjectKey;
import java.util.Date;

/*
 * AppDownloadDomain.java
 * 
 * App download domain
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  29-Nov-2013     tuanta      Create first time
 */
public class AppDownloadDomain implements DomainObject {

    public static final String OBJECT_KEY = ObjectKey.APP_DOWNLOAD;
    private long appId;
    private String appPath;
    private String appDownloadPath;
    private String sessionId;
    private boolean status;
    private Date created;
    private long creater;

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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAppDownloadPath() {
        return appDownloadPath;
    }

    public void setAppDownloadPath(String appDownloadPath) {
        this.appDownloadPath = appDownloadPath;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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
    public String getKey() {
        return getSessionId();
    }

    @Override
    public String getObjectKey() {
        return getAppId() + "-" + OBJECT_KEY;
    }
}
