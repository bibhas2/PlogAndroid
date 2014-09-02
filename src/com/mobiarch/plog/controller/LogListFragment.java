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
	private static final String SEARCH_QUERY = "search-query";
	private ArrayList<LogEntry> logList;
	private ListView listView;
	private EditText logText;
	private LogListAdapter logListAdapter;
	private LogDAO dao;
	private String searchQuery;
	
	/**
	 * Fragments should not have non-zero arg constructor. That's because when Android
	 * needs to automatically recreate a destroyed fragment, it calls the zero arg constructor.
	 * We should use bundle to pass initialization parameters instead. They are persisted by Android
	 * in case it had to destroy a fragment.
	 * 
	 * @param searchQuery
	 * @return
	 */
	public static LogListFragment newInstance(String searchQuery) {
		LogListFragment f = new LogListFragment();
		Bundle args = new Bundle();
		if (searchQuery != null) {
			args.putString(SEARCH_QUERY, searchQuery);
		}
		f.setArguments(args);
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.log_list, container,
				false);

		listView = (ListView) rootView.findViewById(R.id.logListView);
		
		dao = new LogDAO(this, getActivity());

		searchQuery = getArguments() != null ? getArguments().getString(SEARCH_QUERY) : null;
		
		if (searchQuery == null) {
			//Normal mode
			dao.getLogListAsync();

			Button saveBtn = (Button) rootView.findViewById(R.id.saveButton);
			
			saveBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					addNewEntry();
				}
			});
			
			logText = (EditText) rootView.findViewById(R.id.logText);
		} else {
			//Search result mode
			dao.search(searchQuery);
			//Hide the new entry area
			View addEntryArea = rootView.findViewById(R.id.addEntryArea);
			
			addEntryArea.setVisibility(View.GONE);
		}
		
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
