package com.vv.uweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vv.uweather.model.City;
import com.vv.uweather.model.County;
import com.vv.uweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * 把一些常用的数据库 操作封装起来，以方便我们后面使用
 * UWeatherDB 是一个单例类，
 * 构造方法私有化，并提供了一个 getInstance()方法来获取 UWeatherDB 的实例，
 * 保证全局范围内只会有一个 CoolWeatherDB 的实例
 */
public class UWeatherDB {

    public static final String DB_NAME = "u_weather";
    public static final int VERSION = 1;
    private static UWeatherDB uWeatherDB;
    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     * @param context
     */
    private UWeatherDB (Context context){
        UWeatherOpenHelper dbHelper = new UWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取uWeatherDB的实例
     * @param context
     * @return
     */
    public synchronized static UWeatherDB getInstance(Context context) {
        if(uWeatherDB == null){
            uWeatherDB = new UWeatherDB(context);
        }
        return uWeatherDB;
    }

    /**
     * 将Province实例存储到数据库
     * @param province
     */
    public void saveProvince(Province province) {
        if(province !=null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province",null, values);
        }
    }

    /**
     * 从数据库中读取全国所有的省份信息
     * @return
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return list;
    }

    /**
     * 将City实例存储到数据库
     * @param city
     */
    public void saveCity(City city) {
        if (city != null){
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    /**
     * 从数据库读取某省下所有的城市信息
     * @param provinceId
     * @return
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[] { String.valueOf(provinceId) }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 将County实例存储到数据库
     * @param county
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }

    /**
     * 从数据库读取某城市下所有的县信息
     * @param cityId
     * @return
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_id = ?", new String[] { String.valueOf(cityId) }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }




}
