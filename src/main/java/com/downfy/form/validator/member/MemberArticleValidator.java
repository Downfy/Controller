/*
 * Copyright (C) 2014 Downfy Team
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

import com.downfy.form.application.AppArticleForm;
import com.downfy.persistence.domain.category.CategoryDomain;
import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author Tran Anh Tuan<tk1cntt@gmail.com>
 */
@Service
public class MemberArticleValidator implements Validator {

    @Override
    public boolean supports(Class<?> type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void validate(Object target, Errors errors) {
        AppArticleForm form = (AppArticleForm) target;
        if (Strings.isNullOrEmpty(form.getAppTitle())) {
            errors.rejectValue("appTitle", "article.titlenotnull");
        }
        if (Strings.isNullOrEmpty(form.getAppDescription())) {
            errors.rejectValue("appDescription", "article.descriptionnotnull");
        }
        if (Strings.isNullOrEmpty(form.getAppThumbnail())) {
            errors.rejectValue("appThumbnail", "article.thumbnailnotnull");
        }
        if (form.getId() == 0) {
            errors.rejectValue("id", "article.idnotnull");
        }
    }
}
