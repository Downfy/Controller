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
package com.downfy.form.validator;

import com.downfy.common.MyValidator;
import com.downfy.persistence.domain.CategoryDomain;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/*
 * DeveloperValidator.java
 *
 * Developer validator
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
@Service
public class DeveloperValidator
        implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return DeveloperValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CategoryDomain form = (CategoryDomain) target;
        if (MyValidator.validateNullOrEmpty(form.getName())) {
            errors.rejectValue("name", "category.namenotnull");
        }
        if (MyValidator.validateNullOrEmpty(form.getUrl())) {
            errors.rejectValue("url", "category.urlnotnull");
        }
    }
}
