package com.renzyortega.clima.tasks;

import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;

import android.app.ProgressDialog;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.os.AsyncTask;

import com.renzyortega.clima.activities.MainActivity;
import com.renzyortega.clima.utils.Constants;
import com.renzyortega.clima.utils.ApplicationPreference;
import com.renzyortega.clima.R;

public abstract class WeatherClientTask extends AsyncTask<String, String, WeatherClientResponse> {

	private Context context;
	private MainActivity activity;
	private InputStream reader;
	private ProgressDialog progressDialog;
	private int loading = 0;

	public WeatherClientTask(Context mContext, MainActivity mActivity) {
		context = mContext;
		activity = mActivity;
		progressDialog = new ProgressDialog(mActivity);
	}

	@Override
	protected void onPreExecute() {
		incLoadingCounter();
		if(!progressDialog.isShowing()) {
			progressDialog.setMessage(context.getString(R.string.downloading_data));
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
		}
	}

	@Override
	protected WeatherClientResponse doInBackground(String... params) {
		String[] coordinates = new String[]{};
		if(params != null && params.length > 0) {
			coordinates = new String[] {params[1], params[2]};
		}
		URL url = null;
		try {
			url = getFullURL(coordinates);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		HttpURLConnection connection = getURLConnection(url);
		if(connection == null) {
			return(null);
		}
		try {
			connection.setRequestMethod("GET");	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		ApplicationPreference.saveLastUpdateTimeMillis(activity);
		return(processResponse(connection));
	}

	@Override
	protected void onPostExecute(WeatherClientResponse response) {
		if(loading == 1) {
			progressDialog.dismiss();
		}
		decLoadingCounter();
		String v = null;
		if(response == null) {
			handleResponse(v);
			return;
		}
		try {
			v = new String(response.getResponseBody(), "UTF-8");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		handleResponse(v);
	}

	private WeatherClientResponse processResponse(HttpURLConnection connection) {
		int responseCode = 0;
		try {
			responseCode = connection.getResponseCode();
			reader = connection.getInputStream();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(reader == null) {
			return(null);
		}
		byte[] buf = new byte[1024*32];
		byte[] responseBuffer = new byte[(1024 * 1024) * 2];
		int length = 0;
		while(true) {
			try {
				int r = reader.read(buf);
				if(r < 1) {
					break;
				}
				System.arraycopy(buf, 0, responseBuffer, length, r);
				length += r;
			}
			catch(Exception e) {
				e.printStackTrace();
				break;
			}
		}
		byte[] bb = new byte[length];
		int i = 0;
		while(true) {
			if(i > (length - 1)) {
				break;
			}
			int c = (int)(responseBuffer[i] & 0xFF);
			bb[(int)i] = (byte)c;
			i++;
		}
		connection.disconnect();
		connection = null;
		WeatherClientResponse response = new WeatherClientResponse();
		response.setResponseCode(responseCode);
		response.setResponseBody(bb);
		return(response);
	}

	private HttpURLConnection getURLConnection(URL url) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)url.openConnection();
		}
		catch(Exception e) {
			e.printStackTrace();
			connection = null;
		}
		return(connection);
	}

	private URL getFullURL(String[] coordinates) throws UnsupportedEncodingException, MalformedURLException {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String apiKey = preferences.getString("apiKey", activity.getResources().getString(R.string.apiKey));
		StringBuilder sb = new StringBuilder(Constants.WEATHER_BASE_URL);
		if(coordinates.length == 2) {
			sb.append("&lat=").append(coordinates[0]).append("&lon=").append(coordinates[1]);
		}
		else {
			sb.append("&q=").append(URLEncoder.encode(preferences.getString("city", Constants.DEFAULT_CITY)));
		}
		sb.append("&apiKey=");
		sb.append(apiKey);
		return(new URL(sb.toString()));
	}

	private void incLoadingCounter() {
        loading++;
    }

    private void decLoadingCounter() {
        loading--;
    }

	protected abstract void handleResponse(String result);
}