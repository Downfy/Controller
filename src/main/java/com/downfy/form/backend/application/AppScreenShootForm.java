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
import com.downfy.persistence.domain.application.AppScreenshootDomain;
import com.downfy.persistence.domain.application.AppUploadedDomain;

/**
 *
 * @author Tran Anh Tuan <tk1cntt@gmail.com>
 */
public class AppScreenShootForm {

    private long appId;
    private long screenShootId;

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public long getScreenShootId() {
        return screenShootId;
    }

    public void setScreenShootId(long screenShootId) {
        this.screenShootId = screenShootId;
    }

    public AppScreenshootDomain fromAppUploadedDomain(AppUploadedDomain domain) {
        AppScreenshootDomain screenshootDomain = new AppScreenshootDomain();
        screenshootDomain.setAppId(domain.getAppId());
        screenshootDomain.setAppScreenShoot(domain.getAppPath());
        screenshootDomain.setCreated(domain.getCreated());
        screenshootDomain.setCreater(domain.getCreater());
        screenshootDomain.setStatus(AppCommon.PENDING);
        screenshootDomain.setId(domain.getId());
        screenshootDomain.setSize(domain.getSize());
        return screenshootDomain;
    }
}
