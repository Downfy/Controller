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
package com.downfy.controller.member;

import com.downfy.service.AccountService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.downfy.common.ErrorMessage;
import com.downfy.common.ValidationResponse;
import com.downfy.controller.AbstractController;
import com.downfy.controller.MyResourceMessage;
import com.downfy.form.member.ChangePasswordForm;
import com.downfy.form.validator.member.ChangePasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.Device;

/*
 * ChangePasswordController.java
 * 
 * Change password account controller
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
@RequestMapping({"/member/change-password"})
@Controller
public class ChangePasswordController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(ChangePasswordController.class);
    @Autowired
    ChangePasswordValidator validator;
    @Autowired
    AccountService accountService;
    @Autowired
    MyResourceMessage resourceMessage;
    @Autowired
    ShaPasswordEncoder passwordEncoder;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = {"/json"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public ValidationResponse changePasswordAjaxJson(Model model, @ModelAttribute("changePasswordForm") ChangePasswordForm form, HttpServletRequest request, BindingResult bindingResult) {
        ValidationResponse res = new ValidationResponse();
        this.validator.validate(form, bindingResult);
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
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String changePassword(Device device, @ModelAttribute("changePasswordForm") ChangePasswordForm form, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.validator.validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("changePasswordForm", form);
            return view(device, "member/changepassword");
        }
        try {
            this.accountService.changePassword(getUsername(), this.passwordEncoder.encodePassword(form.getNewPassword(), null));
            return view(device, "member/successchangepassword");
        } catch (Exception ex) {
            logger.error("Change password for user " + getUsername() + " error.", ex);
            bindingResult.reject("changepassword.error");
            uiModel.addAttribute("changePasswordForm", new ChangePasswordForm());
        }
        return view(device, "member/changepassword");
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String index(Device device, Model uiModel) {
        uiModel.addAttribute("changePasswordForm", new ChangePasswordForm());
        return view(device, "member/changepassword");
    }
}
