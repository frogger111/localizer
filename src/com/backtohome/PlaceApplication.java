package com.backtohome;


import android.app.Application;

import com.backtohome.others.PlacesDataSource;

public class PlaceApplication extends Application {

	//public static final String APP_NAME = "ThousandWords";

	private PlacesDataSource placeDataSource;

	@Override
	public void onCreate() {
		super.onCreate();
		
		this.placeDataSource = new PlacesDataSource(this);
		this.placeDataSource.createDataBase();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public PlacesDataSource getPlacesDataSource() {
		return this.placeDataSource;
	}

	public void setDataHelper(PlacesDataSource dataHelper) {
		this.placeDataSource = dataHelper;
	}
}
