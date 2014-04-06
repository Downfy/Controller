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
import com.downfy.common.Utils;
import com.downfy.controller.AbstractController;
import com.downfy.controller.MyResourceMessage;
import com.downfy.form.backend.application.AppCreateForm;
import com.downfy.form.backend.application.AppDetailForm;
import com.downfy.form.validator.backend.application.BackendAppValidator;
import com.downfy.persistence.domain.AppFileMetaDomain;
import com.downfy.persistence.domain.application.AppDomain;
import com.downfy.persistence.domain.application.AppUploadedDomain;
import com.downfy.persistence.domain.application.AppVersionDomain;
import com.downfy.persistence.domain.category.CategoryDomain;
import com.downfy.service.application.AppApkService;
import com.downfy.service.application.AppScreenshootService;
import com.downfy.service.application.AppService;
import com.downfy.service.application.AppUploadedService;
import com.downfy.service.application.AppVersionService;
import com.downfy.service.category.CategoryService;
import com.google.api.client.repackaged.com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.apache.commons.io.FileUtils;
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
 * AppController.java
 *
 * App controller
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Dec-2013     tuanta      Create first time
 */
@PreAuthorize("hasRole('ROLE_MANAGER')")
@RequestMapping("/backend/application")
@Controller
public class AppController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(AppController.class);
    @Autowired
    MyResourceMessage resourceMessage;
    @Autowired
    BackendAppValidator validator;
    @Autowired
    AppService appService;
    @Autowired
    AppVersionService appVersionService;
    @Autowired
    AppScreenshootService appScreenshootService;
    @Autowired
    AppUploadedService appUploadedService;
    @Autowired
    AppApkService appApkService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ServletContext context;

    private void setApps(Model uiModel) {
        List<AppDomain> apps = appService.findByDeveloper(getMyId());
        uiModel.addAttribute("apps", apps);
    }

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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") long appId, HttpServletRequest request, Device device, Model uiModel) {
        try {
            AppDomain appDomain = appService.findById(appId);
            if (appDomain.getCreater() == getMyId()) {
                logger.debug("==> Load app " + appDomain.toString());
                CategoryDomain categoryDomain = categoryService.findByURL(appDomain.getAppCategory());
                AppDetailForm form = getAppDetailForm(appId, appDomain, categoryDomain);
                uiModel.addAttribute("app", form);
                return view(device, "backend/application/detail");
            } else {
                return "redirect:/backend/application.html";
            }
        } catch (Exception ex) {
            logger.error("Cannot create detail application.", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/{id}/apk", method = RequestMethod.GET)
    public String apk(@PathVariable("id") long appId, HttpServletRequest request, Device device, Model uiModel) {
        try {
            AppDomain curentApp = appService.findById(appId);
            if (curentApp != null && curentApp.getCreater() == getMyId()) {
                logger.info("Load apk of app " + appId);
                uiModel.addAttribute("app", curentApp);
                List<AppVersionDomain> versions = appVersionService.findByApp(appId);
                uiModel.addAttribute("apps", versions);
                uiModel.addAttribute("apks", appApkService.findByApp(appId));
                uiModel.addAttribute("files", appUploadedService.findByType(appId, AppCommon.FILE_APK));
                return view(device, "backend/application/apk");
            } else {
                return "redirect:/backend/application.html";
            }
        } catch (Exception ex) {
            logger.error("Cannot upload application.", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/{id}/screenshoots", method = RequestMethod.GET)
    public String screenshoots(@PathVariable("id") long appId, HttpServletRequest request, Device device, Model uiModel) {
        try {
            AppDomain currentApp = appService.findById(appId);
            if (currentApp.getCreater() == getMyId()) {
                logger.info("Load screenshoot of app " + appId);
                uiModel.addAttribute("verifyscreenshoots", appScreenshootService.findByApp(appId));
                uiModel.addAttribute("app", currentApp);
                uiModel.addAttribute("screenshoots", appUploadedService.findByType(appId, AppCommon.FILE_SCREENSHOOT));
                return view(device, "backend/application/screenshoots");
            } else {
                return "redirect:/backend/application.html";
            }
        } catch (Exception ex) {
            logger.error("Cannot upload screenshoot application.", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/{id}/requestpublish", method = RequestMethod.GET)
    public String requestPublish(@PathVariable("id") long appId, HttpServletRequest request, Device device, Model uiModel) {
        try {
            AppDomain currentApp = appService.findById(appId);
            if (currentApp.getCreater() == getMyId()) {
                logger.info("Request publish of app " + appId);
                if (!appService.approveApp(appId)) {
                    return view(device, "maintenance");
                }
            }
            return "redirect:/backend/application.html";
        } catch (Exception ex) {
            logger.error("Cannot request publish application.", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/upload/{id}/icon", method = RequestMethod.POST)
    @ResponseBody
    public AppFileMetaDomain icon(@PathVariable("id") long appId, MultipartHttpServletRequest request, Device device, Model uiModel) {
        MultipartFile mpf = request.getFile("appIconFile");
        AppFileMetaDomain fileMeta = new AppFileMetaDomain();
        if (Objects.equal("image/gif", mpf.getContentType())
                || Objects.equal("image/jpeg", mpf.getContentType())
                || Objects.equal("image/pjpeg", mpf.getContentType())
                || Objects.equal("image/png", mpf.getContentType())) {
            storeFileIcon(mpf, fileMeta, appId);
        } else {
            logger.debug("Don't support format icon content type: " + mpf.getContentType());
            fileMeta.setFileName("");
            fileMeta.setFileSize(0);
            fileMeta.setFileStatus(AppCommon.UPLOAD_FAILRE);
        }
        fileMeta.setFileType(mpf.getContentType());
        return fileMeta;
    }

    @RequestMapping(value = "/upload/{id}/apk", method = RequestMethod.POST)
    @ResponseBody
    public AppFileMetaDomain apk(@PathVariable("id") long appId, MultipartHttpServletRequest request, Device device, Model uiModel) {
        MultipartFile mpf = request.getFile("appAPKFile");
        AppFileMetaDomain fileMeta = new AppFileMetaDomain();

        if (Objects.equal("application/vnd.android.package-archive", mpf.getContentType())
                || Objects.equal("application/octet-stream", mpf.getContentType())) {
            storeFileApk(mpf, fileMeta, appId);
        } else {
            logger.debug("Don't support format apk content type: " + mpf.getContentType());
            fileMeta.setFileName("");
            fileMeta.setFileSize(0);
        }
        fileMeta.setFileType(mpf.getContentType());
        return fileMeta;
    }

    private void checkFileApkUploaded(AppDomain appDomain, ApkMeta apkMeta, AppFileMetaDomain fileMeta, long appId, long size, String localPath, String absolutePath) {
        long creater = appApkService.getCreater(apkMeta.getPackageName());
        if (creater == 0) {
            creater = appUploadedService.getCreater(apkMeta.getPackageName(), AppCommon.FILE_APK);
            if (creater == 0) {
                List<AppUploadedDomain> apks = appUploadedService.findByType(appId, AppCommon.FILE_APK);
                if (apks.isEmpty()) {
                    saveUploadFile(appId, apkMeta.getVersionName(), apkMeta.getPackageName(), size, localPath, absolutePath, AppCommon.FILE_APK);
                } else {
                    AppUploadedDomain uploadedDomain = apks.get(0);
                    if (Objects.equal(uploadedDomain.getAppPackage(), apkMeta.getPackageName())) {
                        if (appUploadedService.isExsit(apkMeta.getPackageName(), apkMeta.getVersionName())) {
                            fileMeta.setFileStatus(AppCommon.UPLOAD_FILE_EXIST);
                        } else if (appApkService.isExsit(apkMeta.getPackageName(), apkMeta.getVersionName())) {
                            fileMeta.setFileStatus(AppCommon.UPLOAD_FILE_EXIST);
                        } else if (appVersionService.isExsit(appId, apkMeta.getVersionName())) {
                            fileMeta.setFileStatus(AppCommon.UPLOAD_FILE_EXIST);
                        } else {
                            //Save info of file uploaded
                            saveUploadFile(appId, apkMeta.getVersionName(), apkMeta.getPackageName(), size, localPath, absolutePath, AppCommon.FILE_APK);
                        }
                    } else {
                        fileMeta.setFileStatus(AppCommon.UPLOAD_PACKAGE_NOT_MATCH);
                    }
                }
            } else if (getMyId() == creater) {
                if (appUploadedService.isExsit(apkMeta.getPackageName(), apkMeta.getVersionName())) {
                    if (appUploadedService.isExsit(apkMeta.getPackageName(), appId)) {
                        fileMeta.setFileStatus(AppCommon.UPLOAD_FILE_EXIST);
                    } else {
                        fileMeta.setFileStatus(AppCommon.UPLOAD_PACKAGE_AVAILABLE_ANOTHER);
                    }
                } else if (appApkService.isExsit(apkMeta.getPackageName(), apkMeta.getVersionName())) {
                    fileMeta.setFileStatus(AppCommon.UPLOAD_FILE_EXIST);
                } else if (appVersionService.isExsit(appId, apkMeta.getVersionName())) {
                    fileMeta.setFileStatus(AppCommon.UPLOAD_FILE_EXIST);
                } else {
                    fileMeta.setFileStatus(AppCommon.UPLOAD_PACKAGE_AVAILABLE_ANOTHER);
                }
            } else {
                fileMeta.setFileStatus(AppCommon.UPLOAD_PACKAGE_AVAILABLE);
            }
        } else if (getMyId() == creater) {
            if (appApkService.isExsit(apkMeta.getPackageName(), apkMeta.getVersionName())) {
                if (appDomain.getAppId() == appId) {
                    fileMeta.setFileStatus(AppCommon.UPLOAD_FILE_EXIST);
                } else {
                    fileMeta.setFileStatus(AppCommon.UPLOAD_PACKAGE_AVAILABLE_ANOTHER);
                }
            } else {
                saveUploadFile(appId, apkMeta.getVersionName(), apkMeta.getPackageName(), size, localPath, absolutePath, AppCommon.FILE_APK);
            }
        } else {
            fileMeta.setFileStatus(AppCommon.UPLOAD_PACKAGE_AVAILABLE);
        }
    }

    @RequestMapping(value = "/upload/{id}/screenshoots", method = RequestMethod.POST)
    @ResponseBody
    public LinkedList<AppFileMetaDomain> screenshoots(@PathVariable("id") long appId, MultipartHttpServletRequest request, Device device, Model uiModel) {
        //1. build an iterator
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf;
        LinkedList<AppFileMetaDomain> files = new LinkedList<AppFileMetaDomain>();

        //2. get each file
        while (itr.hasNext()) {
            //2.1 get next MultipartFile
            mpf = request.getFile(itr.next());

            if (Objects.equal("image/gif", mpf.getContentType())
                    || Objects.equal("image/jpeg", mpf.getContentType())
                    || Objects.equal("image/pjpeg", mpf.getContentType())
                    || Objects.equal("image/png", mpf.getContentType())) {
                storeFileScreenShoots(mpf, appId, files);
            } else {
                logger.debug("Don't support format screen shoot content type: " + mpf.getContentType());
            }
        }
        return files;
    }

    private AppDetailForm getAppDetailForm(long appId, AppDomain appDomain, CategoryDomain categoryDomain) {
        AppDetailForm form = new AppDetailForm();
        form.setAppId(appId);
        form.fromAppDomain(appDomain);
        form.setAppCategoryParent(categoryDomain.getParent());
        form.setAppCategories(categoryService.findBySelectorParent(categoryDomain.getParent()));
        return form;
    }

    private void saveUploadFile(long appId, long size, String realPath, String absolutePath, int type) {
        AppUploadedDomain appUploadDomain = new AppUploadedDomain();
        appUploadDomain.setId(System.currentTimeMillis());
        appUploadDomain.setAppId(appId);
        appUploadDomain.setAppPath(absolutePath);
        appUploadDomain.setAppRealPath(realPath);
        appUploadDomain.setCreater(getMyId());
        appUploadDomain.setCreated(new Date());
        appUploadDomain.setType(type);
        appUploadDomain.setSize(size);

        appUploadedService.save(appUploadDomain);
    }

    private void saveUploadFile(long appId, String appVersion, String appPackage, long size, String realPath, String absolutePath, int type) {
        AppUploadedDomain appUploadDomain = new AppUploadedDomain();
        appUploadDomain.setId(System.currentTimeMillis());
        appUploadDomain.setAppId(appId);
        appUploadDomain.setAppPath(absolutePath);
        appUploadDomain.setAppRealPath(realPath);
        appUploadDomain.setCreater(getMyId());
        appUploadDomain.setCreated(new Date());
        appUploadDomain.setType(type);
        appUploadDomain.setSize(size);
        appUploadDomain.setAppVersion(appVersion);
        appUploadDomain.setAppPackage(appPackage);

        appUploadedService.save(appUploadDomain);
    }

    private String generatorPathStoreApk() {
        // copy file to local disk (make sure the path "e.g. D:/temp/files" exists)
        String absolutePath = File.separator + "apk"
                + File.separator + Utils.folderByCurrentTime()
                + File.separator + Utils.toMd5(getUsername())
                + File.separator + Utils.toMd5(System.currentTimeMillis() + "") + ".apk";
        return absolutePath;
    }

    private void responseFileApkStatus(String localPath, AppFileMetaDomain fileMeta, long appId, long size, String absolutePath) {
        ApkMeta apkMeta = Utils.getApkMeta(localPath);
        if (apkMeta != null) {
            fileMeta.setFileVersion(apkMeta.getVersionName());
            fileMeta.setFilePackage(apkMeta.getPackageName());
            AppDomain appPackageDomain = appService.findByPackage(apkMeta.getPackageName());
            if (appPackageDomain != null) {
                if (appPackageDomain.getCreater() == getMyId()) {
                    if (appPackageDomain.getAppId() == appId) {
                        checkFileApkUploaded(appPackageDomain, apkMeta, fileMeta, appId, size, localPath, absolutePath);
                    } else {
                        fileMeta.setFileStatus(AppCommon.UPLOAD_PACKAGE_AVAILABLE_ANOTHER);
                    }
                } else {
                    AppVersionDomain appCurrentDomain = appVersionService.findById(appId);
                    if (appCurrentDomain == null) {
                        checkFileApkUploaded(appPackageDomain, apkMeta, fileMeta, appId, size, localPath, absolutePath);
                    } else if (Objects.equal(appCurrentDomain.getAppPackage(), appPackageDomain.getAppPackage())) {
                        fileMeta.setFileStatus(AppCommon.UPLOAD_FILE_EXIST);
                    } else {
                        fileMeta.setFileStatus(AppCommon.UPLOAD_PACKAGE_NOT_MATCH);
                    }
                }
            } else {
                AppDomain appCurrentDomain = appService.findById(appId);
                if (Objects.equal(appCurrentDomain.getAppPackage(), apkMeta.getPackageName())) {
                    if (appCurrentDomain.getCreater() == getMyId()) {
                        if (appCurrentDomain.getAppId() == appId) {
                            fileMeta.setFileStatus(AppCommon.UPLOAD_PACKAGE_AVAILABLE);
                        } else {
                            checkFileApkUploaded(appPackageDomain, apkMeta, fileMeta, appId, size, localPath, absolutePath);
                        }
                    } else {
                        fileMeta.setFileStatus(AppCommon.UPLOAD_PACKAGE_AVAILABLE);
                    }
                } else {
                    if (Strings.isNullOrEmpty(appCurrentDomain.getAppPackage())) {
                        checkFileApkUploaded(appPackageDomain, apkMeta, fileMeta, appId, size, localPath, absolutePath);
                    } else {
                        fileMeta.setFileStatus(AppCommon.UPLOAD_PACKAGE_NOT_MATCH);
                    }
                }
            }
        } else {
            fileMeta.setFileStatus(AppCommon.UPLOAD_FILE_NOT_SUPPORT);
        }
    }

    private String generatorPathStoreScreenShoots() {
        // copy file to local disk (make sure the path "e.g. D:/temp/files" exists)
        String absolutePath = File.separator + "screenshoots"
                + File.separator + Utils.folderByCurrentTime()
                + File.separator + Utils.toMd5(getUsername())
                + File.separator + Utils.toMd5(System.currentTimeMillis() + "") + ".png";
        return absolutePath;
    }

    private AppFileMetaDomain createAppFileMetaDomain(String absolutePath, MultipartFile mpf) {
        AppFileMetaDomain fileMeta;
        //2.3 create new fileMeta
        fileMeta = new AppFileMetaDomain();
        fileMeta.setFileName(absolutePath);
        fileMeta.setFileSize(mpf.getSize());
        fileMeta.setFileType(mpf.getContentType());
        return fileMeta;
    }

    private void storeFileApk(MultipartFile mpf, AppFileMetaDomain fileMeta, long appId) {
        File f = new File(context.getRealPath("/"));
        String absolutePath = generatorPathStoreApk();
        try {
            String localPath = f.getCanonicalPath() + File.separator + Utils.toMd5("data")
                    + absolutePath;
            f = new File(localPath);
            Files.createParentDirs(f);

            fileMeta.setFileName(absolutePath);
            fileMeta.setFileSize(mpf.getSize());

            logger.debug("Create app apk " + localPath);
            FileCopyUtils.copy(mpf.getInputStream(), new FileOutputStream(localPath));
            responseFileApkStatus(localPath, fileMeta, appId, mpf.getSize(), absolutePath);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            fileMeta.setFileStatus(AppCommon.UPLOAD_FAILRE);
        }
        if (fileMeta.getFileStatus() != AppCommon.UPLOAD_SUCCESS) {
            FileUtils.deleteQuietly(f);
        }
    }

    private String generatorPathStoreIcon() {
        String absolutePath = File.separator + "icon"
                + File.separator + Utils.folderByCurrentTime()
                + File.separator + Utils.toMd5(getUsername())
                + File.separator + Utils.toMd5(System.currentTimeMillis() + "") + ".png";
        return absolutePath;
    }

    private void storeFileIcon(MultipartFile mpf, AppFileMetaDomain fileMeta, long appId) {
        try {
            // copy file to local disk (make sure the path "e.g. D:/temp/files" exists)
            File f = new File(context.getRealPath("/"));
            String absolutePath = generatorPathStoreIcon();
            String localPath = f.getCanonicalPath() + File.separator + Utils.toMd5("data")
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
            fileMeta.setFileName(absolutePath);
            fileMeta.setFileSize(mpf.getSize());

            //Save info of file uploaded
            saveUploadFile(appId, mpf.getSize(), localPath, absolutePath, AppCommon.FILE_ICON);
        } catch (Exception ex) {
            fileMeta.setFileName("");
            fileMeta.setFileSize(0);
        }
    }

    private void storeFileScreenShoots(MultipartFile mpf, long appId, LinkedList<AppFileMetaDomain> files) {
        AppFileMetaDomain fileMeta;
        File f = new File(context.getRealPath("/"));
        String absolutePath = generatorPathStoreScreenShoots();
        fileMeta = createAppFileMetaDomain(absolutePath, mpf);
        try {
            String localPath = f.getCanonicalPath() + File.separator + Utils.toMd5("data")
                    + absolutePath;
            f = new File(localPath);
            Files.createParentDirs(f);
            logger.debug("Create app screenshoot " + localPath);
            Thumbnails.of(mpf.getInputStream())
                    .crop(Positions.CENTER)
                    .size(240, 320)
                    .outputFormat("png")
                    .toFile(localPath);

            //Save info of file uploaded
            saveUploadFile(appId, mpf.getSize(), localPath, absolutePath, AppCommon.FILE_SCREENSHOOT);

            //2.4 add to files
            files.add(fileMeta);
        } catch (IOException ex) {
            FileUtils.deleteQuietly(f);
            logger.error("Cannot upload screenshoot application.", ex);
        }
    }
}
