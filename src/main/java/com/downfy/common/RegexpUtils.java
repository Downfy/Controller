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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Tran Anh Tuan <tk1cntt@gmail.com>
 */
public final class RegexpUtils {

    private RegexpUtils() {
        throw new UnsupportedOperationException("RegexpUtils is a utility class - don't instantiate it!");
    }
    private static final LoadingCache<String, Pattern> COMPILED_PATTERNS =
            CacheBuilder.newBuilder().build(new CacheLoader<String, Pattern>() {
        @Override
        public Pattern load(String regexp) throws Exception {
            return Pattern.compile(regexp);
        }
    });

    private static Pattern getPattern(String regexp) {
        try {
            return COMPILED_PATTERNS.get(regexp);
        } catch (ExecutionException e) {
            throw new RuntimeException(String.format("Error when getting a pattern [%s] from cache", regexp), e);
        }
    }

    public static boolean matches(String stringToCheck, String regexp) {
        return doGetMatcher(stringToCheck, regexp).matches();
    }

    private static Matcher doGetMatcher(String stringToCheck, String regexp) {
        Pattern pattern = getPattern(regexp);
        return pattern.matcher(stringToCheck);
    }
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String MOBILE_PATTERN = "\\d{10,12}";
    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,50}$";
    private static final String VERSION_PATTERN = "^(\\d+\\.)?(\\d+\\.)?(\\d+)$";

    public static boolean validateEmail(String value) {
        return matches(value, EMAIL_PATTERN);
    }

    public static boolean validateUsername(String value) {
        return matches(value, USERNAME_PATTERN);
    }

    public static boolean validateMobile(String value) {
        return matches(value, MOBILE_PATTERN);
    }

    public static boolean validateVersion(String value) {
        return matches(value, VERSION_PATTERN);
    }
}
