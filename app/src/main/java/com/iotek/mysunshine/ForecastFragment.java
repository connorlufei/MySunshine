package com.iotek.mysunshine;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 显示天气的fragment。
 */
public class ForecastFragment extends Fragment implements FetchWeatherTask.FetchWeatherCallBacks{
    //显示天气的适配器
    private WeatherForecastAdapter mForecastAdapter;
    //当前天气数据
    private String[][] weatherStrs;
    private View rootView;
    private ListView listView;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置为有actionbar菜单
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //得到被点击菜单项的id
        int id = item.getItemId();
        //如果是刷新操作的话，则从服务器重新请求数据并显示在UI
        if (id == R.id.action_refresh) {
            refresh();
            return true;
        }
        if (id == R.id.action_settings) {
            final EditText et = new EditText(getActivity());

            new AlertDialog.Builder(getActivity()).setTitle("请输入要切换的城市")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input = et.getText().toString();
                            if (input.equals("")) {
                                Toast.makeText(getActivity(), "搜索内容不能为空！", Toast.LENGTH_LONG).show();
                            } else {
                                SharedPreferences.Editor editor =
                                        PreferenceManager.getDefaultSharedPreferences(ForecastFragment.this.getActivity()).edit();
                                editor.putString("city",input.trim()).apply();
                                refresh();
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.forecast_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv=(TextView)view.findViewById(R.id.list_item_date_textview);
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("date",tv.getText());
                startActivity(intent);
            }
        });

        String city = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("city", "Shanghai");
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.setCallBacks(this);
        weatherTask.execute(city);

        return rootView;
    }

    @Override
    public void onFetchWeatherFinished(String[][] weatherStrs) {
        mForecastAdapter=new WeatherForecastAdapter(getActivity(),weatherStrs);
        listView.setAdapter(mForecastAdapter);
        this.weatherStrs=weatherStrs;
    }
    public void refresh(){
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        String city=PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString("city","Shanghai");
        weatherTask.setCallBacks(this);
        weatherTask.execute(city);
    }

}
