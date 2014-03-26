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

import com.downfy.form.backend.application.AppScreenShootForm;
import com.downfy.service.application.AppVersionService;
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
public class BackendAppScreenshootValidator
        implements Validator {

    @Autowired
    AppVersionService appVersionService;

    @Override
    public boolean supports(Class<?> clazz) {
        return BackendAppScreenshootValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AppScreenShootForm form = (AppScreenShootForm) target;
        if (form.getAppId() == 0) {
            errors.rejectValue("appId", "app.screenshootappidnotnull");
        }
        if (form.getScreenShootId() == 0) {
            errors.rejectValue("screenshootId", "app.screenshootidnotnull");
        }
    }
}
