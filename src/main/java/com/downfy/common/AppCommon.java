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

    //Images
    public static final String IMAGE_FORMAT_PNG = "png";
    //Icon size
    public static final int ICON_WIDTH = 124;
    public static final int ICON_HEIGHT = 124;
    //Screen shoot
    public static final int SCREENSHOOT_WIDTH = 240;
    public static final int SCREENSHOOT_HEIGHT = 320;
    //Landing page image
    public static final int LANDING_PAGE_WIDTH = 480;
    public static final int LANDING_PAGE_HEIGHT = 320;
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
    public static final byte UPLOAD_PACKAGE_AVAILABLE = 4;
    public static final byte UPLOAD_PACKAGE_AVAILABLE_ANOTHER = 5;
    public static final byte UPLOAD_PACKAGE_NOT_MATCH = 6;
    //
    public static final String CATEGORY_APPLICATION = "APPLICATION";
    public static final String CATEGORY_GAME = "GAME";
}
