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
package com.downfy.controller.backend.developer;

import com.downfy.common.ErrorMessage;
import com.downfy.common.ValidationResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.downfy.controller.AbstractController;
import com.downfy.controller.MyResourceMessage;
import com.downfy.form.validator.backend.developer.BackendDeveloperValidator;
import com.downfy.persistence.domain.developer.DeveloperDomain;
import com.downfy.service.developer.DeveloperService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * BackendDeveloperController.java
 * 
 * Admin developer area controller
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  01-Jan-2014     tuanta      Create first time
 */
@RequestMapping("/backend/developer")
@Controller
public class BackendDeveloperController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(BackendDeveloperController.class);
    @Autowired
    MyResourceMessage resourceMessage;
    @Autowired
    BackendDeveloperValidator validator;
    @Autowired
    DeveloperService developerService;

    private void setDevelopers(Model uiModel) {
        List<DeveloperDomain> devs = developerService.findAll();
        uiModel.addAttribute("devs", devs);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(method = RequestMethod.GET)
    public String index(Device device, Model uiModel) {
        try {
            uiModel.addAttribute("developerForm", new DeveloperDomain());
            setDevelopers(uiModel);
            return view(device, "backend/developer");
        } catch (Exception ex) {
            logger.error("Cannot show developer form.", ex);
        }
        return view(device, "maintenance");
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/json", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse createCategoryAjaxJson(@ModelAttribute("developerForm") DeveloperDomain domain, HttpServletRequest request, BindingResult bindingResult) {
        ValidationResponse res = new ValidationResponse();
        this.validator.validate(domain, bindingResult);
        if (!bindingResult.hasErrors()) {
            res.setStatus("SUCCESS");
        } else {
            res.setStatus("FAIL");
            List<FieldError> allErrors = bindingResult.getFieldErrors();
            List<ErrorMessage> errorMesages = new ArrayList<ErrorMessage>();
            for (FieldError objectError : allErrors) {
                errorMesages.add(new ErrorMessage(objectError.getField(), this.resourceMessage.getMessage(objectError.getCode(), request)));
            }
            res.setErrorMessageList(errorMesages);
        }
        return res;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(method = RequestMethod.POST)
    public String createCategory(Device device, @ModelAttribute("developerForm") DeveloperDomain domain, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.validator.validate(domain, bindingResult);
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("developerForm", domain);
            return view(device, "backend/developer");
        }
        try {
            developerService.save(domain);
            setDevelopers(uiModel);
            return "redirect:/backend/developer.html";
        } catch (Exception ex) {
            logger.error("Create developer error.", ex);
            bindingResult.reject("developer.error");
            uiModel.addAttribute("developerForm", domain);
        }
        return view(device, "backend/developer");
    }
}
