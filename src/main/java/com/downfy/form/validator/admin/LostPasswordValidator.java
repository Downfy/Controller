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
package com.downfy.form.validator.admin;

import com.downfy.persistence.domain.AccountDomain;
import com.downfy.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.downfy.common.MyValidator;
import com.downfy.form.admin.LostPasswordForm;

/*
 * LostPasswordValidator.java
 *
 * Lost password validator
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
@Service
public class LostPasswordValidator
        implements Validator {

    @Autowired
    AccountService accountService;

    @Override
    public boolean supports(Class<?> clazz) {
        return LostPasswordForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LostPasswordForm form = (LostPasswordForm) target;

        if (MyValidator.validateNullOrEmpty(form.getEmail())) {
            errors.rejectValue("email", "user.emailnotnull");
        } else if (!MyValidator.validateEmail(form.getEmail())) {
            errors.rejectValue("email", "user.emailnotmatch");
        } else {
            AccountDomain account = this.accountService.findByEmail(form.getEmail());
            if (account == null) {
                errors.rejectValue("email", "user.emailnotfound");
            }
        }
    }
}
