## Weather Data Simulator
This project is a toy model solution to the problem of predicting weather for future dates. The idea behind this solution
 is to provide a reliable model enriched by the historical data available for analytics.

### Pre-Requisites
##### Softwares and plugin Installation:
###### Java Installation
To run the application java 1.7 or version above needs to be installed in the machine.

###### Maven Installation
* Download Apache Maven and install it.
* Unzip apache-maven-3.3.9-bin.zip
* Add the bin directory of the created directory apache-maven-3.3.9 to the PATH environment variable.

##### Project setup

- The history data needs to be downloaded to provide input ([link to data](https://www.wunderground.com/)) or you can find the same in the following path within the project "weathersimulation/src/main/resources/input".
- Update the configuration and properties files. Instructions below.
- Ensure that the following jar is built and used for execution:

> weathersimulation.jar

After importing the project and building using maven, a versioned jar with a similar name would be found in the target folder which does not have the Main Class preset.

### Execution Procedure
##### Modes

This module works in 3 modes:


* all => Forecasting for all future dates including today.
* date => Forecasting for a specific date queried for.
* range => Forecasting for a range of dates between queried start and end dates.


##### Command to Run
```ruby
java -jar weathersimulation.jar 
	-m <mode>
	-c <properties_file_path>
	[-s <start_date>] 
	[-e <end_date>]
```
Eg. below:
```ruby
java -jar weathersimulation.jar 
	-m range
	-c ~/Desktop/test/config/weatherinfo.properties
	-s 2017-7-21
	-e 2017-7-25
```
> 1. mode - Mode parameter has values in [all,date,range]
> 2. properties_file_path- absolute path of properties file
> 3. start_date -
>   * for mode date => specific date to be forecasted
>   * for mode range => starting date of date range
>
> 4. end_date -
>   * for mode range => end date of forecast

**Note:** The date format is yyyy-M-d, for eg. 2016-11-5

##### Main class
```ruby
com.tcs.cba.weathersimulation.process.impl.WeatherDataSimulator
```
##### Dataset Considered

Historical weather data between 2005 and 2015 (both years inclusive) for 4 locations listed below:


Sydney
Melbourne
Adelaide
Brisbane


_source_: [link to data](https://www.wunderground.com/)


### Approach Note

#### Assumptions:
 1. The weather on a day of the year bears resemblance to the trend of change in weather of the same day in earlier years.
 2. Prediction over predicted data might arguably reduce the accuracy of prediction. Also since we are using the system memory (heap) instead of a database to hold the historical data, the model might fail at a specific time when the data generation crosses the system limits.

#### Configuration:

The configuration file "weatherstationsinfo.xml" holds the static information regarding various geographic locations (weather stations) which are subject to weather forecasting. The configuration file reads as below:

PATH: weathersimulation/src/main/resources/config/weatherstationsinfo.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<weatherStations>
	<weatherStation statName="Sydney" statCode="SYD" countryCode="AU" location="-33.86,151.21" altitude="39">
		<weatherData loc="C:\Users\lenovo\Desktop\sruthi\EclipseWorkspace\weathersimulation\src\main\resources\input\Sydney" />
	</weatherStation>
	<weatherStation statName="Melbourne" statCode="MEL" countryCode="AU" location="-37.83,144.98" altitude="7">
		<weatherData loc="C:\Users\lenovo\Desktop\sruthi\EclipseWorkspace\weathersimulation\src\main\resources\input\Melbourne" />
	</weatherStation>
	<weatherStation statName="Brisbane" statCode="BRS" countryCode="AU" location="-27.47,153.02" altitude="27">
		<weatherData loc="C:\Users\lenovo\Desktop\sruthi\EclipseWorkspace\weathersimulation\src\main\resources\input\Brisbane" />
	</weatherStation>
	<weatherStation statName="Adelaide" statCode="ADL" countryCode="AU" location="-34.92,138.62" altitude="48">
		<weatherData loc="C:\Users\lenovo\Desktop\sruthi\EclipseWorkspace\weathersimulation\src\main\resources\input\Adelaide" />
	</weatherStation>
</weatherStations>
```
The above xml adheres to a schema document "weatherstationsinfo.xsd" as follows:

PATH: weathersimulation/src/main/resources/config/weatherstationsinfo.xsd
```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified">
  <xs:element name="weatherStations">
    <xs:complexType>
      <xs:sequence>
        <xs:element maxOccurs="unbounded" ref="weatherStation"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="weatherStation">
    <xs:complexType>
      <xs:sequence>
        <xs:element ref="weatherData"/>
      </xs:sequence>
      <xs:attribute name="altitude" use="required" type="xs:integer"/>
      <xs:attribute name="statName" use="required" type="xs:NCName"/>
      <xs:attribute name="location" use="required"/>
      <xs:attribute name="countryCode" use="required" type="xs:NCName"/>
      <xs:attribute name="statCode" use="required" type="xs:NCName"/>
    </xs:complexType>
  </xs:element>
  <xs:element name="weatherData">
    <xs:complexType>
      <xs:attribute name="loc" use="required"/>
    </xs:complexType>
  </xs:element>
</xs:schema>
```
#### Properties:
The following properties help execute the module:

PATH: weathersimulation/src/main/resources/config/weatherinfo.properties
```
CONFIG_LOCATION=weathersimulation/src/main/resources/config/weatherstationsinfo.xml
```
The CONFIG_LOCATION is the location of "weatherstationsinfo.xml" which holds the static details of the weather stations.

#### Functional Description:
When a query is made to forecast the weather for any day of the year, the historical data available for that day from
previous years is fetched as reference data. This data is then shuffled to begin with the oscillation model.

[Note: For leap years, the 366th and 365th days are both considered, as historical data for 2 leap years is only available]

Oscillation model finds the moving averages from the above shuffled data within two randomly selected windows for each
weather factor (temperature, pressure, humidity).
```
Moving Average1 (MA1) = ∑(Xn)/N1
Moving Average2 (MA2) = ∑(Xm)/N2
```
; where N1 is the size of window 1, N2 is the size of window 2.

; where n ranges from offset value 'f' to sum of offset and window 1 size (f + N1).
Similarly m ranges from offset value 'f' to sum of offset and window 2 size (f + N2).

Here,
- N2 > N1
- Both window1 and window2 start from the same offset 'f'

The oscillation is then calculated using the two moving averages.
```
Oscillation (OSC) = |MA1 - MA2|
```
;where the absolute value is taken.

The rate of change is calculated by considering the first window (Window 1) of the moving average calculations.
The last entry of the window 'Xe', along with another entry selected randomly 'Xa' from the initial shuffled list of
weather parameters is used.
```
Rate of Change(ROC) = 1-(Xe/Xa);
```
;where Xe is the final entry in the first window selected.
	 Xa can be any random entry from the shuffled list of weather factor.
Here,
- f < a < N1

The rate of change(ROC) obtained can be positive or negative, indicating a rise or drop in the weather factors value.
It can be considered as a means of identifying, whether there is a rise or fall in the weather factor over the years.
The oscillation can give the amount of the change that has been observed over the years.

The weather prediction is always done based on the historical data available for edge case of the initial list.
The final prediction is done by evaluating the effect of the rate of change and oscillation values on the previous
recorded data for the said date.
```ruby
WeatherFactor(Wf) = We + (OSC * ROC)
```
; Here
* We => Weather factor at the edge case.
* OSC => Oscillation factor
* ROC => Rate of change factor

Finally, based on the values forecasted for the weather factors, the weather type is categorized as Cloudy, Rainy, Cool,
 Cold, Warm and Hot.

#### Technical Description:
##### Classes of Significance
 1. _WeatherDataSimulator.java_ - This class is the starting point of the application. It loads the configuration file
 using JAXB and spawns a thread for each work station.
 2. _PredictionThread.java_ - This is a thread class that runs for each workstation configured in the configuration file.
 3. _OscillationModel.java_ - This class does the core functional implementation. It does the processing to predict the
 weather factors for any date.
 4. _Util.java_ - The reusable functions and logics are embedded in this class.
 5. _WeatherParameters.java_ - This is the model (DTO) class carrying the weather information for any day of any year.

##### Process Flow
The process flow starts with the **WeatherDataSimulator.java**. This class ensures that the directory locations mentioned in
 the attribute "**loc**" of element "**weatherData**" in xml has the right set of per-year old weather data in files.
 The configuration file is then loaded into the **JAXB class** instance.
The process then makes use of a multi-threaded system where a single thread handles all the data loading, processing and
 predictions for a single location.

The **PredictionThread.java** is the thread class for the threads implementing the Runnable interface. The multithreading is
 implemented using **ExecutorService**, as ExecutorService abstracts away many of the complexities associated with the
 lower-level abstractions like raw Thread. It provides mechanisms for safely starting, closing down, submitting,
 executing, and blocking on the successful or abrupt termination of tasks (expressed as Runnable or Callable).
 It also gives a flexible thread pooling.

The core logic of the application is to calculate the weather factor values for a single day only and this lies in the
OscillationModel.java. The 3 modes of working mentioned in the "Execution Procedure" are appropriately handled by the
thread by calling the same method, namely **predictWeatherForADay()**. The **OscillationModel.java** also contains the mathematical
 logic of the application. **Util.java** is called in, to handle certain common functions.

The final weather factors predicted are collected and classified based on the 6 weather types mentioned earlier.
Finally the result emitted by the thread of string format to the standard output media(console by default) is of format:

```
SYD|-33.86,151.21,39|2017-08-17T12:47:37Z|COLD|12.0|1020.0|46
```
Here, the output can be read in the following format:

```
<statCode>|latitude,longitude,altitude|date_time|weatherType|temperature|pressure|relative_humidity
```

## Challenges
1. The accuracy of the model is restricted to the minimal historical data.
2. Logically, the recent weather factors affect the future weather factors as compared to the very early years.
This is not currently handled in the model.
3. The current model does not allow considering predicted weather factor values for future predictions. This is
done purposefully to avoid biasing of the predicted data.



## References
#### TestCases:
JUnit test cases are provided in the path
```
"weathersimulation/src/test/java"
```
which would run on build using Maven too.
#### Documentation:
Please find the documentation in the following paths:
```
"weathersimulation/documents/javadoc" => Auto-generated javadoc
```
#### Samples:
The sample input and output could be located  in the following paths:
```
weathersimulation/src/test/resources/sampleinput
```
```
weathersimulation/src/test/resources/sampleoutput
```
