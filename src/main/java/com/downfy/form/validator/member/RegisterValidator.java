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
package com.downfy.form.validator.member;

import com.downfy.common.RegexpUtils;
import com.downfy.persistence.domain.AccountDomain;
import com.downfy.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.downfy.form.member.RegisterForm;
import com.google.common.base.Strings;

/*
 * RegisterValidator.java
 *
 * Register validator
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
@Service
public class RegisterValidator
        implements Validator {

    @Autowired
    AccountService accountService;

    @Override
    public boolean supports(Class<?> type) {
        return RegisterValidator.class.equals(type);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterForm form = (RegisterForm) target;

        if (Strings.isNullOrEmpty(form.getUsername())) {
            errors.rejectValue("username", "user.usernamenotnull");
        } else if (!RegexpUtils.validateUsername(form.getUsername())) {
            errors.rejectValue("username", "user.usernamenotmatch");
        } else {
            AccountDomain account = this.accountService.findByEmail(form.getUsername());
            if (account != null) {
                errors.rejectValue("username", "user.usernamenotavaiable");
            }
        }
        if (Strings.isNullOrEmpty(form.getPassword())) {
            errors.rejectValue("password", "user.passwordnotnull");
        }
        if (Strings.isNullOrEmpty(form.getRePassword())) {
            errors.rejectValue("rePassword", "user.passwordnotnull");
        }
        if (!form.getPassword().equals(form.getRePassword())) {
            errors.rejectValue("password", "user.passwordnotmatch");
            errors.rejectValue("rePassword", "user.passwordnotmatch");
        }
        if (Strings.isNullOrEmpty(form.getEmail())) {
            errors.rejectValue("email", "user.emailnotnull");
        } else if (!RegexpUtils.validateEmail(form.getEmail())) {
            errors.rejectValue("email", "user.emailnotmatch");
        } else {
            AccountDomain account = this.accountService.findByEmail(form.getEmail());
            if (account != null) {
                errors.rejectValue("email", "user.emailnotavaiable");
            }
        }
    }
}
