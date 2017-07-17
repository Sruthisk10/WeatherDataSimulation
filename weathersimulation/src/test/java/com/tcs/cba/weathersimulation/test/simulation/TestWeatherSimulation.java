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

package com.tcs.cba.weathersimulation.test.simulation;

import java.util.ArrayList;
import java.util.List;

import com.tcs.cba.weathersimulation.datamodel.WeatherParameters;
import com.tcs.cba.weathersimulation.process.impl.OscillationModel;
import org.junit.Before;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;


public class TestWeatherSimulation {


    public List<WeatherParameters> oldWeatherDataList = null;
    public String resultWeatherInfo = null;


    @Before
    public void intializeValues() throws Exception {
        oldWeatherDataList = new ArrayList<WeatherParameters>();
        resultWeatherInfo = "RAINY|23.0|1082.0|85";
        initializeListOfHistory();

    }

    /**
     * Intializes dummy value to be placed as history list.
     */
    private void initializeListOfHistory() {

        WeatherParameters wpBean1 = new WeatherParameters();

        wpBean1.setCoordinates("-33.86,151.21");
        wpBean1.setDayOfYear(12);
        wpBean1.setLocationCode("SYD");
        wpBean1.setHumidity(72d);
        wpBean1.setPressure(1100d);
        wpBean1.setTemperature(26d);
        wpBean1.setWeatherType("CLOUDY");
        wpBean1.setYear(2010);

        WeatherParameters wpBean2 = new WeatherParameters();

        wpBean2.setCoordinates("-33.86,151.21");
        wpBean2.setDayOfYear(12);
        wpBean2.setLocationCode("SYD");
        wpBean2.setHumidity(65d);
        wpBean2.setPressure(1070d);
        wpBean2.setTemperature(22d);
        wpBean2.setWeatherType("WARM");
        wpBean2.setYear(2013);

        WeatherParameters wpBean3 = new WeatherParameters();

        wpBean3.setCoordinates("-33.86,151.21");
        wpBean3.setDayOfYear(12);
        wpBean3.setLocationCode("SYD");
        wpBean3.setHumidity(85d);
        wpBean3.setPressure(1082d);
        wpBean3.setTemperature(23d);
        wpBean3.setWeatherType("RAINY");
        wpBean3.setYear(2015);

        oldWeatherDataList.add(wpBean1);
        oldWeatherDataList.add(wpBean2);
        oldWeatherDataList.add(wpBean3);

    }

    @Test
    public void foreCastWeatherSuccess() {

        OscillationModel vImpl = new OscillationModel();
        String weatherInfo = vImpl.getLocationWeatherInfo(oldWeatherDataList);
        assertEquals(resultWeatherInfo, weatherInfo);
    }

    @Test
    public void testMovingAverage(){
        OscillationModel oscillationObj = new OscillationModel();
        List <Double> tempList = new ArrayList<Double>();
        for(WeatherParameters factorList : oldWeatherDataList){
            tempList.add(factorList.getTemperature());
        }
        assertEquals(22.5, oscillationObj.getMovingAverage(2,1,tempList));
    }

    @Test
    public void testNegativeRateOfChange(){
        OscillationModel oscillationObj = new OscillationModel();
        assertEquals(-0.11059907834101401, oscillationObj.getRateOfChange(24.1,21.7));
    }

    @Test
    public void testPositiveRateOfChange(){
        OscillationModel oscillationObj = new OscillationModel();
        assertEquals(0.09958506224066399, oscillationObj.getRateOfChange(21.7,24.1));
    }
}
