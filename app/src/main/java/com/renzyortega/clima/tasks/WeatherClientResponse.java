package com.renzyortega.clima.tasks;

public class WeatherClientResponse {

	private int responseCode;
	private byte[] responseBody;

	public WeatherClientResponse() {
	}

	public WeatherClientResponse setResponseCode(int code) {
		responseCode = code;
		return(this);
	}

	public int getResponseCode() {
		return(responseCode);
	}

	public WeatherClientResponse setResponseBody(byte[] body) {
		responseBody = body;
		return(this);
	}

	public byte[] getResponseBody() {
		return(responseBody);
	}
}
