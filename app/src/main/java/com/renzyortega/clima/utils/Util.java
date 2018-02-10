package com.renzyortega.clima.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.renzyortega.clima.activities.MainActivity;
import com.renzyortega.clima.R;

public class Util {
	
	public static JSONObject getObject(JSONObject obj, String key) throws JSONException {
        return(obj.optJSONObject(key));
    }

    public static String getString(JSONObject obj, String key) throws JSONException {
        return(obj.optString(key));
    }

    public static float getFloat(JSONObject obj, String key) throws JSONException {
        return((float)obj.optDouble(key));
    }

    public static int getInt(JSONObject obj, String key) throws JSONException {
        return(obj.optInt(key));
    }

    public static long getLong(JSONObject obj, String key) throws JSONException {
        return(obj.optLong(key));
	}
	
	public static String getWeatherIcon(MainActivity activity, int actualId, int hourOfDay) {
		int id = actualId / 100;
		String icon = "";
		if(actualId == 800) {
			if (hourOfDay >= 7 && hourOfDay < 20) {
				icon = activity.getString(R.string.weather_sunny);
			}
			else {
				icon = activity.getString(R.string.weather_clear_night);
			}
		}
		else {
			switch (id) {
				case 2:
					icon = activity.getString(R.string.weather_thunder);
					break;
				case 3:
					icon = activity.getString(R.string.weather_drizzle);
					break;
				case 7:
					icon = activity.getString(R.string.weather_foggy);
					break;
				case 8:
					icon = activity.getString(R.string.weather_cloudy);
					break;
				case 6:
					icon = activity.getString(R.string.weather_snowy);
					break;
				case 5:
					icon = activity.getString(R.string.weather_rainy);
					break;
			}
		}
		return icon;
	}
}