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

import com.maxmind.geoip2.model.CityResponse;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Tran Anh Tuan <tk1cntt@gmail.com>
 */
public class GeoLiteTest {

    @Test
    public void testGeoLite() {
        GeoLite geo = new GeoLite();
        CityResponse response = geo.getCity("210.211.97.105");
        Assert.assertNotNull(response);

        Assert.assertNotNull("VN", response.getCountry().getIsoCode());
        Assert.assertNotNull("Vietnam", response.getCountry().getName());
        Assert.assertNotNull("Hanoi", response.getCity().getName());
    }
}
