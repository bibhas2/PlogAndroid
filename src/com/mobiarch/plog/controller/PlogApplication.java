package com.mobiarch.plog.controller;

import com.mobiarch.plog.model.DBHelper;

import android.app.Application;

public class PlogApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		//Create or update schema
		DBHelper dbh = new DBHelper(this);
		
		dbh.getWritableDatabase().close();
	}
}
