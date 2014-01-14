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

import com.downfy.persistence.domain.AccountDomain;
import com.downfy.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.downfy.form.member.ChangePasswordForm;
import com.google.common.base.Strings;

/*
 * ChangePasswordValidator.java
 *
 * Change password validator
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
@Service
public class ChangePasswordValidator
        implements Validator {

    @Autowired
    AccountService accountService;
    @Autowired
    ShaPasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return ChangePasswordForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChangePasswordForm form = (ChangePasswordForm) target;
        if (Strings.isNullOrEmpty(form.getOldPassword())) {
            errors.rejectValue("oldPassword", "changepassword.oldpasswordnotnull");
        }
        if (Strings.isNullOrEmpty(form.getNewPassword())) {
            errors.rejectValue("newPassword", "changepassword.newpasswordnotnull");
        }
        if (Strings.isNullOrEmpty(form.getAgainPassword())) {
            errors.rejectValue("againPassword", "changepassword.againpasswordnotnull");
        } else if (!form.getNewPassword().equals(form.getAgainPassword())) {
            errors.rejectValue("newPassword", "changepassword.passwordsnomatch");

            errors.rejectValue("againPassword", "changepassword.passwordsnomatch");
        } else {
            String oldPassword = form.getOldPassword();
            String encryptPassword = this.passwordEncoder.encodePassword(oldPassword, null);
            try {
                AccountDomain accountDomain = this.accountService.findByEmailAndPassword(getUsername(), encryptPassword);
                if (accountDomain == null) {
                    errors.rejectValue("oldPassword", "changepassword.invalidpassword");
                }
            } catch (Exception e) {
                errors.rejectValue("oldPassword", "changepassword.invalidpassword");
            }
        }
    }

    public String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if ((auth != null) && ((auth.getPrincipal() instanceof UserDetails))) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        }
        return auth.getPrincipal().toString();
    }
}
