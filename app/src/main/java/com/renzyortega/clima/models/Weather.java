package com.renzyortega.clima.models;

import java.util.Date;

public class Weather {

	public Location location;
	public Temperature temperature;
	public Sys sys;
	private int weatherId;
	private Date date;
	private String description;
	private String icon;

	public void setWeatherId(int id) {
		weatherId = id;
	}

	public int getWeatherId() {
		return(weatherId);
	}

	public void setDate(Date d) {
		date = d;
	}

	public Date getDate() {
		return(date);
	}

	public void setDescription(String d) {
		description = d;
	}

	public String getDescription() {
		return(description);
	}

	public String getIcon() {
        return(icon);
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}