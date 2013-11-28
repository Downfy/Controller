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
package com.downfy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 * MobileLoginController.java
 * 
 * Mobile login controller
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  28-Nov-2013     tuanta      Create first time
 */
@RequestMapping("/login")
@Controller
public class LoginController extends AbstractController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    MyResourceMessage resourceMessage;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Device device, Model model) {
        try {
            if (isMobile(device)) {
                return "mobile/login";
            }
            return "home/login";
        } catch (Exception ex) {
            logger.error("Cannot get data." + ex.toString(), ex);
        }
        return "mobile/maintenance";
    }

    @RequestMapping(value = "/failure", method = RequestMethod.GET)
    public String failure(Device device, Model model) {
        try {
            if (isMobile(device)) {
                return "mobile/login";
            }
            return "home/login";
        } catch (Exception ex) {
            logger.error("Cannot get data." + ex.toString(), ex);
        }
        return "mobile/maintenance";
    }
}