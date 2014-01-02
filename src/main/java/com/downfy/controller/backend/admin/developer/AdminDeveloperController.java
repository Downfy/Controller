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
package com.downfy.controller.backend.admin.developer;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.downfy.controller.AbstractController;
import com.downfy.persistence.domain.admin.developer.AdminDeveloperDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 * AdminDeveloperController.java
 * 
 * Admin developer area controller
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  01-Jan-2014     tuanta      Create first time
 */
@RequestMapping("/admin/developer")
@Controller
public class AdminDeveloperController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(AdminDeveloperController.class);

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(method = RequestMethod.GET)
    public String index(Device device, Model uiModel) {
        try {
            uiModel.addAttribute("adminDeveloperForm", new AdminDeveloperDomain());
            return view(device, "admin/developer");
        } catch (Exception ex) {
            logger.error("Cannot show developer form.", ex);
        }
        return view(device, "maintenance");
    }
}
