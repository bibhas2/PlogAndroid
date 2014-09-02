package com.mobiarch.plog.controller;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.mobiarch.plog.R;
import com.mobiarch.plog.util.Logger;

public class SearchResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, 
							LogListFragment.newInstance(getSearchQuery(getIntent()))).commit();
		}
	}
	
	private String getSearchQuery(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            Logger.info("Searching for: %s", query);
            
            return query;
        }
		
		return null;
	}
}
