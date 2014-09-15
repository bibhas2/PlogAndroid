package com.mobiarch.plog.controller;

import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class LogListFragment extends Fragment implements LogDataReceiver, SwipeDeleteHandler, android.content.DialogInterface.OnClickListener {
	private static final int MENU_SHARE = 0;
	private static final int MENU_COPY = 1;
	private static final int MENU_DELETE = 2;
	private static final String SEARCH_QUERY = "search-query";
	private ArrayList<LogEntry> logList;
	private ListView listView;
	private EditText logText;
	private LogListAdapter logListAdapter;
	private LogDAO dao;
	private String searchQuery;
	private int itemClickPosition = -1;
	
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

	@Override
	public void onClick(DialogInterface arg0, int selectedItem) {
		if (itemClickPosition < 0) {
			return;
		}
		LogEntry e = logList.get(itemClickPosition);

		if (selectedItem == MENU_DELETE) {
			dao.deleteLogEntry(e);
			logList.remove(itemClickPosition);
			logListAdapter.notifyDataSetChanged();
			itemClickPosition = -1;
		} else if (selectedItem == MENU_COPY) {
			ClipboardManager clipboard = (ClipboardManager)
			        getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("simple text", e.getText());
			clipboard.setPrimaryClip(clip);
		} else if (selectedItem == MENU_SHARE) {
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, e.getText());
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
		}
	}

	@Override
	public void onItemClick(int position) {
		itemClickPosition = position;
		
    	String items[] = {"Share", "Copy", "Delete"};
    	new AlertDialog.Builder(getActivity()).setItems(items, LogListFragment.this).create().show();
	}
}
