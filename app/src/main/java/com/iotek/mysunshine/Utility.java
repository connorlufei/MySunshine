package com.iotek.mysunshine;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/9/26.
 * 工具类大集合，包括从本地文件和网络取回Json天气数据，解析以及基于天气状况选择图片资源
 */

public class Utility {
    public static final String APPID = "d1c425412d5884890506708cf21aeba9";
    public static WeatherInfo[] mWeatherInfos;
    public static String rawJson;
    public static String JSON_FILE = "last_raw_json";
    public static Context context;

    public static int getIconResourceForWeatherCondition(int weatherId) {
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    public static int getArtResourceForWeatherCondition(int weatherId) {
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }

    public static String getDateForPosition(int position) {
        Date date = new Date();
        switch (position) {
            case 0:
                return "今天, " + formatDate(date, 0);
            case 1:
                return "明天";
            case 2:
                return "后天";
            default:
                return formatDate(date, position);
        }
    }

    private static String formatDate(Date date, int position) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);   //设置当前日期
        c.add(Calendar.DATE, position); //日期加position天
        date = c.getTime();
        return new SimpleDateFormat("M月dd日").format(date);
    }

    public static String requestJsonFromWeb(String city) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String format = "json";
        String units = "metric";
        String lang = "zh";
        int numDays = 7;

        try {
            final String FORECAST_BASE_URL =
                    "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String APPID_PARAM = "APPID";
            final String LANGUAGE = "lang";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, city)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .appendQueryParameter(APPID_PARAM, Utility.APPID)
                    .appendQueryParameter(LANGUAGE, lang)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.i("Test", url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            if (buffer.length() == 0) {
                return null;
            }
            Log.i("TEST", buffer.toString());
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static WeatherInfo[] getWeatherInfosFromJson(String forecastJsonStr) throws JSONException {
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";//最高温度
        final String OWM_MIN = "min";//最低温度
        final String OWM_DESCRIPTION = "description";//天气状况
        final String OWM_WEATHER_ID = "id";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_PRESSURE = "pressure";
        final String OWM_WIND = "speed";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
        WeatherInfo[] weatherInfos = new WeatherInfo[7];

        for (int i = 0; i < 7; i++) {

            JSONObject dayForecast = weatherArray.getJSONObject(i);
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            String description = weatherObject.getString(OWM_DESCRIPTION);
            int weatherId = weatherObject.getInt(OWM_WEATHER_ID);
            double humidity = dayForecast.getDouble(OWM_HUMIDITY);
            double pressure = dayForecast.getDouble(OWM_PRESSURE);
            double wind = dayForecast.getDouble(OWM_WIND);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            String max = formatTemp(temperatureObject.getDouble(OWM_MAX));
            String min = formatTemp(temperatureObject.getDouble(OWM_MIN));

            WeatherInfo weatherInfo = new WeatherInfo(weatherId + "", description, max, min, humidity + "", pressure + "", wind + "");
            weatherInfos[i] = weatherInfo;
        }
        return weatherInfos;
    }

    private static String formatTemp(double temp) {
        return Math.round(temp) + "";
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 传入要解析的json字符串和要解析的天数，拿到需要的天气数据存放在数组里面
     */
    public static String[][] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // 从OWM服务器返回的Json字符串中对象的名字
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";//最高温度
        final String OWM_MIN = "min";//最低温度
        final String OWM_DESCRIPTION = "description";//天气状况
        final String OWM_WEATHER_ID = "id";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
        String[][] weatherStrs = new String[numDays][4];

        for (int i = 0; i < numDays; i++) {
            JSONObject dayForecast = weatherArray.getJSONObject(i);
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            String description = weatherObject.getString(OWM_DESCRIPTION);
            int weatherId = weatherObject.getInt(OWM_WEATHER_ID);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            String max = formatTemp(temperatureObject.getDouble(OWM_MAX));
            String min = formatTemp(temperatureObject.getDouble(OWM_MIN));

            weatherStrs[i][0] = weatherId + "";
            weatherStrs[i][1] = description;
            weatherStrs[i][2] = max;
            weatherStrs[i][3] = min;
        }
        return weatherStrs;

    }

    public static String getJsonStringFromSavedFile(String fileName) {
        FileInputStream fis = null;
        StringBuffer sb = new StringBuffer();
        try {
            fis = context.openFileInput(fileName);
            byte[] bytes = new byte[1024];
            int b = 0;
            while ((b = fis.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, b));
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;

    }

    public static void saveJsonStringToFile(String fileName) {
        //将该原始json字符串存到文件中保存，覆盖原内容，以便在无网络连接时使用
        FileOutputStream fos = null;
        try {
            fos = Utility.context.openFileOutput(Utility.JSON_FILE, Context.MODE_PRIVATE);
            fos.write(Utility.rawJson.getBytes());
            Log.i("TEST", "success");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TEST", "save to file error");
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void getLocation() {
        Log.i("LOCA","entry");
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Log.i("LOCA","entry");
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                Log.i("LOCA",location.getLongitude()+"");
                Log.i("LOCA",location.getLatitude()+"");

             //   makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


        try{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }catch (SecurityException s){
            Log.i("LOCA","error");
        }
    }
}
