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
package com.downfy.controller.backend.category;

import com.downfy.common.ErrorMessage;
import com.downfy.common.ValidationResponse;
import com.downfy.controller.AbstractController;
import com.downfy.controller.MyResourceMessage;
import com.downfy.form.CategorySelectorForm;
import com.downfy.form.validator.backend.category.BackendCategoryValidator;
import com.downfy.persistence.domain.category.CategoryDomain;
import com.downfy.service.category.CategoryService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * BackendCategoryController.java
 *
 * Admin category create controller
 *
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  26-Dec-2013     tuanta      Create first time
 */
@PreAuthorize("hasRole('ROLE_MANAGER')")
@RequestMapping("/backend/category")
@Controller
public class BackendCategoryController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(BackendCategoryController.class);
    @Autowired
    MyResourceMessage resourceMessage;
    @Autowired
    BackendCategoryValidator validator;
    @Autowired
    CategoryService categoryService;

    private void setCategories(Model uiModel) {
        List<CategoryDomain> apps = categoryService.findByParent("APPLICATIONS");
        List<CategoryDomain> games = categoryService.findByParent("GAMES");
        List<CategoryDomain> article = categoryService.findByParent("ARTICLE");
        Collections.sort(apps, new Comparator<CategoryDomain>() {
            @Override
            public int compare(CategoryDomain o1, CategoryDomain o2) {
                return o1.getUrl().compareTo(o2.getUrl());
            }
        });
        Collections.sort(games, new Comparator<CategoryDomain>() {
            @Override
            public int compare(CategoryDomain o1, CategoryDomain o2) {
                return o1.getUrl().compareTo(o2.getUrl());
            }
        });
        Collections.sort(article, new Comparator<CategoryDomain>() {
            @Override
            public int compare(CategoryDomain o1, CategoryDomain o2) {
                return o1.getUrl().compareTo(o2.getUrl());
            }
        });
        uiModel.addAttribute("apps", apps);
        uiModel.addAttribute("games", games);
        uiModel.addAttribute("article", article);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(HttpServletRequest request, Device device, Model uiModel) {
        try {
            uiModel.addAttribute("categoryForm", new CategoryDomain());
            setCategories(uiModel);
            return view(device, "backend/category");
        } catch (Exception ex) {
            logger.error("Cannot create category group.", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/{url}", method = RequestMethod.GET)
    public String getCategory(@PathVariable("url") String url, HttpServletRequest request, Device device, Model uiModel) {
        try {
            uiModel.addAttribute("categoryForm", categoryService.findByURL(url));
            setCategories(uiModel);
            return view(device, "backend/category");
        } catch (Exception ex) {
            logger.error("Cannot create category group.", ex);
        }
        return view(device, "maintenance");
    }

    @RequestMapping(value = "/parent/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<CategorySelectorForm> getCategoryList(@PathVariable("id") String id, HttpServletRequest request, Device device, Model uiModel) {
        return categoryService.findBySelectorParent(id);
    }

    @RequestMapping(value = "/json", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse createCategoryAjaxJson(@ModelAttribute("categoryForm") CategoryDomain domain, HttpServletRequest request, BindingResult bindingResult) {
        ValidationResponse res = new ValidationResponse();
        this.validator.validate(domain, bindingResult);
        if (!bindingResult.hasErrors()) {
            res.setStatus("SUCCESS");
        } else {
            res.setStatus("FAIL");
            List<FieldError> allErrors = bindingResult.getFieldErrors();
            List<ErrorMessage> errorMesages = new ArrayList<ErrorMessage>();
            for (FieldError objectError : allErrors) {
                errorMesages.add(new ErrorMessage(objectError.getField(), this.resourceMessage.getMessage(objectError.getCode(), request)));
            }
            res.setErrorMessageList(errorMesages);
        }
        return res;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createCategory(Device device, @ModelAttribute("categoryForm") CategoryDomain domain, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        this.validator.validate(domain, bindingResult);
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("categoryForm", domain);
            return view(device, "backend/category");
        }
        try {
            CategoryDomain current = categoryService.findByURL(domain.getUrl());
            if (current != null) {
                if (categoryService.update(domain)) {
                    setCategories(uiModel);
                    return "redirect:/backend/category.html";
                } else {
                    bindingResult.reject("category.updateerror");
                    uiModel.addAttribute("categoryForm", domain);
                    return view(device, "backend/category");
                }
            } else {
                if (categoryService.save(domain)) {
                    setCategories(uiModel);
                    return "redirect:/backend/category.html";
                } else {
                    bindingResult.reject("category.saveerror");
                    uiModel.addAttribute("categoryForm", domain);
                    return view(device, "backend/category");
                }
            }

        } catch (Exception ex) {
            logger.error("Create category error.", ex);
        }
        bindingResult.reject("category.error");
        uiModel.addAttribute("categoryForm", domain);
        return view(device, "backend/category");
    }
}
