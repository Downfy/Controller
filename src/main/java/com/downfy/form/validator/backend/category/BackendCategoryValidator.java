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
package com.downfy.form.validator.backend.category;

import com.downfy.persistence.domain.category.CategoryDomain;
import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/*
 * BackendCategoryValidator.java
 *
 * Admin category validator
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
@Service
public class BackendCategoryValidator
        implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return BackendCategoryValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CategoryDomain form = (CategoryDomain) target;
        if (Strings.isNullOrEmpty(form.getName())) {
            errors.rejectValue("name", "category.namenotnull");
        }
        if (Strings.isNullOrEmpty(form.getUrl())) {
            errors.rejectValue("url", "category.urlnotnull");
        }
    }
}
