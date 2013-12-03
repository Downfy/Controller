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
package com.downfy.controller;

import org.springframework.mobile.device.Device;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

@Controller
public abstract class AbstractController {

    public String getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (auth.getPrincipal() instanceof UserDetails) {
                return ((UserDetails) auth.getPrincipal()).getUsername();
            } else {
                return auth.getPrincipal().toString();
            }
        }
        return "anonymousUser";
    }

    public boolean isAuthenticated() {
        if ((SecurityContextHolder.getContext().getAuthentication() != null) && (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) && (!getUserId().equals("anonymousUser"))) {
            return true;
        }
        return false;
    }

    /**
     * Check device User-Agent
     *
     * @param device
     * @return
     */
    public boolean isMobile(Device device) {
        if (device != null && (device.isMobile() || device.isTablet())) {
            return true;
        }
        return false;
    }

    /**
     * Redirect view
     *
     * @param device Devices
     * @param path Address view
     * @return
     */
    public String view(Device device, String path) {
        if (isMobile(device)) {
            return "mobile/" + path;
        }
        return "home/" + path;
    }
}