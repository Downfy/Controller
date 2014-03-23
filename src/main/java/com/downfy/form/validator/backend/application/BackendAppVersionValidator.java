/*
 * Copyright 2013 Downfy Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.downfy.form.validator.backend.application;

import com.downfy.common.RegexpUtils;
import com.downfy.form.backend.application.AppApkForm;
import com.downfy.service.application.AppVersionService;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/*
 * BackendAppVersionValidator.java
 *
 * Backend app version validator
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  16-Mar-2014     tuanta      Create first time
 */
@Service
public class BackendAppVersionValidator
        implements Validator {

    @Autowired
    AppVersionService appVersionService;

    @Override
    public boolean supports(Class<?> clazz) {
        return BackendAppVersionValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AppApkForm form = (AppApkForm) target;
        String version = Strings.nullToEmpty(form.getAppVersion());
        if (Strings.isNullOrEmpty(form.getAppVersion()) || !RegexpUtils.validateVersion(version)) {
            errors.rejectValue("appVersion", "app.versionwrongformat");
        }
        if (Strings.isNullOrEmpty(form.getAppPath())) {
            errors.rejectValue("appPath", "app.apppathnotnull");
        }
        if (form.getAppSize() == 0) {
            errors.rejectValue("appSize", "app.appsizenotnull");
        }
        if (appVersionService.isExsit(form.getAppId(), version)) {
            errors.rejectValue("appVersion", "app.appversionexist");
        }
    }
}
