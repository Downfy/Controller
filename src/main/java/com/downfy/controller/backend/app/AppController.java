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
package com.downfy.controller.backend.app;

import com.downfy.controller.AbstractController;
import com.downfy.controller.MyResourceMessage;
import com.downfy.form.backend.application.AppForm;
import com.downfy.form.validator.backend.application.BackendAppValidator;
import com.downfy.persistence.domain.application.AppDomain;
import com.downfy.service.AppService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 * BackendCategoryController.java
 *
 * Admin application create controller
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Dec-2013     tuanta      Create first time
 */
@RequestMapping("/backend/application")
@Controller
public class AppController extends AbstractController {

    private Logger logger = LoggerFactory.getLogger(AppController.class);
    @Autowired
    MyResourceMessage resourceMessage;
    @Autowired
    BackendAppValidator validator;
    @Autowired
    AppService appService;

    private void setApps(Model uiModel) {
        List<AppDomain> apps = appService.findAll();
        uiModel.addAttribute("apps", apps);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(method = RequestMethod.GET)
    public String index(HttpServletRequest request, Device device, Model uiModel) {
        try {
            uiModel.addAttribute("applicationForm", new AppForm());
            setApps(uiModel);
            return view(device, "backend/application");
        } catch (Exception ex) {
            logger.error("Cannot create application.", ex);
        }
        return view(device, "maintenance");
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") long appId, HttpServletRequest request, Device device, Model uiModel) {
        try {
            AppDomain appDomain = appService.findById(appId);
            uiModel.addAttribute("app", appDomain);
            return view(device, "backend/application/detail");
        } catch (Exception ex) {
            logger.error("Cannot create detail application.", ex);
        }
        return view(device, "maintenance");
    }
}
