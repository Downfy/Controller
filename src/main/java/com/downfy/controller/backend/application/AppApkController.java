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
package com.downfy.controller.backend.application;

import com.downfy.common.AppCommon;
import com.downfy.common.ErrorMessage;
import com.downfy.common.Utils;
import com.downfy.common.ValidationResponse;
import com.downfy.controller.AbstractController;
import com.downfy.controller.MyResourceMessage;
import com.downfy.form.backend.application.AppApkForm;
import com.downfy.form.validator.backend.application.BackendAppApkValidator;
import com.downfy.persistence.domain.application.AppUploadedDomain;
import com.downfy.service.application.AppApkService;
import com.downfy.service.application.AppUploadedService;
import com.downfy.service.application.AppVersionService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
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
 * AppApkController.java
 *
 * Admin application apk controller
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
    BackendAppApkValidator validator;
    @Autowired
    AppUploadedService appUploadedService;
    @Autowired
    AppApkService appApkService;
    @Autowired
    AppVersionService appVersionService;
    @Autowired
    ServletContext context;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/json/verify", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse verifyApkJson(@ModelAttribute("appApkForm") AppApkForm domain, HttpServletRequest request, BindingResult bindingResult) {
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
    @RequestMapping(value = "/json/remove", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse removeApkJson(@ModelAttribute("appApkForm") AppApkForm domain, HttpServletRequest request, BindingResult bindingResult) {
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
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public String verifyApk(@ModelAttribute("appApkForm") AppApkForm domain, Device device, HttpServletRequest request, BindingResult bindingResult, Model uiModel) {
        try {
            logger.debug("Verify file apk uploaded " + domain.getAppPackage() + ":" + domain.getAppVersion());
            AppUploadedDomain uploadedDomain = appUploadedService.findById(domain.getAppPackage(), domain.getAppVersion());
            if (uploadedDomain != null) {
                File f = new File(context.getRealPath("/"));
                String localPath = f.getCanonicalPath() + File.separator + Utils.toMd5("data")
                        + uploadedDomain.getAppPath();
                long id = System.currentTimeMillis();
                appVersionService.save(domain.toAppVersion(localPath, id, uploadedDomain));
                appApkService.save(domain.toAppApk(localPath, id, uploadedDomain));
                appUploadedService.delete(uploadedDomain.getKey(), domain.getAppId(), AppCommon.FILE_APK);
            }
            return "redirect:/backend/application/" + domain.getAppId() + "/apk.html";
        } catch (IOException ex) {
            logger.error("Cannot upload apk application.", ex);
        }
        return view(device, "maintenance");
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public String removeApk(@ModelAttribute("appApkForm") AppApkForm domain, Device device, HttpServletRequest request, BindingResult bindingResult, Model uiModel) {
        try {
            logger.debug("Remove file apk uploaded " + domain.getAppPackage() + ":" + domain.getAppVersion());
            appVersionService.delete(domain.getApkId(), domain.getAppId());
            appUploadedService.delete(domain.getAppPackage() + ":" + domain.getAppVersion(), domain.getAppId(), AppCommon.FILE_APK);
            appApkService.delete(domain.getApkId(), domain.getAppId());
            return "redirect:/backend/application/" + domain.getAppId() + "/apk.html";
        } catch (Exception ex) {
            logger.error("Cannot upload apk application.", ex);
        }
        return view(device, "maintenance");
    }
}
