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

import com.tcs.cba.weathersimulation.datamodel.WeatherParameters;
import com.tcs.cba.weathersimulation.process.simulationmodel.WeatherSimulationModel;

import java.util.*;

import static com.tcs.cba.weathersimulation.utils.Util.*;
import static com.tcs.cba.weathersimulation.utils.Constants.*;

/**
 * OscillationModel is the class where the core functional 
 * implementations are done to obtain the  weather predictions for
 * any date.
 * @author Sruthi Sasikumar
 *
 */
public class OscillationModel implements WeatherSimulationModel{

    private Map<Integer, List<WeatherParameters>> oldWeatherDataMap;

    public OscillationModel() {}

    public OscillationModel(Map<Integer, List<WeatherParameters>> historyMap) {
        this.oldWeatherDataMap = historyMap;
    }
    
    /**
     * This method is used to predict the weather factors for a single day (-d option).
     */
    @Override
    public String predictWeatherForADay(int day, int year) {
        List<WeatherParameters> historyDayList = oldWeatherDataMap.get(day);
        if (day == 366) {
            historyDayList.addAll(oldWeatherDataMap.get(day - 1));
        }
        Collections.shuffle(historyDayList);

        //process weather data
        return getLocationWeatherInfo(historyDayList);

    }

    /**
     * This Method performs the mathematical calculations to obtain the weatherinfo in the
     * required format as a string.
     * It is done using moving average(MA),Oscillation(OSC),
     * Rate of Change(ROC)
     * 
     * @param historyDayList
     * @return predicted weather info
     */
    public String getLocationWeatherInfo(List<WeatherParameters> historyDayList) {
        WeatherParameters wpBean = null;
        Random r = new Random();
        int sizeOfList = historyDayList.size();
        int lowWindow1 = sizeOfList/3;
        int highWindow1 = (sizeOfList/2)+1;
        int window1 = r.nextInt(highWindow1-lowWindow1) + lowWindow1; // value between 3 and 5 both inclusive

        int lowWindow2 = (sizeOfList/2)+1;
        int highWindow2 = sizeOfList;
        int window2 = r.nextInt(highWindow2-lowWindow2) + lowWindow2; // value between 6 and 7 both inclusive

        int offset = r.nextInt(sizeOfList); // value between 0 and (sizeOfList - 1) both inclusive

        List<Double> temperatureList = new ArrayList<Double>(sizeOfList);
        List<Double> pressureList = new ArrayList<Double>(sizeOfList);
        List<Double> humidityList = new ArrayList<Double>(sizeOfList);
        for (int i = 0; i < sizeOfList; i++) {
            wpBean = historyDayList.get(i);
            temperatureList.add(wpBean.getTemperature());
            pressureList.add(wpBean.getPressure());
            humidityList.add(wpBean.getHumidity());
        }

        double temperatureMovingAverage1 = getMovingAverage(window1, offset, temperatureList);
        double temperatureMovingAverage2 = getMovingAverage(window2, offset, temperatureList);

        double pressureMovingAverage1 = getMovingAverage(window1, offset, pressureList);
        double pressureMovingAverage2 = getMovingAverage(window2, offset, pressureList);

        double humidityMovingAverage1 = getMovingAverage(window1, offset, humidityList);
        double humidityMovingAverage2 = getMovingAverage(window2, offset, humidityList);

        double temperatureOscillation  = getOscillation(temperatureMovingAverage1, temperatureMovingAverage2);
        double pressureOscillation     = getOscillation(pressureMovingAverage1, pressureMovingAverage2);
        double humidityOscillation     = getOscillation(humidityMovingAverage1, humidityMovingAverage2);

        int windowEdge = getResultantIndex(window1 + offset - 1, sizeOfList);  // minimum value 3
        // timestep minimum between 0 and 2
        int timeStep = r.nextInt(getIndexOfVariance(windowEdge, offset, sizeOfList)+1) + offset;

        int resultantTimeStep = getResultantIndex(timeStep, sizeOfList);

        double rateOfChangeTemp = getRateOfChange(temperatureList.get(windowEdge), temperatureList.get(resultantTimeStep));
        double rateOfChangePressure = getRateOfChange(pressureList.get(windowEdge), pressureList.get(resultantTimeStep));
        double rateOfChangeHumidity = getRateOfChange(humidityList.get(windowEdge), humidityList.get(resultantTimeStep));

        double resultantTemperature = roundedValue(temperatureList.get(temperatureList.size()-1) +
                (rateOfChangeTemp * temperatureOscillation));
        double resultantPressure    = roundedValue(pressureList.get(pressureList.size()-1) +
                (rateOfChangePressure * pressureOscillation));
        int resultantHumidity    = (int) roundedValue(humidityList.get(humidityList.size()-1) +
                (rateOfChangeHumidity * humidityOscillation));

        String weatherType = getWeatherType(resultantTemperature, resultantHumidity);
        return weatherType + PIPE_DELIMITER + resultantTemperature + PIPE_DELIMITER + resultantPressure + PIPE_DELIMITER
               + resultantHumidity;
    }

    /**
     * Method calculates the rate of change. It can be positive or negative
     * 
     * @param factorT
     * @param factorTMinusA
     * @return rate of change
     */
    public double getRateOfChange(Double factorT, Double factorTMinusA) {
        double rateOfChange = 1 - (factorT/factorTMinusA);
        return rateOfChange;
    }

    /**
     * Method calculates the oscillation based on the window selected
     * 
     * @param factorMovingAvg1
     * @param factorMovingAvg2
     * @return oscillation
     */
    private double getOscillation(double factorMovingAvg1, double factorMovingAvg2) {
        return Math.abs(factorMovingAvg1 - factorMovingAvg2);
    }

    
    /**
     * Method used to find the moving averages of the specific weather parameter 
     * based on the window selected
     * 
     * @param window
     * @param offset
     * @param weatherFactorList
     * @return moving average
     */
    public double getMovingAverage(int window, int offset, List<Double> weatherFactorList) {
        List<Double> sampledDataList = new ArrayList<Double>();
        int sizeOfList = weatherFactorList.size();
        if(offset + window < sizeOfList) {
            sampledDataList = weatherFactorList.subList(offset, offset + window);
        } else {
            sampledDataList.addAll(weatherFactorList.subList(offset, sizeOfList));
            sampledDataList.addAll(weatherFactorList.subList(0, getResultantIndex(offset + window, sizeOfList)));
        }
        return getAverage(sampledDataList);
    }

    /**
     * Method helps predict the weather type based on the weather factors.
     *
     * @param temperature
     * @param humidity
     * @return weatherType as String
     */
    private String getWeatherType(double temperature, int humidity) {
        String weatherType = null;

        // conditions governing weatherType.
        if (temperature > 20 && humidity > 70 && humidity < 80) {
            weatherType = WEATHER_CLOUDY;
        } else if (temperature > 20 && humidity >= 80) {
            weatherType = WEATHER_RAINY;
        } else if (temperature >= 20 && temperature <= 24) {
            weatherType = WEATHER_WARM;
        } else if (temperature < 20 && temperature >= 14) {
            weatherType = WEATHER_COOL;
        } else if (temperature < 14) {
            weatherType = WEATHER_COLD;
        } else {
            weatherType = WEATHER_HOT;
        }

        return weatherType;

    }
}
