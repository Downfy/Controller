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

import com.downfy.form.backend.application.AppCreateForm;
import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/*
 * BackendDeveloperValidator.java
 *
 * Backend developer validator
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
@Service
public class BackendAppValidator
        implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return BackendAppValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AppCreateForm form = (AppCreateForm) target;
        if (Strings.isNullOrEmpty(form.getAppName())) {
            errors.rejectValue("appName", "app.namenotnull");
        }
        if (Strings.isNullOrEmpty(form.getAppAuthor())) {
            errors.rejectValue("appAuthor", "app.authornotnull");
        }
        if (Strings.isNullOrEmpty(form.getAppCategory())) {
            errors.rejectValue("appCategory", "app.categorynotnull");
        }
        if (Strings.isNullOrEmpty(form.getAppCategoryParent())) {
            errors.rejectValue("appCategoryParent", "app.categoryparentnotnull");
        }
    }
}
