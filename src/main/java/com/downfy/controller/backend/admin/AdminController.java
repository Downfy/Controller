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
package com.downfy.controller.backend.admin;

import com.downfy.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * AdminController.java
 * 
 * Admin area controller
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  01-Jan-2014     tuanta      Create first time
 */
@RequestMapping({"/admin"})
@Controller
public class AdminController {

    @Autowired
    AccountService accountService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String index(Model model) {
        model.addAttribute("", "");
        return "admin/index";
    }
}
