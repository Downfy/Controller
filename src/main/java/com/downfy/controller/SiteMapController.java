/*
 * Copyright 2012 Hadoop Vietnam <admin@hadoopvietnam.com>.
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
package com.downfy.controller;

import com.downfy.common.MyCommon;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * HomeController.java
 * 
 * Version 1.0
 *
 * Date 01/01/2013
 * 
 * Home controller
 * 
 * Modification Logs:
 *  DATE            AUTHOR      DESCRIPTION
 *  --------------------------------------------------------
 *  01-Jan-2013     tuanta      Create first time
 *  25-jan-2013     tuanta      Refactor code
 *  25-jan-2013     tuanta      Add comments
 */
@RequestMapping({"/sitemap"})
@Controller
public class SiteMapController {

    private final Logger logger = LoggerFactory.getLogger(SiteMapController.class);

    private String createCategorySiteMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"http://mangtuyendung.vn/sitemap.xsl\"?>");
        sb.append("<urlset xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        sb.append("<url>");
        sb.append("<loc>http://mangtuyendung.vn/</loc>");
        sb.append("<lastmod>").append(MyCommon.formatDate("yyyy-MM-dd'T'HH:mm:ss", new Date())).append("+00:00").append("</lastmod>");
        sb.append("<changefreq>daily</changefreq>");
        sb.append("<priority>1.0</priority>");
        sb.append("</url>");
//        Collection<JobCategoryDomain> categoryDomains = this.categoryService.findAll();
//        for (JobCategoryDomain jobCategoryDomain : categoryDomains) {
//            sb.append("<url>");
//            sb.append("<loc>http://mangtuyendung.vn/tim-viec-lam-").append(jobCategoryDomain.getUrl().toLowerCase()).append(".html</loc>");
//            sb.append("<changefreq>daily</changefreq>");
//            sb.append("<priority>1.0</priority>");
//            sb.append("</url>");
//        }
//        Collection<JobLocationDomain> locationDomains = this.locationService.findAll();
//        for (JobLocationDomain jobLocationDomain : locationDomains) {
//            sb.append("<url>");
//            sb.append("<loc>http://mangtuyendung.vn/viec-lam-tai-").append(jobLocationDomain.getUrl().toLowerCase()).append(".html</loc>");
//            sb.append("<changefreq>daily</changefreq>");
//            sb.append("<priority>1.0</priority>");
//            sb.append("</url>");
//        }
        sb.append("</urlset>");
        return sb.toString();
    }

    /**
     * Create sitemap jobs format
     */
    private String createItemSiteMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"http://mangtuyendung.vn/sitemap.xsl\"?>");
        sb.append("<urlset xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        sb.append("<url>");
        sb.append("<loc>http://mangtuyendung.vn/</loc>");
        sb.append("<lastmod>").append(MyCommon.formatDate("yyyy-MM-dd'T'HH:mm:ss", new Date())).append("+00:00").append("</lastmod>");
        sb.append("<changefreq>daily</changefreq>");
        sb.append("<priority>1.0</priority>");
        sb.append("</url>");

//        int size = 2000;
//        try {
//            String query = "*:*";
//            QueryResponse response = this.solrService.search(query, 0, size);
//            int total = (int) response.getResults().getNumFound();
//            int page = total / size;
//            for (int i = 0; i <= page; i++) {
//                response = this.solrService.search(query, i * size, (i + 1) * size);
//                List<JobDomain> jobs = this.solrService.convertDocument(response.getResults());
//                for (JobDomain jobDomain : jobs) {
//                    sb.append("<url>");
//                    sb.append("<loc>http://mangtuyendung.vn/").append(JobUtils.convertURL(jobDomain.getTitle()).toLowerCase()).append("/id-").append(jobDomain.getSignature()).append(".html</loc>");
//                    sb.append("<changefreq>Monthly</changefreq>");
//                    sb.append("<priority>0.2</priority>");
//                    sb.append("</url>");
//                }
//            }
//        } catch (SolrServerException ex) {
//        }
        sb.append("</urlset>");
        return sb.toString();
    }

    /**
     * Create 2000 jobs newest format
     */
    private String createNewItemSiteMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"http://mangtuyendung.vn/sitemap.xsl\"?>");
        sb.append("<urlset xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        sb.append("<url>");
        sb.append("<loc>http://mangtuyendung.vn/</loc>");
        sb.append("<lastmod>").append(MyCommon.formatDate("yyyy-MM-dd'T'HH:mm:ss", new Date())).append("+00:00").append("</lastmod>");
        sb.append("<changefreq>daily</changefreq>");
        sb.append("<priority>1.0</priority>");
        sb.append("</url>");

//        int size = 2000;
//        try {
//            String query = "*:*";
//            QueryResponse response = this.solrService.search(query, 0, size);
//            List<JobDomain> jobs = this.solrService.convertDocument(response.getResults());
//            for (JobDomain jobDomain : jobs) {
//                sb.append("<url>");
//                sb.append("<loc>http://mangtuyendung.vn/").append(JobUtils.convertURL(jobDomain.getTitle()).toLowerCase()).append("/id-").append(jobDomain.getSignature()).append(".html</loc>");
//                sb.append("<changefreq>Monthly</changefreq>");
//                sb.append("<priority>0.2</priority>");
//                sb.append("</url>");
//            }
//        } catch (SolrServerException ex) {
//        }
        sb.append("</urlset>");
        return sb.toString();
    }

    /**
     * Write sitemap to disk.
     *
     * @param realPath Path to write
     * @param sitemap Sitemap content
     */
    private void createSitemap(String realPath, String sitemap) {
        try {
            FileWriter fw = new FileWriter(new File(realPath));
            fw.write(sitemap);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            this.logger.error(new StringBuilder().append("Create sitemap file ").append(realPath).append(" error.").toString(), e);
        }
    }
}