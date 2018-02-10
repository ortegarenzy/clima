package com.renzyortega.clima.models;

public class Location {

	private String city;
	private String country;
	private float latitude;
	private float longitude;

	public void setCity(String c) {
		city = c;
	}

	public String getCity() {
		return(city);
	}

	public void setCountry(String c) {
		country = c;
	}

	public String getCountry() {
		return(country);
	}

	public void setLatitude(float lat) {
		latitude = lat;
	}

	public float getLatitude() {
		return(latitude);
	}

	public void setLongitude(float lon) {
		longitude = lon;
	}

	public float getLongitude() {
		return(longitude);
	}
}