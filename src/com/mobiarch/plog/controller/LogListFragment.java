package com.mobiarch.plog.controller;

import java.util.ArrayList;
import java.util.Date;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mobiarch.plog.R;
import com.mobiarch.plog.model.LogDAO;
import com.mobiarch.plog.model.LogDataReceiver;
import com.mobiarch.plog.model.LogEntry;

public class LogListFragment extends Fragment implements LogDataReceiver, SwipeDeleteHandler {
	private ArrayList<LogEntry> logList;
	private ListView listView;
	private EditText logText;
	private LogListAdapter logListAdapter;
	private LogDAO dao;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.log_list, container,
				false);

		listView = (ListView) rootView.findViewById(R.id.logListView);
		
		Button saveBtn = (Button) rootView.findViewById(R.id.saveButton);
		
		saveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addNewEntry();
			}
		});
		
		logText = (EditText) rootView.findViewById(R.id.logText);
		
		dao = new LogDAO(this, getActivity());
		dao.getLogListAsync();
		
		return rootView;
	}

	protected void addNewEntry() {
		LogEntry e = new LogEntry();
		
		e.setText(logText.getText().toString());
		e.setCreatedOn(new Date());
		logList.add(e);
		
		dao.addLogEntry(e);
		
		logListAdapter.notifyDataSetChanged();
		
		logText.setText("");
	}

	@Override
	public void onLogListAvailable(ArrayList<LogEntry> list) {
		this.logList = list;
		
		logListAdapter = new LogListAdapter(list, this);
		listView.setAdapter(logListAdapter);
	}

	@Override
	public void onError(String message) {
		
	}

	@Override
	public boolean onSwipeDelete(ListView lv, int position) {
		LogEntry e = logList.get(position);
		
		dao.deleteLogEntry(e);
		logList.remove(position);
		
		return true;
	}
}
