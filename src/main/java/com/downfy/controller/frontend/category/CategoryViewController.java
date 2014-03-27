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
package com.downfy.controller.frontend.category;

import com.downfy.controller.AbstractController;
import com.downfy.controller.MyResourceMessage;
import com.downfy.controller.frontend.appication.ApplicationViewController;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 * CategoryViewController.java
 *
 * Category view controller
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  24-Dec-2013     tuanta      Create first time
 */
@Controller
public class CategoryViewController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(ApplicationViewController.class);
    @Autowired
    MyResourceMessage resourceMessage;

    @RequestMapping(value = "/android-{group}", method = RequestMethod.GET)
    public String groupAndroid(@PathVariable("group") String groupName, HttpServletRequest request, Device device, Model model) {
        try {
            return view(device, "category/view");
        } catch (Exception ex) {
            logger.error("Cannot view group android " + groupName, ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/android-{group}/{category}", method = RequestMethod.GET)
    public String groupAndroidCategory(@PathVariable("group") String group, @PathVariable("category") String categoryName, HttpServletRequest request, Device device, Model model) {
        try {
            return view(device, "category/view");
        } catch (Exception ex) {
            logger.error("Cannot view category android " + categoryName, ex);
        }
        return view(device, "maintenance");
    }
}
