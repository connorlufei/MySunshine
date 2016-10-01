package com.iotek.mysunshine;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 创建一个异步任务，用于执行从服务器返回天气数据的任务
 */

public class FetchWeatherTask extends AsyncTask<String,Void,String[][]>{
    private FetchWeatherCallBacks callBacks;
    public void setCallBacks(FetchWeatherCallBacks callBacks){
        this.callBacks=callBacks;
    }

    public interface FetchWeatherCallBacks{
        void onFetchWeatherFinished(String [][] weatherInfo);
    }

    @Override
    protected String[][] doInBackground(String... params) {

        if(Utility.isNetworkConnected(Utility.context)){
            Log.i("TEST","network");
            Log.i("LOCA","loca");
            String forecastJsonStr = Utility.requestJsonFromWeb(params[0]);
            //保存该原始json字符串供DetailActivity解析使用
            Utility.rawJson = forecastJsonStr;
            Utility.saveJsonStringToFile(Utility.JSON_FILE);
            try {
                return Utility.getWeatherDataFromJson(forecastJsonStr, 7);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }else{
            Log.i("TEST","offline");
            String rawJson = Utility.getJsonStringFromSavedFile(Utility.JSON_FILE);
            try {
                return Utility.getWeatherDataFromJson(rawJson,7);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    protected void onPostExecute(String[][] result) {
        if (result != null) {
            callBacks.onFetchWeatherFinished(result);
        }
    }
}
