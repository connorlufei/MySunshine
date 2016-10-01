package com.iotek.mysunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/27.
 * 用于显示天气的详细信息，包括湿度，气压，风速
 */

public class DetailActivity extends AppCompatActivity {
    private static final String HUMIDITY = "湿度：";
    private static final String PRESSURE = "气压：";
    private static final String WIND = "风速：";

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;
    private int mPosition;
    WeatherInfo mWeatherInfo;
    String mDate;
    private ShareActionProvider mShareActionProvider;
    String mForecast;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_activity,menu);
        MenuItem item=(MenuItem)menu.findItem(R.id.action_share);
        mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareIntent(createShareForecastIntent());
        return super.onCreateOptionsMenu(menu);
    }
    private Intent createShareForecastIntent() {
        mForecast = String.format("分享 ：%s 天气 %s - 温度 %s/%s #天气观察家", mDate,
                mWeatherInfo.getWeather(), mWeatherInfo.getMaxTemp(), mWeatherInfo.getMinTemp());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast);
        return shareIntent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        mIconView = (ImageView) findViewById(R.id.detail_icon);
        mDateView = (TextView) findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) findViewById(R.id.detail_pressure_textview);
        mPosition = getIntent().getIntExtra("position",0);
        mDate=getIntent().getStringExtra("date");
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            Utility.mWeatherInfos = Utility.getWeatherInfosFromJson(Utility.rawJson);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("TEST","ERROR");
        }

        mWeatherInfo = Utility.mWeatherInfos[mPosition];

        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(Integer.parseInt(mWeatherInfo.getWeather_id())));
        mFriendlyDateView.setText(mDate);
        mDescriptionView.setText(mWeatherInfo.getWeather());
        mHighTempView.setText(mWeatherInfo.getMaxTemp()+"°");
        mLowTempView.setText(mWeatherInfo.getMinTemp()+"°");
        mHumidityView.setText(HUMIDITY+mWeatherInfo.getHumidity()+"%");
        mWindView.setText(WIND+mWeatherInfo.getWind()+" mph");
        mPressureView.setText(PRESSURE+mWeatherInfo.getPressure()+" hPa");
    }
}
