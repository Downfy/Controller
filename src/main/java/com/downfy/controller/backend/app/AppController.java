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
import com.downfy.form.backend.application.AppCreateForm;
import com.downfy.form.backend.application.AppDetailForm;
import com.downfy.form.validator.backend.application.BackendAppValidator;
import com.downfy.persistence.domain.AppFileMetaDomain;
import com.downfy.persistence.domain.application.AppDomain;
import com.downfy.persistence.domain.category.CategoryDomain;
import com.downfy.service.AppService;
import com.downfy.service.category.CategoryService;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
    @Autowired
    CategoryService categoryService;

    private void setApps(Model uiModel) {
        List<AppDomain> apps = appService.findAll();
        uiModel.addAttribute("apps", apps);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(method = RequestMethod.GET)
    public String index(HttpServletRequest request, Device device, Model uiModel) {
        try {
            uiModel.addAttribute("applicationForm", new AppCreateForm());
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
            CategoryDomain categoryDomain = categoryService.findById(appDomain.getAppCategory());
            AppDetailForm form = new AppDetailForm();
            form.fromAppDomain(appDomain);
            form.setAppCategoryParent(categoryDomain.getParent());
            form.setAppCategories(categoryService.findBySelectorParent(categoryDomain.getParent()));
            uiModel.addAttribute("app", form);
            return view(device, "backend/application/detail");
        } catch (Exception ex) {
            logger.error("Cannot create detail application.", ex);
        }
        return view(device, "maintenance");
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/{id}/apk", method = RequestMethod.GET)
    public String apk(@PathVariable("id") long appId, HttpServletRequest request, Device device, Model uiModel) {
        try {
            AppDomain appDomain = appService.findById(appId);
            uiModel.addAttribute("app", appDomain);
            return view(device, "backend/application/apk");
        } catch (Exception ex) {
            logger.error("Cannot upload application.", ex);
        }
        return view(device, "maintenance");
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/{id}/screenshoot", method = RequestMethod.GET)
    public String screenshoots(@PathVariable("id") long appId, HttpServletRequest request, Device device, Model uiModel) {
        try {
            AppDomain appDomain = appService.findById(appId);
            AppDetailForm form = new AppDetailForm();
            form.fromAppDomain(appDomain);
            uiModel.addAttribute("app", form);
            return view(device, "backend/application/screenshoots");
        } catch (Exception ex) {
            logger.error("Cannot upload screenshoot application.", ex);
        }
        return view(device, "maintenance");
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/upload/icon", method = RequestMethod.POST)
    @ResponseBody
    public LinkedList<AppFileMetaDomain> icon(MultipartHttpServletRequest request, Device device, Model uiModel) {
        //1. build an iterator
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf;
        LinkedList<AppFileMetaDomain> files = new LinkedList<AppFileMetaDomain>();
        AppFileMetaDomain fileMeta;

        //2. get each file
        while (itr.hasNext()) {

            //2.1 get next MultipartFile
            mpf = request.getFile(itr.next());
            logger.debug("File " + mpf.getOriginalFilename() + " uploaded! " + files.size());

            //2.2 if files > 10 remove the first from the list
            if (files.size() >= 10) {
                files.pop();
            }

            //2.3 create new fileMeta
            fileMeta = new AppFileMetaDomain();
            fileMeta.setFileName(mpf.getOriginalFilename());
            fileMeta.setFileSize(mpf.getSize() / 1024 + " Kb");
            fileMeta.setFileType(mpf.getContentType());

            try {
                fileMeta.setBytes(mpf.getBytes());

                // copy file to local disk (make sure the path "e.g. D:/temp/files" exists)            
                FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream("/home/tuanta/Desktop/data/icon/" + mpf.getOriginalFilename()));

            } catch (IOException ex) {
                // TODO Auto-generated catch block
                logger.error("Cannot upload icon application.", ex);
            }
            //2.4 add to files
            files.add(fileMeta);
        }
        // result will be like this
        // [{"fileName":"app_engine-85x77.png","fileSize":"8 Kb","fileType":"image/png"},...]
        return files;
    }
}
