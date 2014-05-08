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
package com.downfy.controller.member.appication;

import com.downfy.common.AppCommon;
import com.downfy.common.ErrorMessage;
import com.downfy.common.ValidationResponse;
import com.downfy.controller.AbstractController;
import com.downfy.controller.MyResourceMessage;
import com.downfy.form.application.AppScreenShootForm;
import com.downfy.form.validator.backend.application.BackendAppScreenshootValidator;
import com.downfy.persistence.domain.application.AppUploadedDomain;
import com.downfy.service.application.AppScreenshootService;
import com.downfy.service.application.AppUploadedService;
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
 * AppScreenShootController.java
 *
 * Admin application screen shoot controller
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Dec-2013     tuanta      Create first time
 */
@PreAuthorize("isAuthenticated()")
@RequestMapping("/member/application/screenshoots")
@Controller
public class MemberAppScreenShootController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(MemberAppScreenShootController.class);
    @Autowired
    MyResourceMessage resourceMessage;
    @Autowired
    BackendAppScreenshootValidator validator;
    @Autowired
    AppUploadedService appUploadedService;
    @Autowired
    AppScreenshootService appScreenshootService;

    @RequestMapping(value = "/json/verify", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse verifyScreenShootJson(@ModelAttribute("appScreenShootForm") AppScreenShootForm domain, HttpServletRequest request, BindingResult bindingResult) {
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

    @RequestMapping(value = "/json/remove", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse removeScreenShootJson(@ModelAttribute("appScreenShootForm") AppScreenShootForm domain, HttpServletRequest request, BindingResult bindingResult) {
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

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public String verifyScreenShoot(@ModelAttribute("appScreenShootForm") AppScreenShootForm domain, Device device, HttpServletRequest request, BindingResult bindingResult, Model uiModel) {
        try {
            AppUploadedDomain uploadedDomain = appUploadedService.findById(domain.getScreenShootId());
            if (uploadedDomain != null) {
                appScreenshootService.save(domain.fromAppUploadedDomain(uploadedDomain));
            }
            return showScreenshoots(domain);
        } catch (Exception ex) {
            logger.error("Cannot upload screenshoot application.", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public String removeScreenShoot(@ModelAttribute("appScreenShootForm") AppScreenShootForm domain, Device device, HttpServletRequest request, BindingResult bindingResult, Model uiModel) {
        try {
            appScreenshootService.delete(domain.getScreenShootId(), domain.getAppId());
            return showScreenshoots(domain);
        } catch (Exception ex) {
            logger.error("Cannot upload screenshoot application.", ex);
        }
        return view(device, "maintenance");
    }

    private String showScreenshoots(AppScreenShootForm domain) {
        appUploadedService.delete(domain.getScreenShootId() + "", domain.getAppId(), AppCommon.FILE_SCREENSHOOT);
        return "redirect:/member/application/" + domain.getAppId() + "/screenshoots.html";
    }
}
