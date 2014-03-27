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

import com.downfy.controller.AbstractController;
import com.downfy.persistence.domain.AccountDomain;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * BackendAccountController.java
 *
 * Admin account area controller
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  01-Jan-2014     tuanta      Create first time
 */
@RequestMapping({"/backend/account"})
@Controller
public class BackendAccountController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(BackendAccountController.class);

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String index(Model model) {
        try {
            int sizeNo = 10;
            Collection<AccountDomain> accounts = null;//this.accountService.findByLimit("CREATED DESC", 0, sizeNo);
            model.addAttribute("topaccounts", accounts);
            long total = this.accountService.count();
            float nrOfPages = (float) total / sizeNo;
            model.addAttribute("totalaccount", Long.valueOf(total));
            model.addAttribute("maxPages", (int) Math.ceil(nrOfPages == 0 ? nrOfPages + 1 : nrOfPages));

            return "admin/members";
        } catch (Exception ex) {
            this.logger.error("Cannot get data." + ex.toString());
        }
        return "home/maintenance";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = {"/p{page}-{size}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String indexPage(@PathVariable("page") Integer page, @PathVariable("size") Integer size, Model model) {
        try {
            Collection<AccountDomain> accounts;
            int sizeNo;
            sizeNo = size == null ? 10 : size.intValue();
            page = Integer.valueOf(page == null ? 0 : page.intValue() - 1);
            accounts = null;//this.accountService.findByLimit("CREATED DESC", page.intValue() * sizeNo, sizeNo);
            long total = this.accountService.count();
            float nrOfPages = (float) total / sizeNo;
            model.addAttribute("maxPages", (int) Math.ceil(nrOfPages == 0 ? nrOfPages + 1 : nrOfPages));

            model.addAttribute("totalaccount", Long.valueOf(total));
            model.addAttribute("topaccounts", accounts);
            model.addAttribute("pageNo", Integer.valueOf(page.intValue() + 1));
            model.addAttribute("sizeNo", size);
            return "admin/members";
        } catch (Exception ex) {
            this.logger.error("Cannot get data." + ex.toString());
        }
        return "home/maintenance";
    }
}
