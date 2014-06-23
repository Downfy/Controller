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

import com.downfy.common.AppCommon;
import com.downfy.controller.AbstractController;
import com.downfy.controller.MyResourceMessage;
import com.downfy.form.application.article.ArticleForm;
import com.downfy.form.validator.member.MemberArticleValidator;
import com.downfy.persistence.domain.article.ArticleDomain;
import com.downfy.service.application.article.ArticleService;
import java.util.List;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
    MemberArticleValidator validator;
    @Autowired
    ArticleService articleService;
    @Autowired
    ServletContext context;

    @RequestMapping(method = RequestMethod.GET)
    public String viewNews(Device device, Model uiModel) {
        try {
            List<ArticleDomain> articles = articleService.findByCreaterAndType(getMyId(), AppCommon.ARTICLE_DEFAULT);
            uiModel.addAttribute("articles", articles);
            return view(device, "member/article/index");
        } catch (Exception ex) {
            logger.error("Cannot view list article.", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewNewsItem(@PathVariable("id") long id, Device device, Model uiModel) {
        try {
            uiModel.addAttribute("articleForm", articleService.findById(id));
            return view(device, "member/article/create");
        } catch (Exception ex) {
            logger.error("Cannot view ", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createNewsForm(Device device, Model uiModel) {
        try {
            ArticleForm articleForm = new ArticleForm();
            articleForm.setId(System.currentTimeMillis());
            uiModel.addAttribute("articleForm", articleForm);
            return view(device, "member/article/create");
        } catch (Exception ex) {
            logger.error("Cannot create article.", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createNews(@ModelAttribute("articleForm") ArticleForm form, BindingResult bindingResult, Device device, Model uiModel) {
        this.validator.validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("articleForm", form);
            return view(device, "member/article/create");
        }
        try {
            ArticleDomain domain = form.toArticle();
            if (articleService.isExsit(domain.getId())) {
                domain = articleService.findById(domain.getId());
                if (articleService.update(domain)) {
                    return "redirect:/member/article.html";
                }
            } else {
                domain.setType(AppCommon.ARTICLE_DEFAULT);
                domain.setCreater(getMyId());
                if (articleService.save(domain)) {
                    return "redirect:/member/article.html";
                }
            }
        } catch (Exception ex) {
            logger.error("Cannot create article.", ex);
        }
        bindingResult.reject("create.article.error");
        uiModel.addAttribute("articleForm", form);
        return view(device, "member/article/create");
    }
}
