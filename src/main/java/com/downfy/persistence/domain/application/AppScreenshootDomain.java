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
import java.util.Date;

/*
 * AppScreenshootDomain.java
 *
 * App screen shoot domain
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  29-Nov-2013     tuanta      Create first time
 */
public class AppScreenshootDomain implements DomainObject {

    public static final String OBJECT_KEY = ObjectKey.APP_SCREEN_SHOOT;
    private long id;
    private long appId;
    private String appScreenShoot;
    private long size;
    private int status;
    private Date created;
    private long creater;

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppScreenShoot() {
        return appScreenShoot;
    }

    public void setAppScreenShoot(String appScreenShoot) {
        this.appScreenShoot = appScreenShoot;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
                .add("appPath", getAppScreenShoot())
                .toString();
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
