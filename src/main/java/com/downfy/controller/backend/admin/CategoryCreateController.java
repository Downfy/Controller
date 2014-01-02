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

import com.downfy.common.ErrorMessage;
import com.downfy.common.ValidationResponse;
import com.downfy.controller.AbstractController;
import com.downfy.controller.MyResourceMessage;
import com.downfy.controller.app.AppViewController;
import com.downfy.form.admin.ChangePasswordForm;
import com.downfy.form.validator.CategoryValidator;
import com.downfy.persistence.domain.CategoryDomain;
import com.downfy.service.CategoryService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * CategoryCreateController.java
 *
 * Category create controller
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Dec-2013     tuanta      Create first time
 */
@RequestMapping("/backend/category")
@Controller
public class CategoryCreateController extends AbstractController {

    private Logger logger = LoggerFactory.getLogger(AppViewController.class);
    @Autowired
    MyResourceMessage resourceMessage;
    @Autowired
    CategoryValidator validator;
    @Autowired
    CategoryService categoryService;

    private void setCategories(Model uiModel) {
        List<CategoryDomain> cats = categoryService.findAll();
        uiModel.addAttribute("cats", cats);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(HttpServletRequest request, Device device, Model uiModel) {
        try {
            uiModel.addAttribute("categoryForm", new CategoryDomain());
            setCategories(uiModel);
            return view(device, "member/create-cat");
        } catch (Exception ex) {
            logger.error("Cannot create category group.", ex);
        }
        return view(device, "maintenance");
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/json", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse createCategoryAjaxJson(@ModelAttribute("categoryForm") CategoryDomain domain, HttpServletRequest request, BindingResult bindingResult) {
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

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.POST)
    public String createCategory(Device device, @ModelAttribute("categoryForm") CategoryDomain domain, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.validator.validate(domain, bindingResult);
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("categoryForm", domain);
            return view(device, "member/create-cat");
        }
        try {
            categoryService.save(domain);
            setCategories(uiModel);
            return "redirect:/member/category.html";
        } catch (Exception ex) {
            logger.error("Change password for user " + getUserId() + " error.", ex);
            bindingResult.reject("changepassword.error");
            uiModel.addAttribute("changePasswordForm", new ChangePasswordForm());
        }
        return view(device, "member/create-cat");
    }
}
