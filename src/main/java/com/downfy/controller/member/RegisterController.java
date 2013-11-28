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

import com.downfy.persistence.domain.AccountDomain;
import com.downfy.service.AccountService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.downfy.controller.MyResourceMessage;
import com.downfy.form.admin.RegisterForm;
import com.downfy.form.validator.admin.RegisterValidator;

@Controller
@RequestMapping({"/create-account"})
public class RegisterController {

    private final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    @Autowired
    RegisterValidator validator;
    @Autowired
    MyResourceMessage resourceMessage;
    @Autowired
    ShaPasswordEncoder passwordEncoder;
    @Autowired
    AccountService accountService;

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String registerMemberForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        model.addAttribute("title", "Đăng ký thành viên");
        model.addAttribute("description", "Hãy đăng ký thành viên và tham gia vào mạng xã hội tuyển dụng. Hàng ngàn công việc phù hợp với bạn");
        return "user/create";
    }

    @RequestMapping(value = {"/json"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public ValidationResponse registerMemberAjaxJson(Model model, @ModelAttribute("registerForm") RegisterForm form, HttpServletRequest request, BindingResult bindingResult) {
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

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String registerMember(@ModelAttribute("registerForm") RegisterForm form, BindingResult bindingResult, Model model) {
        this.validator.validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerForm", form);
            return "user/create";
        }
        try {
            AccountDomain account = new AccountDomain();
            account.setId(System.currentTimeMillis());
            account.setPassword(this.passwordEncoder.encodePassword(form.getPassword(), null));
            account.setEmail(form.getEmail());
            account.setEnabled(true);
            if (this.accountService.save(account)) {
                model.addAttribute("email", form.getEmail());
                return "user/successregister";
            }
        } catch (Exception ex) {
            this.logger.error("Register account error: ", ex);
        }
        bindingResult.rejectValue("username", "register.save.error");
        model.addAttribute("registerForm", form);
        return "user/create";
    }
}