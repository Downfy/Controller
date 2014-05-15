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
package com.downfy.controller.member.article;

import com.downfy.controller.AbstractController;
import com.downfy.controller.MyResourceMessage;
import com.downfy.form.application.AppReviewForm;
import com.downfy.form.validator.backend.application.BackendAppApkValidator;
import com.downfy.service.application.news.AppNewsService;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Tran Anh Tuan <tk1cntt@gmail.com>
 */
//@PreAuthorize("isAuthenticated()")
@RequestMapping("/member/article")
@Controller
public class MemberArticleController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(MemberArticleController.class);
    @Autowired
    MyResourceMessage resourceMessage;
    @Autowired
    BackendAppApkValidator validator;
    @Autowired
    AppNewsService appNewsService;
    @Autowired
    ServletContext context;

    @RequestMapping(value = "/news", method = RequestMethod.GET)
    public String viewNews(Device device, Model uiModel) {
        try {
            return view(device, "member/news/index");
        } catch (Exception ex) {
            logger.error("Cannot view list news.", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/news/{id}", method = RequestMethod.GET)
    public String viewNewsItem(Device device, Model uiModel) {
        try {
            return view(device, "member/news/view");
        } catch (Exception ex) {
            logger.error("Cannot view ", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/news/create", method = RequestMethod.GET)
    public String createNewsForm(Device device, Model uiModel) {
        try {
            uiModel.addAttribute("articleForm", new AppReviewForm());
            return view(device, "member/news/create");
        } catch (Exception ex) {
            logger.error("Cannot create news.", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/news/create", method = RequestMethod.POST)
    public String createNews(Device device, Model uiModel) {
        try {
            return view(device, "member/news/create");
        } catch (Exception ex) {
            logger.error("Cannot create news.", ex);
        }
        return "redirect:/member/news.html";
    }
}
