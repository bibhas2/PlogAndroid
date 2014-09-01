package com.mobiarch.plog.util;

import android.util.Log;

public class Logger {
	private static final String TAG = "PLOG";
	
	public static void info(String format, Object... args) {
		String output = String.format(format, args);
		Log.i(TAG, output);
	}
	public static void error(String message, Throwable e) {
		Log.e(TAG, message, e);
	}
}
