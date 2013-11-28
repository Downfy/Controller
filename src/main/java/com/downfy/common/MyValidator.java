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
package com.downfy.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyValidator {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String MOBILE_PATTERN = "\\d{10,12}";
    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,50}$";

    public static boolean validateEmail(String value) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean validateUsername(String value) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean validateMobile(String value) {
        Pattern pattern = Pattern.compile(MOBILE_PATTERN);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean validateNullOrEmpty(String value) {
        if (value == null) {
            return true;
        }
        if (value.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean validateNullOrEmpty(String[] value) {
        if (value == null) {
            return true;
        }
        if (value.length == 0) {
            return true;
        }
        return false;
    }

    public static boolean validateNumberInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception ex) {
        }
        return false;
    }
}
