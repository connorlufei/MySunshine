package com.iotek.mysunshine;

/**
 * Created by Administrator on 2016/9/26.
 * 实体类
 */

public class WeatherInfo {
    private String maxTemp;
    private String minTemp;
    private String weather;
    private String cityName;
    private String humidity;
    private String pressure;
    private String wind;
    private String weather_id;

    public WeatherInfo(String weather_id, String weather, String maxTemp, String minTemp, String humidity,String pressure,String wind ) {
        this.maxTemp = maxTemp;
        this.weather_id = weather_id;
        this.wind = wind;
        this.pressure = pressure;
        this.humidity = humidity;
        this.weather = weather;
        this.minTemp = minTemp;
    }

    public String getPressure() {

        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public WeatherInfo() {
    }



    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }
}
