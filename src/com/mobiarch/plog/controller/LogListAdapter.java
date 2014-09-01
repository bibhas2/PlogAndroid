package com.mobiarch.plog.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobiarch.plog.R;
import com.mobiarch.plog.model.LogEntry;

public class LogListAdapter extends BaseAdapter {
	List<LogEntry> list;
	SimpleDateFormat sdf;
	
	public LogListAdapter(List<LogEntry> list) {
		super();
		this.list = list;
		this.sdf = new SimpleDateFormat("E, d MMM yyyy 'at' k a");
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
        if (convertView == null) {
        	LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	convertView = inflater.inflate(R.layout.log_cell, null);
        }
        LogEntry e = list.get(position);
        TextView tv;
        
        tv = (TextView) convertView.findViewById(R.id.logLabel);
        tv.setText(e.getText());
        tv = (TextView) convertView.findViewById(R.id.logCreateDateLabel);
        tv.setText(sdf.format(e.getCreatedOn()));
        
        return convertView;
	}

}
