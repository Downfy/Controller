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
package com.downfy.common;

/**
 *
 * @author Tran Anh Tuan <tk1cntt@gmail.com>
 */
public class AppCommon {

    //App status
    public static final byte DELETED = 0;
    public static final byte BLOCKED = 1;
    public static final byte CREATED = 2;
    public static final byte PENDING = 3;
    public static final byte PUBLISHED = 4;
    //File upload type
    public static final byte FILE_APK = 1;
    public static final byte FILE_SCREENSHOOT = 2;
    public static final byte FILE_ICON = 0;
    //Upload apk status
    public static final byte UPLOAD_SUCCESS = 0;
    public static final byte UPLOAD_FAILRE = 1;
    public static final byte UPLOAD_FILE_EXIST = 2;
    public static final byte UPLOAD_FILE_NOT_SUPPORT = 3;
    //
    public static final String CATEGORY_APPLICATION = "APPLICATION";
    public static final String CATEGORY_GAME = "GAME";
}
