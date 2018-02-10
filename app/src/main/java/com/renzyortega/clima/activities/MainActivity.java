package com.renzyortega.clima.activities;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;

import android.graphics.Typeface;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.view.View;

import android.widget.TextView;

import java.text.DecimalFormat;

import android.util.Log;
import java.util.Calendar;

import com.renzyortega.clima.R;
import com.renzyortega.clima.models.*;
import com.renzyortega.clima.tasks.*;
import com.renzyortega.clima.utils.PermissionUtil;
import com.renzyortega.clima.utils.Util;
import com.renzyortega.clima.utils.ApplicationPreference;

public class MainActivity extends BaseActivity {

	private final String TAG = "MainActivity";

	private static final int REQUEST_LOCATION = 0;
    private static String[] PERMISSIONS_LOCATION = {
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION
	};

	private static final int NO_UPDATE_REQUIRED_THRESHOLD = 300000;

	TextView todayIcon;
	TextView todayTemperature;
	TextView todayDescription;
	TextView city;
	TextView country;

	Typeface weatherFont;

	LocationManager locationManager;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		setContentView(R.layout.main_activity);
		Log.i(TAG, "onCreate");
		todayTemperature = (TextView) findViewById(R.id.todayTemperature);
		todayDescription = (TextView) findViewById(R.id.todayDescription);
		city = (TextView)findViewById(R.id.city);
		country = (TextView)findViewById(R.id.country);
		todayIcon = (TextView) findViewById(R.id.todayIcon);
		weatherFont = Typeface.createFromAsset(this.getAssets(), "fonts/weather.ttf");
		if(todayIcon != null) {
			todayIcon.setTypeface(weatherFont);
		}
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		requestLocation();
	}

	@Override
    public void onResume() {
		super.onResume();
		requestLocation();
	}

	@Override
    protected void onDestroy() {
        super.onDestroy();
	}

	private void requestLocation() {
		Log.i(TAG, "Requesting location...");
		int permission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
		if(permission != PackageManager.PERMISSION_GRANTED) {
			requestPermissions();
		}
		else {
			detectLocation();
		}
	}

	private void requestPermissions() {
		Log.i(TAG, "Requesting permissions...");
		if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
			Log.i(TAG, "shouldShowRequestPermissionRationale");
			Snackbar.make(findViewById(android.R.id.content), R.string.permission_info, Snackbar.LENGTH_LONG).setAction(android.R.string.ok, new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
				}
			}).show();
		}
		else {
			Log.i(TAG, "else");
			ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION:
				if(PermissionUtil.verifyPermissions(grantResults)) {
					Snackbar.make(findViewById(android.R.id.content), R.string.permission_granted, Snackbar.LENGTH_SHORT).show();
				}
				else {
					Snackbar.make(findViewById(android.R.id.content), R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();
				}
				break;
			default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

	private LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			Log.i(TAG, "Location changed...");
			if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(locationListener);
			}
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			Log.i("LOCATION (" + location.getProvider().toUpperCase() + ")", latitude + ", " + longitude);
			getTodayWeather(latitude, longitude);
		}

		@Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
	};

	private void detectLocation() {
		Log.i(TAG, "Detecting location...");
		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if(isNetworkEnabled) {
			requestNetworkLocation();
		}
		else {
			if(isGPSEnabled) {
				requestGPSLocation();
			}
		}
	}

	private void requestNetworkLocation() {
		Log.i(TAG, "Requesting network location..");
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	private void requestGPSLocation() {
		Log.i(TAG, "Requesting GPS location..");
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}

	private boolean shouldUpdate() {
        long lastUpdate = ApplicationPreference.getLastUpdateTimeMillis(this);
        return lastUpdate < 0 || (Calendar.getInstance().getTimeInMillis() - lastUpdate) > NO_UPDATE_REQUIRED_THRESHOLD;
	}

	private void getTodayWeather(Double latitude, Double longitude) {
		if(shouldUpdate()) {
			Log.i(TAG, "Should update is true");
		}
		if(ApplicationPreference.locationChanged(this, latitude, longitude)) {
			Log.i(TAG, "Locationchanged is true");
		}
		if(shouldUpdate() || ApplicationPreference.locationChanged(this, latitude, longitude)) {
			Log.i(TAG, "Requesting new data ........");
			TodaysWeatherTask task = new TodaysWeatherTask(this, this);
			if(latitude > 0 && longitude > 0) {
				task.execute("coords", Double.toString(latitude), Double.toString(longitude));
				ApplicationPreference.saveLocation(MainActivity.this, latitude, longitude);
			}
			else {
				task.execute();
			}
		}
		else {
			loadCachedData();
		}
	}

	private void loadCachedData() {
		CurrentWeather cWeather = ApplicationPreference.getCurrentWeather(this);
		if(cWeather == null) {
			Log.i(TAG, "Requesting new data ....");
			getTodayWeather(-1.0, -1.0);
		}
		else {
			Log.i(TAG, "Loading cached data ....");
			updateTodayWeather(cWeather);
		}
	}

	private void parseWeatherData(String result) {
		Log.i("Location", "parseWeatherData: " + result);
		WeatherResponseParser parser = new WeatherResponseParser(MainActivity.this);
		CurrentWeather cWeather = parser.getCurrentWeather(result);
		updateTodayWeather(cWeather);
	}

	private void updateTodayWeather(CurrentWeather cWeather) {
		if(cWeather == null) {
			return;
		}
		ApplicationPreference.saveCurrentWeather(this, cWeather);
		ApplicationPreference.saveLastUpdateTimeMillis(this);
		Weather weather = cWeather.weather;
		if(weather == null) {
			return;
		}
		com.renzyortega.clima.models.Location location = weather.location;
		if(location != null) {
			city.setText(location.getCity());
			country.setText(location.getCountry());
		}
		Temperature temp = weather.temperature;
		if(temp != null) {
			todayTemperature.setText(new DecimalFormat("0.#").format(temp.getTemp()) + " °C");
		}
		todayDescription.setText(weather.getDescription().toUpperCase());
		todayIcon.setText(Util.getWeatherIcon(this, weather.getWeatherId(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
	}
	
	class TodaysWeatherTask extends WeatherClientTask {

		public TodaysWeatherTask(Context context, MainActivity activity) {
            super(context, activity);
		}

		@Override
		protected void handleResponse(String result) {
			parseWeatherData(result);
		}
	}
}
