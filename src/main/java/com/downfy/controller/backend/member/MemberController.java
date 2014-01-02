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
package com.downfy.controller.backend.member;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.downfy.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 * MemberController.java
 * 
 * Member area controller
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  20-Dec-2013     tuanta      Create first time
 */
@RequestMapping({"/member"})
@Controller
public class MemberController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET)
    public String index(Device device, Model uiModel) {
        return view(device, "member");
    }
}
