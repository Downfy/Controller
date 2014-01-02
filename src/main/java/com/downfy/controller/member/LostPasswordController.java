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

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.downfy.form.member.LostPasswordForm;
import com.downfy.form.validator.member.LostPasswordValidator;
import org.springframework.mobile.device.Device;

/*
 * LostPasswordController.java
 * 
 * Forgot password account controller
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
@RequestMapping({"/forgot-password"})
@Controller
public class LostPasswordController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(LostPasswordController.class);
    @Autowired
    LostPasswordValidator validator;
    @Autowired
    MyResourceMessage resourceMessage;

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String lostPasswordForm(Device device, Model model) {
        model.addAttribute("lostPasswordForm", new LostPasswordForm());
        model.addAttribute("title", "Quên mật khẩu");
        model.addAttribute("description", "Bạn quên mật khẩu? Hãy lấy lại mật khẩu của bạn một cách đơn giản.");
        return view(device, "member/lostpassword");
    }

    @RequestMapping(value = {"/json"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public ValidationResponse lostPasswordAjaxJson(Model model, @ModelAttribute("lostPasswordForm") LostPasswordForm form, HttpServletRequest request, BindingResult bindingResult) {
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
    public String lostPassword(Device device, LostPasswordForm form, Model model, BindingResult bindingResult) {
        this.validator.validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("lostPasswordForm", form);
            return view(device, "member/lostpassword");
        }
        model.addAttribute("email", form.getEmail());
        return view(device, "member/successlostpassword");
    }
}