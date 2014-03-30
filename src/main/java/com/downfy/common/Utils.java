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

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.servlet.ServletContext;
import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.exception.ParserException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

/*
 * Utils.java
 *
 * Version 1.0
 *
 * Date 01/01/2013
 *
 * Utilities
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  01-Jan-2013     haidh       Create first time
 *  01-Jan-2013     tuanta      Add encode URL
 *  23-Jan-2013     tuanta      Add bytesToHex
 *  13-Feb-2013     tuanta      Add toMd5
 */
public class Utils {

    private final static Logger logger = LoggerFactory.getLogger(Utils.class);

    /**
     * Convert byte to hex string.
     *
     * @param bytes Array byte
     * @return Hex string
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02X", bytes[i]));
        }
        return sb.toString();
    }

    /**
     * Convert to VNese no sign.
     *
     * @param value Plain text
     * @return Text no sign
     */
    public static String convertURL(String value) {
        char arrChar[] = value.toCharArray();
        char result[] = new char[arrChar.length];
        for (int i = 0; i < arrChar.length; i++) {
            switch (arrChar[i]) {
                case '\u00E1':
                case '\u00E0':
                case '\u1EA3':
                case '\u00E3':
                case '\u1EA1':
                case '\u0103':
                case '\u1EAF':
                case '\u1EB1':
                case '\u1EB3':
                case '\u1EB5':
                case '\u1EB7':
                case '\u00E2':
                case '\u1EA5':
                case '\u1EA7':
                case '\u1EA9':
                case '\u1EAB':
                case '\u1EAD':
                case '\u0203':
                case '\u01CE': {
                    result[i] = 'a';
                    break;
                }
                case '\u00E9':
                case '\u00E8':
                case '\u1EBB':
                case '\u1EBD':
                case '\u1EB9':
                case '\u00EA':
                case '\u1EBF':
                case '\u1EC1':
                case '\u1EC3':
                case '\u1EC5':
                case '\u1EC7':
                case '\u0207': {
                    result[i] = 'e';
                    break;
                }
                case '\u00ED':
                case '\u00EC':
                case '\u1EC9':
                case '\u0129':
                case '\u1ECB': {
                    result[i] = 'i';
                    break;
                }
                case '\u00F3':
                case '\u00F2':
                case '\u1ECF':
                case '\u00F5':
                case '\u1ECD':
                case '\u00F4':
                case '\u1ED1':
                case '\u1ED3':
                case '\u1ED5':
                case '\u1ED7':
                case '\u1ED9':
                case '\u01A1':
                case '\u1EDB':
                case '\u1EDD':
                case '\u1EDF':
                case '\u1EE1':
                case '\u1EE3':
                case '\u020F': {
                    result[i] = 'o';
                    break;
                }
                case '\u00FA':
                case '\u00F9':
                case '\u1EE7':
                case '\u0169':
                case '\u1EE5':
                case '\u01B0':
                case '\u1EE9':
                case '\u1EEB':
                case '\u1EED':
                case '\u1EEF':
                case '\u1EF1': {
                    result[i] = 'u';
                    break;
                }
                case '\u00FD':
                case '\u1EF3':
                case '\u1EF7':
                case '\u1EF9':
                case '\u1EF5': {
                    result[i] = 'y';
                    break;
                }
                case '\u0111': {
                    result[i] = 'd';
                    break;
                }
                case '\u00C1':
                case '\u00C0':
                case '\u1EA2':
                case '\u00C3':
                case '\u1EA0':
                case '\u0102':
                case '\u1EAE':
                case '\u1EB0':
                case '\u1EB2':
                case '\u1EB4':
                case '\u1EB6':
                case '\u00C2':
                case '\u1EA4':
                case '\u1EA6':
                case '\u1EA8':
                case '\u1EAA':
                case '\u1EAC':
                case '\u0202':
                case '\u01CD': {
                    result[i] = 'A';
                    break;
                }
                case '\u00C9':
                case '\u00C8':
                case '\u1EBA':
                case '\u1EBC':
                case '\u1EB8':
                case '\u00CA':
                case '\u1EBE':
                case '\u1EC0':
                case '\u1EC2':
                case '\u1EC4':
                case '\u1EC6':
                case '\u0206': {
                    result[i] = 'E';
                    break;
                }
                case '\u00CD':
                case '\u00CC':
                case '\u1EC8':
                case '\u0128':
                case '\u1ECA': {
                    result[i] = 'I';
                    break;
                }
                case '\u00D3':
                case '\u00D2':
                case '\u1ECE':
                case '\u00D5':
                case '\u1ECC':
                case '\u00D4':
                case '\u1ED0':
                case '\u1ED2':
                case '\u1ED4':
                case '\u1ED6':
                case '\u1ED8':
                case '\u01A0':
                case '\u1EDA':
                case '\u1EDC':
                case '\u1EDE':
                case '\u1EE0':
                case '\u1EE2':
                case '\u020E': {
                    result[i] = 'O';
                    break;
                }
                case '\u00DA':
                case '\u00D9':
                case '\u1EE6':
                case '\u0168':
                case '\u1EE4':
                case '\u01AF':
                case '\u1EE8':
                case '\u1EEA':
                case '\u1EEC':
                case '\u1EEE':
                case '\u1EF0': {
                    result[i] = 'U';
                    break;
                }

                case '\u00DD':
                case '\u1EF2':
                case '\u1EF6':
                case '\u1EF8':
                case '\u1EF4': {
                    result[i] = 'Y';
                    break;
                }
                case '\u0110':
                case '\u00D0':
                case '\u0089': {
                    result[i] = 'D';
                    break;
                }
                case ' ':
                case '/': {
                    result[i] = '-';
                    break;
                }
                default:
                    result[i] = arrChar[i];
            }
        }
        value = new String(result);
        value = value.replaceAll("[^a-zA-Z0-9\\-]+", "");
        value = value.replaceAll("---", "-");
        value = value.replaceAll("--", "-");
        return value;
    }

    /**
     * Rating random value from 8 to 10.
     */
    public static float ratingValue() {
        Random rd = new Random();
        int randomNum = rd.nextInt(9 - 8 + 1) + 8;
        return randomNum + rd.nextFloat();
    }

    /**
     * Encode URL
     */
    public static String encodeURL(String url) {
        return URLEncoder.encode(url);
    }

    /**
     * Encode data to md5
     *
     * @param data Data
     * @return String md5
     */
    public static String toMd5(String data) {
        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        return encoder.encodePassword(data, null);
    }

    /**
     * Create folder by current time
     *
     * @return Format folder yyyy/MM/dd
     */
    public static String folderByCurrentTime() {
        return new SimpleDateFormat("yyyy/MM/dd").format(new Date());
    }

    public static ApkMeta getApkMeta(String localPath) {
        try {
            File f = new File(localPath);
            if (f.exists()) {
                ApkParser apkParser = new ApkParser(f);
                ApkMeta meta = apkParser.getApkMeta();
                apkParser.close();
                return meta;
            } else {
                logger.info("File not found " + localPath);
            }
        } catch (ParserException ex) {
            logger.info("Not support this file format");
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            FileUtils.deleteQuietly(new File(localPath));
        }
        return null;
    }

    public static String getFolderData(ServletContext context, String data, String absolutePath) {
        try {
            File f = new File(context.getRealPath("/"));
            String localPath = f.getCanonicalPath() + File.separator + Utils.toMd5(data) + absolutePath;
            return localPath;
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return absolutePath;
        }
    }
}
