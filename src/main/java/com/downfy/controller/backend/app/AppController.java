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
import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
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
 * AppController.java
 *
 * App controller
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
    @Autowired
    ServletContext context;

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
            CategoryDomain categoryDomain = categoryService.findByURL(appDomain.getAppCategory());
            AppDetailForm form = new AppDetailForm();
            form.setAppId(appId);
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
    public AppFileMetaDomain icon(MultipartHttpServletRequest request, Device device, Model uiModel) {
        MultipartFile mpf = request.getFile("appIconFile");
        AppFileMetaDomain fileMeta = null;
        try {
            Md5PasswordEncoder encoder = new Md5PasswordEncoder();
            String appIconName = encoder.encodePassword(System.currentTimeMillis() + "", null);
            // copy file to local disk (make sure the path "e.g. D:/temp/files" exists)
            File f = new File(context.getRealPath("/"));
            String absolutePath = "/icon/" + encoder.encodePassword(getUsername(), null)
                    + "/" + appIconName + ".png";
            String localPath = f.getCanonicalPath() + "/" + encoder.encodePassword("data", null)
                    + absolutePath;
            f = new File(localPath);
            Files.createParentDirs(f);
            logger.debug("Create app icon " + localPath);
            Thumbnails.of(mpf.getInputStream())
                    .crop(Positions.CENTER)
                    .size(84, 84)
                    .outputFormat("png")
                    .toFile(localPath);
            //Create new fileMeta
            fileMeta = new AppFileMetaDomain();
            fileMeta.setFileName(absolutePath);
            fileMeta.setFileSize(mpf.getSize() + "");
            fileMeta.setFileType(mpf.getContentType());
        } catch (Exception ex) {
            logger.error("Cannot upload icon application.", ex);
        }
        return fileMeta;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/upload/apk", method = RequestMethod.POST)
    @ResponseBody
    public AppFileMetaDomain apk(MultipartHttpServletRequest request, Device device, Model uiModel) {
        MultipartFile mpf = request.getFile("appAPKFile");
        AppFileMetaDomain fileMeta = null;
        try {
            Md5PasswordEncoder encoder = new Md5PasswordEncoder();
            String appIconName = encoder.encodePassword(System.currentTimeMillis() + "", null);
            // copy file to local disk (make sure the path "e.g. D:/temp/files" exists)
            File f = new File(context.getRealPath("/"));
            String absolutePath = "/apk/" + encoder.encodePassword(getUsername(), null)
                    + "/" + appIconName + ".apk";
            String localPath = f.getCanonicalPath() + "/" + encoder.encodePassword("data", null)
                    + absolutePath;
            f = new File(localPath);
            Files.createParentDirs(f);
            logger.debug("Create app apk " + localPath);
            FileCopyUtils.copy(mpf.getInputStream(), new FileOutputStream(localPath));
            //Create new fileMeta
            fileMeta = new AppFileMetaDomain();
            fileMeta.setFileName(absolutePath);
            fileMeta.setFileSize(mpf.getSize() + "");
            fileMeta.setFileType(mpf.getContentType());
        } catch (Exception ex) {
            logger.error("Cannot upload icon application.", ex);
        }
        return fileMeta;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/upload/screenshoots", method = RequestMethod.POST)
    @ResponseBody
    public LinkedList<AppFileMetaDomain> screenshoots(MultipartHttpServletRequest request, Device device, Model uiModel) {
        //1. build an iterator
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf;
        LinkedList<AppFileMetaDomain> files = new LinkedList<AppFileMetaDomain>();
        AppFileMetaDomain fileMeta;

        //2. get each file
        while (itr.hasNext()) {

            //2.1 get next MultipartFile
            mpf = request.getFile(itr.next());

            //2.2 if files > 10 remove the first from the list
            if (files.size() >= 10) {
                files.pop();
            }

            Md5PasswordEncoder encoder = new Md5PasswordEncoder();
            String appIconName = encoder.encodePassword(System.currentTimeMillis() + "", null);
            // copy file to local disk (make sure the path "e.g. D:/temp/files" exists)
            File f = new File(context.getRealPath("/"));
            String absolutePath = "/screenshoots/" + encoder.encodePassword(getUsername(), null)
                    + "/" + appIconName + ".png";

            //2.3 create new fileMeta
            fileMeta = new AppFileMetaDomain();
            fileMeta.setFileName(absolutePath);
            fileMeta.setFileSize(mpf.getSize() + "");
            fileMeta.setFileType(mpf.getContentType());

            try {
                String localPath = f.getCanonicalPath() + "/" + encoder.encodePassword("data", null)
                        + absolutePath;
                f = new File(localPath);
                Files.createParentDirs(f);
                logger.debug("Create app screenshoot " + localPath);
                FileCopyUtils.copy(mpf.getInputStream(), new FileOutputStream(localPath));

                //2.4 add to files
                files.add(fileMeta);
            } catch (IOException ex) {
                logger.error("Cannot upload screenshoot application.", ex);
            }

        }
        return files;
    }
}
