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
package com.tcs.cba.weathersimulation.process.impl;

import com.tcs.cba.weathersimulation.jaxb.WeatherStation;
import com.tcs.cba.weathersimulation.utils.Constants;
import com.tcs.cba.weathersimulation.utils.Util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static com.tcs.cba.weathersimulation.utils.Constants.loadProperties;

/**
 * WeatherDataSimulator is the main class of the application. This class invokes the processing
 * for predicting weatherdata factors.
 * There are 3 modes in which this application works which bears the following arguments:
 *  1. -a [propertyfile location]   => generate weather factors for all future dates.
 *  2. -d [propertyfile location] [startdate in yyyy-M-d]    => generate weather factors for a particular date.
 *  3. -r [propertyfile location] [startdate in yyyy-M-d] [enddate in yyyy-M-d] => generate weather factors for a range
 *                                                                                  of dates.
 * @author Sruthi Sasikumar
 */
public class WeatherDataSimulator {
    public static void main(String[] args)  throws FileNotFoundException, IOException {

        ExecutorService executorService = null;
        String mode = args[0];
        String propertiesPath = args[1];
        String startDate = null;
        String endDate = null;

        // loading properties file.
        loadProperties(propertiesPath);

        if (args.length > 2) {
            startDate = args[2];
        }
        if (args.length > 3) {
            endDate = args[3];
        }
        try {
            List<WeatherStation> weatherStations = Util.loadLocationSettings(Constants.CONFIG_LOCATION);
            if (weatherStations != null && weatherStations.size() > 0) {
                executorService = Executors.newFixedThreadPool(weatherStations.size());
                for (WeatherStation weatherStation : weatherStations) {
                    final PredictionThread forecastThreadObj = new PredictionThread(weatherStation, mode, startDate, endDate);
                    final Thread locationThread = new Thread(forecastThreadObj, weatherStation.getStatName());
                    executorService.execute(locationThread);
                }
            }
        } finally {
            executorService.shutdown();
        }
    }
}
