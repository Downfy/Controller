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

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.downfy.form.member.ResetPasswordForm;
import com.google.common.base.Objects;

/*
 * ResetPasswordValidator.java
 *
 * Reset password validator
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Nov-2013     tuanta      Create first time
 */
@Service
public class ResetPasswordValidator
        implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ResetPasswordValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ResetPasswordForm form = (ResetPasswordForm) target;
        if (Objects.equal(form.getNewPassword(), form.getAgainPassword())) {
            errors.rejectValue("againPassword", "user.resetpasswordnotmatch");
        }
    }
}
