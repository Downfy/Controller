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
package com.downfy.geolite;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tran Anh Tuan <tk1cntt@gmail.com>
 */
public class GeoLite {

    private String dbPath = "";

    public GeoLite() {
        dbPath = "./GeoLite2-City.mmdb";
    }

    public GeoLite(String path) {
        dbPath = path;
    }

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(GeoLite.class);

    public CityResponse getCity(String ip) {
        try {
            logger.debug("Check geo from client ip " + ip);
            // A File object pointing to your GeoIP2 or GeoLite2 database
            File database = new File(dbPath);

            // This creates the DatabaseReader object, which should be reused across lookups.
            DatabaseReader reader = new DatabaseReader.Builder(database).build();

            // Replace "city" with the appropriate method for your database, e.g., "country".
            CityResponse response = reader.city(InetAddress.getByName(ip));

            return response;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } catch (GeoIp2Exception ex) {
            logger.info(ex.getMessage());
        }
        return null;
    }
}
