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


import com.tcs.cba.weathersimulation.jaxb.WeatherStation;
import com.tcs.cba.weathersimulation.jaxb.WeatherStations;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

/**
 * Util class contains common functions used across the project.
 * @author Sruthi Sasikumar
 *
 */
public class Util {
	
	/**
	 * loadLocationSettings is used to obtain a list of the old weather data for the 
	 * weather stations, on which the weather simulation application runs.
	 * @param configPath: reads the value from the properties file
	 * @return list of weatherstation 
	 */
 public static List<WeatherStation> loadLocationSettings(String configPath) {
        File configFile = null;
        WeatherStations weatherStationlist = null;
        try {

            configFile = new File(configPath);
            final JAXBContext jaxbContext = JAXBContext.newInstance(WeatherStations.class);
            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            weatherStationlist = (WeatherStations) jaxbUnmarshaller.unmarshal(configFile);

        } catch (JAXBException exception) {
            exception.printStackTrace();
        }
        return (weatherStationlist == null) ? null : weatherStationlist.getWeatherStation();
    }

    /**
     * Calculates and returns the mean value of a list of double values.
     * @param factorList
     */
    public static double getAverage(List<Double> factorList) {
        double sum = 0;
        for(Double factor: factorList) {
            sum += factor;
        }
        return sum/factorList.size();
    }

    /**
     * This method helps rounding the double value to precision 1 (1 decimal space).
     * @param forecastedValue
     */
    public static double roundedValue(double forecastedValue) {
        double roundValue = (double) Math.round(forecastedValue * 10) / 10;
        return roundValue; 
    }

    /**
     * This method helps identifying the window edges whenever the offset moves towards end of list.
     * @param index
     * @param sizeOfList
     */
    public static int getResultantIndex(int index, int sizeOfList) {
        if(index >= sizeOfList) {
            index = index - sizeOfList;
        }
        return index;
    }

    /**
     *  This method helps identifying the window edges whenever the offset moves towards end of list.
     * @param windowEdge
     * @param offset
     * @param sizeOfList
     */
    public static int getIndexOfVariance(int windowEdge, int offset, int sizeOfList) {
        if(windowEdge - offset >=0) {
            return windowEdge - offset;
        } else {
            return sizeOfList + (windowEdge - offset);
        }
    }


}
