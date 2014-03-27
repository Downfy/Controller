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

import com.downfy.form.backend.application.AppApkForm;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/*
 * BackendAppApkValidator.java
 *
 * Backend app apk validator
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  16-Mar-2014     tuanta      Create first time
 */
@Service
public class BackendAppApkValidator
        implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return BackendAppApkValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AppApkForm form = (AppApkForm) target;
        if (form.getAppId() == 0) {
            errors.rejectValue("appId", "app.apkappidnotnull");
        }
        if (form.getApkId() == 0) {
            errors.rejectValue("apkId", "app.apkidnotnull");
        }
    }
}
