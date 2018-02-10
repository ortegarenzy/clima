package com.renzyortega.clima.tasks;

import android.content.Context;

import com.renzyortega.clima.utils.Util;
import com.renzyortega.clima.models.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherResponseParser {

	private Context context;

	public WeatherResponseParser(Context ctx) {
		context = ctx;
	}

	public CurrentWeather getCurrentWeather(String result) {
		CurrentWeather currentWeather = new CurrentWeather();
		Weather weather = new Weather();
		try {
			JSONObject json = new JSONObject(result);
			JSONObject coordObject = Util.getObject(json, "coord");
			JSONObject sysObj = Util.getObject(json, "sys");
			Sys sys = new Sys();
			sys.setSunrise(Util.getFloat(sysObj, "sunrise"));
			sys.setSunset(Util.getFloat(sysObj, "sunset"));
			weather.sys = sys;
			Location loc = new Location();
			loc.setLatitude(Util.getFloat(coordObject, "lat"));
			loc.setLongitude(Util.getFloat(coordObject, "lon"));
			loc.setCountry(Util.getString(sysObj, "country"));
			loc.setCity(Util.getString(json, "name"));
			weather.location = loc;
			JSONObject mainObj = Util.getObject(json, "main");
			Temperature temperature = new Temperature();
            temperature.setMaxTemp(Util.getFloat(mainObj, "temp_max"));
            temperature.setMinTemp(Util.getFloat(mainObj, "temp_min"));
			temperature.setTemp(Util.getFloat(mainObj, "temp"));
			weather.temperature = temperature;
			JSONArray weatherArray = json.getJSONArray("weather");
			JSONObject cWeather = weatherArray.getJSONObject(0);
			weather.setWeatherId(Util.getInt(cWeather, "id"));
			weather.setDescription(Util.getString(cWeather, "description"));
            weather.setIcon(Util.getString(cWeather, "icon"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		currentWeather.weather = weather;
		return(currentWeather);
	}
}