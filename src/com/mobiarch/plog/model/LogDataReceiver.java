package com.mobiarch.plog.model;

import java.util.ArrayList;


public interface LogDataReceiver {
	public void onLogListAvailable(ArrayList<LogEntry> list);
	public void onError(String message);
}
