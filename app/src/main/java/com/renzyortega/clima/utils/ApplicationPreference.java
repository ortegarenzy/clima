package com.renzyortega.clima.utils;

import android.content.Context;
import android.content.SharedPreferences;

import android.location.Address;
import android.location.Geocoder;

import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.renzyortega.clima.models.*;

public class ApplicationPreference {

	private static String TAG = "ApplicationPreference";

	public static void saveCurrentWeather(Context context, CurrentWeather currentWeather) {
		Weather weather = currentWeather.weather;
		if(weather == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(Constants.APP_CURRENT_WEATHER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(Constants.WEATHER_DATA_ID, weather.getWeatherId());
		editor.putFloat(Constants.WEATHER_DATA_TEMPERATURE, weather.temperature.getTemp());
		editor.putString(Constants.WEATHER_DATA_DESCRIPTION, weather.getDescription());
		editor.putString(Constants.WEATHER_DATA_ICON, weather.getIcon());
		editor.putString(Constants.APP_SETTINGS_CITY, weather.location.getCity());
		editor.putString(Constants.APP_SETTINGS_COUNTRY_CODE, weather.location.getCountry());
		editor.putFloat(Constants.WEATHER_DATA_SUNRISE, weather.sys.getSunrise());
		editor.putFloat(Constants.WEATHER_DATA_SUNSET, weather.sys.getSunset());
		editor.apply();
	}

	public static CurrentWeather getCurrentWeather(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Constants.APP_CURRENT_WEATHER, Context.MODE_PRIVATE);
		Sys sys = new Sys();
		sys.setSunrise(preferences.getFloat(Constants.WEATHER_DATA_SUNRISE, 0));
		sys.setSunset(preferences.getFloat(Constants.WEATHER_DATA_SUNSET, 0));
		Location location = new Location();
		location.setCity(preferences.getString(Constants.APP_SETTINGS_CITY, ""));
		location.setCountry(preferences.getString(Constants.APP_SETTINGS_COUNTRY_CODE, ""));
		Temperature temp = new Temperature();
		temp.setTemp(preferences.getFloat(Constants.WEATHER_DATA_TEMPERATURE, 0));
		Weather weather = new Weather();
		weather.temperature = temp;
		weather.location = location;
		weather.sys = sys;
		weather.setWeatherId(preferences.getInt(Constants.WEATHER_DATA_ID, -1));
		weather.setDescription(preferences.getString(Constants.WEATHER_DATA_DESCRIPTION, ""));
		CurrentWeather cWeather = new CurrentWeather();
		cWeather.weather = weather;
		return cWeather;
	}

	public static void saveLocation(Context context, Double mlatitude, Double mlongitude) {
		SharedPreferences preferences = context.getSharedPreferences(Constants.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		String latitude = String.format("%1$.2f", mlatitude);
		String longitude = String.format("%1$.2f", mlongitude);
		editor.putString(Constants.APP_SETTINGS_LATITUDE, latitude);
		editor.putString(Constants.APP_SETTINGS_LONGITUDE, longitude);
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		try {
			String latitudeEn = latitude.replace(",", ".");
			String longitudeEn = longitude.replace(",", ".");
			List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitudeEn), Double.parseDouble(longitudeEn), 1);
			if((addresses != null) && (addresses.size() > 0)) {
				editor.putString(Constants.APP_SETTINGS_GEO_CITY, addresses.get(0).getLocality());
				editor.putString(Constants.APP_SETTINGS_GEO_COUNTRY_NAME, addresses.get(0).getCountryName());
			}
		} catch (IOException | NumberFormatException ex) {
			Log.e(TAG, "Unable to get address from latitude and longitude", ex);
		}
		editor.apply();
	}

	public static long saveLastUpdateTimeMillis(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Constants.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
		long now = System.currentTimeMillis();
		sp.edit().putLong(Constants.LAST_UPDATE_TIME_IN_MS, now).apply();
		return now;
	}

	public static long getLastUpdateTimeMillis(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Constants.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
		return sp.getLong(Constants.LAST_UPDATE_TIME_IN_MS, 0);
	}

	public static boolean locationChanged(Context context, Double latitude, Double longitude) {
		SharedPreferences sp = context.getSharedPreferences(Constants.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
		Double mLat = Double.parseDouble(sp.getString(Constants.APP_SETTINGS_LATITUDE, ""));
		Double mLon = Double.parseDouble(sp.getString(Constants.APP_SETTINGS_LONGITUDE, ""));
		Double lat = Double.parseDouble(String.format("%1$.2f", latitude));
		Double lon = Double.parseDouble(String.format("%1$.2f", longitude));
		Log.i(TAG, "mlat" + mLat.toString());
		Log.i(TAG, "mLon" + mLon.toString());
		Log.i(TAG, "latitude" + lat.toString());
		Log.i(TAG, "longitude" + lon.toString());
		if(Double.compare(mLat, lat) == 0 && Double.compare(mLon, lon) == 0) {
			Log.i(TAG, "return false");
			return false;
		}
		Log.i(TAG, "return true");
		return true;
	}
}