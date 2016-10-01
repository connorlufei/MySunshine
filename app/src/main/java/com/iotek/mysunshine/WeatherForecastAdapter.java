package com.iotek.mysunshine;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/9/26.
 * 天气适配器
 */

public class WeatherForecastAdapter extends BaseAdapter {
    private Context context;
    String[][] weatherInfo;
    public WeatherForecastAdapter(Context context, String[][] weatherInfo){
        this.context=context;
        this.weatherInfo=weatherInfo;
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int layoutId = position==0 ? R.layout.list_item_today:R.layout.list_item_future;


        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        TextView descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        TextView highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
        TextView lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        TextView cityView = (TextView) view.findViewById(R.id.city_textview);
        String[] dayWeather=weatherInfo[position];

        dateView.setText(Utility.getDateForPosition(position));
        descriptionView.setText(dayWeather[1]);
        highTempView.setText(dayWeather[2]+"℃");


        if(position==0){
            iconView.setImageResource(Utility.getArtResourceForWeatherCondition(Integer.parseInt(dayWeather[0])));
            cityView.setText(PreferenceManager.getDefaultSharedPreferences(context).getString("city","shanghai"));
        }else{
            iconView.setImageResource(Utility.getIconResourceForWeatherCondition(Integer.parseInt(dayWeather[0])));
            lowTempView.setText(dayWeather[3]+"℃");
        }

        return view;
    }
}
