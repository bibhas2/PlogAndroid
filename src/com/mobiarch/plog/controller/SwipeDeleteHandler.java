package com.mobiarch.plog.controller;

import android.widget.ListView;

public interface SwipeDeleteHandler {
	/**
	 * This method is called when user completes swipe gesture to delete a ListView row.
	 * From this method one should actually delete the item for the row from the model
	 * and update the ListView.
	 * 
	 * @param lv - The ListView where swipe took place.
	 * @param position - The row position where wipe took place.
	 * 
	 * return - This method should return true if the item was deleted. Else, return false.
	 */
	public boolean onSwipeDelete(ListView lv, int position);

	public void onItemClick(int position);
}
