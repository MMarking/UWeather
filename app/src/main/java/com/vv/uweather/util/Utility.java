package com.vv.uweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.vv.uweather.db.UWeatherDB;
import com.vv.uweather.model.City;
import com.vv.uweather.model.County;
import com.vv.uweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 提供一个工具类来解析和处理服务器返回的省市县数据
 * 提供了 handleProvincesResponse() 、handleCitiesResponse() 、handleCountiesResponse()这三个方法，
 * 分别用于解析和处理服务器返回的省级、市级和县级数据。解析的规则就是先按逗号分隔，再按单竖线分隔，
 * 接着将解析出来的数据设置到实体类中，最后调用 CoolWeatherDB 中的三个 save()方法将数据存储到相应的表中
 */
public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     * @param uWeatherDB
     * @param response
     * @return
     */
    public synchronized static boolean handleProvincesResponse(UWeatherDB uWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    // 将解析出来的数据存储到Province类
                    uWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }


    /**
     * 解析和处理服务器返回的市级数据
     * @param uWeatherDB
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCitiesResponse(UWeatherDB uWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    // 将解析出来的数据存储到City表
                    uWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }


    /**
     * 解析和处理服务器返回的县级数据
     * @param uWeatherDB
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountiesResponse(UWeatherDB uWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    // 将解析出来的数据存储到County表
                    uWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
     * @param context
     * @param response
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器范湖的天气信息存储到 SharedPreferences 文件中
     * @param context
     * @param cityName
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weatherDesp
     * @param publishTime
     */
    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        // 除了天气 信息之外，还存储了一个 city_selected 标志位，以此来辨别当前是否已经选中了一个城市
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }

}
