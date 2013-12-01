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
 * AccountDomain.java
 * 
 * Account domain
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  27-Nov-2013     tuanta      Create first time
 */
public class AccountDomain
        implements DomainObject {

    public static final String OBJECT_KEY = ObjectKey.USER;
    private long id;
    private String password;
    private String email;
    private boolean enabled;
    private int failedLoginCount;
    private Date lastFailedLoginTime;
    private String lastHostAddress;
    private Date lastLoginTime;
    private Date lastPasswordChangeTime;
    private Date expirationDate;
    private String activeKey;
    private String CSRFToken;
    private boolean isLogin;
    private Date created;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getFailedLoginCount() {
        return this.failedLoginCount;
    }

    public void setFailedLoginCount(int failedLoginCount) {
        this.failedLoginCount = failedLoginCount;
    }

    public Date getLastFailedLoginTime() {
        return this.lastFailedLoginTime;
    }

    public void setLastFailedLoginTime(Date lastFailedLoginTime) {
        this.lastFailedLoginTime = lastFailedLoginTime;
    }

    public String getLastHostAddress() {
        return this.lastHostAddress;
    }

    public void setLastHostAddress(String lastHostAddress) {
        this.lastHostAddress = lastHostAddress;
    }

    public Date getLastLoginTime() {
        return this.lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getLastPasswordChangeTime() {
        return this.lastPasswordChangeTime;
    }

    public void setLastPasswordChangeTime(Date lastPasswordChangeTime) {
        this.lastPasswordChangeTime = lastPasswordChangeTime;
    }

    public String getCSRFToken() {
        return this.CSRFToken;
    }

    public void setCSRFToken(String CSRFToken) {
        this.CSRFToken = CSRFToken;
    }

    public Date getExpirationDate() {
        return this.expirationDate;
    }

    public String getActiveKey() {
        return activeKey;
    }

    public void setActiveKey(String activeKey) {
        this.activeKey = activeKey;
    }

    public boolean isLocked() {
        return isEnabled();
    }

    public boolean isLoggedIn() {
        return this.isLogin;
    }

    public void setLocked(boolean b) {
        setEnabled(this.enabled);
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public void setExpirationDate(Date expirationTime) {
        this.expirationDate = expirationTime;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Email:").append(getEmail()).append(",");
        sb.append("Password:").append("********").append(",");
        sb.append("Enable:").append(isEnabled()).append("]");
        return sb.toString();
    }

    @Override
    public String getKey() {
        return getEmail();
    }

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }
}