package com.example.mike.yahooweather;

/**
 * Created by Mike on 2/28/2017.
 */

public class Weather {
    String tempUnit;
    String temp;
    String skyCondition;
    String humidity;
    double longitude;
    double latitude;
    String location;

    public Weather()
    {
        tempUnit = null;
        temp = null;
        skyCondition = null;
        humidity = null;
        longitude = -255;
        latitude = -255;
        location = null;
    }

    public Weather(String tempUnit, String temp, String skyCondition, String humidity, double longitude, double latitude, String location)
    {
        this.tempUnit = tempUnit;
        this.temp = temp;
        this.skyCondition = skyCondition;
        this.humidity = humidity;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
    }

    public void setTempUnit(String tempUnit)
    {
        this.tempUnit = tempUnit;
    }

    public String getTempUnit()
    {
        return tempUnit;
    }

    public void setTemp(String temp)
    {
        this.temp = temp;
    }

    public String getTemp()
    {
        return temp;
    }

    public void setSkyCondition(String skyCondition)
    {
        this.skyCondition = skyCondition;
    }

    public String getSkyCondition()
    {
        return skyCondition;
    }

    public void setHumidity(String humidity)
    {
        this.humidity = humidity;
    }

    public String getHumidity()
    {
        return humidity;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLocation()
    {
        return location;
    }


}
