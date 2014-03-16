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

import com.downfy.common.ErrorMessage;
import com.downfy.common.ValidationResponse;
import com.downfy.controller.AbstractController;
import com.downfy.controller.MyResourceMessage;
import com.downfy.form.backend.application.AppApkForm;
import com.downfy.form.validator.backend.application.BackendAppVersionValidator;
import com.downfy.persistence.domain.application.AppVersionDomain;
import com.downfy.service.AppVersionService;
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
 * BackendCategoryController.java
 *
 * Admin application create controller
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Dec-2013     tuanta      Create first time
 */
@RequestMapping("/backend/application/apk")
@Controller
public class AppApkController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(AppApkController.class);
    @Autowired
    MyResourceMessage resourceMessage;
    @Autowired
    BackendAppVersionValidator validator;
    @Autowired
    AppVersionService appVersionService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/json", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse createApplicationAjaxJson(@ModelAttribute("appApkForm") AppApkForm domain, HttpServletRequest request, BindingResult bindingResult) {
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
    public String uploadApkApplication(Device device, @ModelAttribute("appApkForm") AppApkForm domain, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.validator.validate(domain, bindingResult);
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("appApkForm", domain);
            return view(device, "backend/application/apk");
        }
        try {
            AppVersionDomain appVersionDomain = domain.toAppVersion();
            logger.debug("Upload apk version " + appVersionDomain.toString());
            appVersionDomain.setId(System.currentTimeMillis());
            appVersionDomain.setCreater(getUserId());
            if (appVersionService.save(appVersionDomain)) {
                return "redirect:/backend/application/" + domain.getAppId() + "/apk.html";
            }
        } catch (Exception ex) {
            logger.error("Upload apk application error.", ex);
        }
        bindingResult.reject("app.appapkuploadfailure");
        uiModel.addAttribute("appApkForm", domain);
        return view(device, "backend/application/apk");
    }
}
