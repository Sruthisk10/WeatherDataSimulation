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
import com.tcs.cba.weathersimulation.exception.WeatherSimulationException;
import com.tcs.cba.weathersimulation.jaxb.WeatherStation;
import com.tcs.cba.weathersimulation.process.simulationmodel.WeatherSimulationModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.tcs.cba.weathersimulation.utils.Constants.*;


/**
 * PredictionThread is a thread class and is invoked for each worker thread.
 * @author Sruthi Sasikumar
 *
 */
public class PredictionThread implements Runnable {

    private String oldWeatherData;
    private String statCode;
    private String location;
    private String altitude;
    private String mode;
    private String startDate;
    private String endDate;

    private Calendar cal;
    private SimpleDateFormat sdf;

    private Map<Integer, List<WeatherParameters>> oldWeatherDataMap;

    public PredictionThread(WeatherStation weatherStation, String mode, String startDate, String endDate) {
        this.statCode = weatherStation.getStatCode();
        this.location = weatherStation.getLocation();
        this.oldWeatherData = weatherStation.getWeatherData().getLoc();
        this.altitude = String.valueOf(weatherStation.getAltitude().intValue());
        this.mode = mode;
        this.startDate = startDate;
        this.endDate = endDate;

        cal = new GregorianCalendar();
        sdf = new SimpleDateFormat();
        sdf.setLenient(false);

    }

    @Override
    public void run() {
        try {
            generateWeatherReport();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    /**
     * Method to generate the weather report based on the mode selected
     * 
     * @throws IOException
     * @throws ParseException
     * @throws WeatherSimulationException
     * @throws InterruptedException
     */
    private void generateWeatherReport() throws IOException, ParseException, WeatherSimulationException,
            InterruptedException {
        oldWeatherDataMap = new TreeMap<Integer, List<WeatherParameters>>();
        loadOldWeatherData();
        WeatherSimulationModel simulationObj = new OscillationModel(oldWeatherDataMap);
        if (FORECAST_ALL_FUTURE_DATES.equalsIgnoreCase(mode)) {
            predictWeatherForFutureDates(simulationObj);
        } else if (FORECAST_DATE_RANGE.equalsIgnoreCase(mode)) {
            predictWeatherForADateRange(simulationObj);
        } else if (FORECAST_DATE.equalsIgnoreCase(mode)) {
            int day = findDay(startDate);
            int year = getYear(getFormattedDate(startDate));
            printReport(simulationObj.predictWeatherForADay(day, year), getFormattedDate(startDate));
        } else {
            throw new WeatherSimulationException("please use the correct mode [-d,-r,-a]");
        }
    }

    /**
     * This method is used to obtain the weather factors for a given date range. (-r option)
     * @throws ParseException
     * @throws InterruptedException
     */
    private void predictWeatherForADateRange(WeatherSimulationModel simulationObj) throws ParseException, InterruptedException {
        sdf.applyPattern(INPUT_DATE_PATTERN);
        Date beginDate = sdf.parse(startDate);
        Date stopDate = sdf.parse(endDate);
        Date tempDate = beginDate;
        Calendar c = Calendar.getInstance();
        c.setTime(beginDate);

        while (true) {
            int theDay = getDayOfYear(tempDate);
            int year = getYear(tempDate);
            printReport(simulationObj.predictWeatherForADay(theDay, year), tempDate);
            c.add(Calendar.DATE, 1);
            tempDate = c.getTime();
            if (stopDate.before(tempDate)) {
                break;
            }
        }
    }

    /**
     * This method is used to generate infinitely the weather factors for all future dates. (-a option)
     * @throws ParseException
     * @throws InterruptedException
     */
    private void predictWeatherForFutureDates(WeatherSimulationModel simulationObj) throws ParseException, InterruptedException {
        Date todaysDate = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(todaysDate);
        while (true) {
            int today = getDayOfYear(todaysDate);
            int year = getYear(todaysDate);
            printReport(simulationObj.predictWeatherForADay(today, year), todaysDate);
            c.add(Calendar.DATE, 1);
            todaysDate = c.getTime();
        }
    }

    /**
     * <p>
     * Method used to print the report. It uses the standard output to print the
     * result.
     * </p>
     *
     * @param predictedWeather
     *            -weather prediction factors weather
     *            type|temperature|pressure|relative humidity.
     * @param date
     * @throws InterruptedException
     */
    private void printReport(String predictedWeather, Date date) throws InterruptedException {
        Thread.sleep(60);
        sdf.applyPattern(DATE_TIME_PATTERN);
        String reportStatement = statCode + PIPE_DELIMITER + location + COMMA + altitude + PIPE_DELIMITER
                + sdf.format(date) + PIPE_DELIMITER + predictedWeather;

        System.out.println(reportStatement);

    }

    /**
     * <p>
     * Utility method finds the day of the year for a particular date
     * </p>
     *
     * @param dateVal
     *            - the date in String format.
     * @return day of the year in int format.
     * @throws ParseException
     */
    private int findDay(String dateVal) throws ParseException {
        Date dateObj = getFormattedDate(dateVal);
        int dayOfYear = getDayOfYear(dateObj);
        return dayOfYear;

    }

    /**
     * <p>
     * Method helps load old weather date from a directory specific to a location.
     * </p>
     *
     * @throws IOException
     * @throws ParseException
     */
    private void loadOldWeatherData() throws IOException, ParseException {
        File weatherDataLoc = new File(oldWeatherData);
        File[] weatherDataFiles = null;
        if (weatherDataLoc.exists() && weatherDataLoc.isDirectory()) {
            weatherDataFiles = weatherDataLoc.listFiles();
            for (int i = 0; i < weatherDataFiles.length; i++) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(weatherDataFiles[i]));
                loadWeatherData(bufferedReader);
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        }
    }

    /**
     * <p>
     * Method helps load history data from each file read into the
     * bufferedReader
     * </p>
     *
     * @param bufferedReader
     *            - holds the data read for a file in a directory particular to
     *            a location for a specific year.
     * @throws IOException
     * @throws ParseException
     */
    private void loadWeatherData(final BufferedReader bufferedReader) throws IOException, ParseException {

        String weatherInfo = null;
        while (bufferedReader.ready()) {
            weatherInfo = bufferedReader.readLine();
            String[] lineSplit = weatherInfo.split(COMMA, -1);
            WeatherParameters wpBean = setWeatherDetails(lineSplit);
            addWeatherDetails(wpBean);
        }
    }

    /**
     * <p>
     * Method adds the bean objects to a Map having a tuple each for a specific
     * day of a year for eg. 'n'th day of the year would be present with a key
     * 'n' where n is an Integer
     * </p>
     *
     * @param wdBean
     */
    private void addWeatherDetails(WeatherParameters wpBean) {
        List<WeatherParameters> wpBeanList = null;
        int dayOfYear = wpBean.getDayOfYear();
        if (oldWeatherDataMap.containsKey(dayOfYear)) {
            wpBeanList = oldWeatherDataMap.get(dayOfYear);
        } else {
            wpBeanList = new ArrayList<WeatherParameters>();
        }
        wpBeanList.add(wpBean);
        oldWeatherDataMap.put(dayOfYear, wpBeanList);
    }

    /**
     * <p>
     * Method helps to populate attributes of the bean object.
     * </p>
     *
     * @param lineSplit
     *            - split data for each line split on delimiter comma(',').
     * @return- bean to be added to the list.
     * @throws ParseException
     */
    private WeatherParameters setWeatherDetails(String[] lineSplit) throws ParseException {
        WeatherParameters wpBean = new WeatherParameters();

        Date formattedDate = getFormattedDate(lineSplit[0]);

        wpBean.setDayOfYear(getDayOfYear(formattedDate));
        wpBean.setLocationCode(this.statCode);
        wpBean.setCoordinates(this.location);
        wpBean.setTemperature(Double.parseDouble(lineSplit[1]));
        wpBean.setHumidity(Double.parseDouble(lineSplit[2]));
        wpBean.setPressure(Double.parseDouble(lineSplit[3]));
        wpBean.setWeatherType(lineSplit[4].trim());
        wpBean.setYear(getYear(formattedDate));

        return wpBean;
    }

    /**
     * <p>
     * Returns a Date object which is the formatted date from the string input.
     * </p>
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public Date getFormattedDate(String date) throws ParseException {
        sdf.applyPattern(INPUT_DATE_PATTERN);
        Date dateInstance = sdf.parse(date);
        return dateInstance;

    }

    /**
     * <p>
     * Method returns the count of the day of the year for a particular date
     * </p>
     *
     * @param dateInstance
     * @return
     * @throws ParseException
     */
    public int getDayOfYear(Date dateInstance) throws ParseException {
        cal.setTime(dateInstance);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * <p>
     * Method formats the date to extract the year only.
     * </p>
     *
     * @param dateInstance
     * @return
     */
    public int getYear(Date dateInstance) {
        sdf.applyPattern(YEAR_PATTERN);
        return Integer.parseInt(sdf.format(dateInstance));
    }

}
