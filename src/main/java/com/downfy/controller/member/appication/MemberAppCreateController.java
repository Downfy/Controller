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
import com.downfy.form.backend.application.AppCreateForm;
import com.downfy.form.validator.backend.application.BackendAppValidator;
import com.downfy.persistence.domain.application.AppDomain;
import com.downfy.service.application.AppService;
import java.util.ArrayList;
import java.util.Date;
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
 * MemberAppCreateController.java
 *
 * Member application create controller
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Dec-2013     tuanta      Create first time
 */
@PreAuthorize("isAuthenticated()")
@RequestMapping("/member/application/create")
@Controller
public class MemberAppCreateController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(MemberAppCreateController.class);
    @Autowired
    MyResourceMessage resourceMessage;
    @Autowired
    BackendAppValidator validator;
    @Autowired
    AppService appService;

    private void setApps(Model uiModel) {
        List<AppDomain> apps = appService.findByDeveloper(getMyId());
        uiModel.addAttribute("apps", apps);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(method = RequestMethod.GET)
    public String index(HttpServletRequest request, Device device, Model uiModel) {
        try {
            uiModel.addAttribute("applicationForm", new AppCreateForm());
            return view(device, "member/application/create");
        } catch (Exception ex) {
            logger.error("Cannot create application.", ex);
        }
        return view(device, "maintenance");
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/json", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse createApplicationAjaxJson(@ModelAttribute("applicationForm") AppCreateForm domain, HttpServletRequest request, BindingResult bindingResult) {
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
    public String createApplication(Device device, @ModelAttribute("applicationForm") AppCreateForm domain, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.validator.validate(domain, bindingResult);
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("applicationForm", domain);
            return view(device, "member/application/create");
        }
        try {
            if (domain.getAppId() != 0) {
                AppDomain appDomain = appService.findById(domain.getAppId());
                if (appDomain.getCreater() == getMyId()) {
                    appDomain = domain.toAppDomain(appDomain);
                    appDomain.setUpdater(getMyId());
                    appDomain.setUpdated(new Date());
                    appService.updateAppInfo(appDomain, getMyId());
                }
            } else {
                AppDomain appDomain = new AppDomain();
                appDomain = domain.toAppDomain(appDomain);
                appDomain.setUpdater(getMyId());
                appDomain.setUpdated(new Date());
                appDomain.setCreater(getMyId());
                appDomain.setCreated(new Date());
                appDomain.setAppId(System.currentTimeMillis());
                appDomain.setStatus(AppCommon.CREATED);
                appService.save(appDomain);
            }
            setApps(uiModel);
            return "redirect:/member/application.html";
        } catch (Exception ex) {
            logger.error("Create application error.", ex);
            bindingResult.reject("app.error");
            uiModel.addAttribute("applicationForm", domain);
        }
        return view(device, "member/application/create");
    }
}
