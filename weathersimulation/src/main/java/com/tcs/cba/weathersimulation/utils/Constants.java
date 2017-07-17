/**
 * Copyright (c) 2017, Sruthi Sasikumar. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
package com.tcs.cba.weathersimulation.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Constants {

    public static final String COMMA = ",";

    public static final String PIPE_DELIMITER = "|";

    public static final String FORECAST_DATE = "-d";

    public static final String FORECAST_DATE_RANGE = "-r";

    public static final String FORECAST_ALL_FUTURE_DATES = "-a";

    public static final String INPUT_DATE_PATTERN = "yyyy-M-d";

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'hh:mm:ss'Z'";

    public static final String YEAR_PATTERN = "yyyy";

    public static final String CONFIG_LOCATION_NAME = "CONFIG_LOCATION";

    public static final String WEATHER_RAINY = "RAINY";

    public static final String WEATHER_CLOUDY = "CLOUDY";

    public static final String WEATHER_WARM = "WARM";

    public static final String WEATHER_HOT = "HOT";

    public static final String WEATHER_COOL = "COOL";

    public static final String WEATHER_COLD = "COLD";

    public static Properties prop;

    public static String CONFIG_LOCATION = null;

    /**
     * <p>
     * Method loads the properties file.
     * </p>
     */
    public static void loadProperties(String propertiesPath) throws FileNotFoundException, IOException {
        prop = new Properties();
        prop.load(new FileInputStream(propertiesPath));

        CONFIG_LOCATION = prop.getProperty(CONFIG_LOCATION_NAME);
    }
}
