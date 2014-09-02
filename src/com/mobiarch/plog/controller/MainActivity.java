package com.mobiarch.plog.controller;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchManager.OnCancelListener;
import android.app.SearchManager.OnDismissListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;

import com.mobiarch.plog.R;
import com.mobiarch.plog.util.Logger;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new LogListFragment()).commit();
		}
		
		handleIntent(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));
	    searchManager.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel() {
				Logger.info("Running **again** in non-search mode.");
			}
		});
	    searchManager.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				Logger.info("**setOnDismissListener.");				
			}
		});
	    searchView.setOnCloseListener(new OnCloseListener() {
			@Override
			public boolean onClose() {
				Logger.info("Search view getting closed.");
				return false;
			}
		});
	    
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		return super.onOptionsItemSelected(item);
	}
	@Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

	private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            Logger.info("Searching for: %s", query);
        } else {
        	Logger.info("Running in non-search mode.");
        }
    }
}
