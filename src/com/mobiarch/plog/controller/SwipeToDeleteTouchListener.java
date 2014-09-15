package com.mobiarch.plog.controller;

import android.animation.Animator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.mobiarch.plog.util.Logger;

/**
 * This touch listener horizontally drags the view that it is attached to.
 * It is designed to operate within a scrolled view. It does so by calling requestDisallowInterceptTouchEvent
 * when user is dragging the view horizontally. Scrolling becomes disabled at that point since
 * the view begins to keep all touch events to itself and doesn't let them bubble up to the
 * parent scroll view. Otherwise, normal vertical scrolling is allowed when user is dragging vertically.
 * 
 * @author bibhas
 *
 */
public class SwipeToDeleteTouchListener implements OnTouchListener {
	private float deleteThreshold = 100f; //Minimum horizontal drag that will initiate a delete operation.
	private int swipeThreshold = 40; //Minimum horizontal drag that will lock scrolling and start moving the row.
	
	private float downX;
	private boolean scrollLocked = false;
	private ListView listView;
	private SwipeDeleteHandler deleteHandler;
	
	/**
	 * 
	 * @param listView - The ListView for the row cell to which this touch listener is attached to.
	 * @param deleteHandler - The listener that is notified when user completes a swipe action.
	 */
	public SwipeToDeleteTouchListener(ListView listView, SwipeDeleteHandler deleteHandler) {
		super();
		this.listView = listView;
		this.deleteHandler = deleteHandler;
	}

	@Override
	public boolean onTouch(final View view, MotionEvent event) {
		int action = event.getAction();

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			scrollLocked = false;

			break;
		case MotionEvent.ACTION_MOVE:
			float diff = event.getX() - downX;

			if (!scrollLocked && (Math.abs(diff) > swipeThreshold)) {
				view.getParent().requestDisallowInterceptTouchEvent(true);
				Logger.info("Locking scroll");
				scrollLocked = true;
			}

			if (scrollLocked) {
				view.setTranslationX(view.getTranslationX() + diff);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (scrollLocked) {
				Logger.info("Un-locking scroll. dX: " + view.getTranslationX());
				//Release lock
				view.getParent().requestDisallowInterceptTouchEvent(false);
				scrollLocked = false;

				//If we haven't dragged much do nothing.
				if (view.getTranslationX() < deleteThreshold) {
					view.setTranslationX(0);
					
					break;
				}
				//See if this item is being deleted
				int position = listView.getPositionForView(view);
				
				if (!deleteHandler.onSwipeDelete(listView, position)) {
					//We are not deleting the item after all
					break;
				}
				//Item is deleted. Animate the row into oblivion
				view.animate().setDuration(500).scaleY(0f).scaleX(0f)
						.setListener(new Animator.AnimatorListener() {
							@Override
							public void onAnimationStart(Animator anim) {
							}
							@Override
							public void onAnimationRepeat(Animator anim) {
							}
							@Override
							public void onAnimationEnd(Animator anim) {
								Logger.info("Delete log entry here.");
								//Restore scale. They may have been set to zero after a swipe deletion
								view.setScaleX(1f);
								view.setScaleY(1f);
								view.setTranslationX(0f);

								((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
							}

							@Override
							public void onAnimationCancel(Animator anim) {
							}
						});
			} else {
				/*
				 * There was not much movement, this is just a click. Fire the click
				 * handler function.
				 */
				int position = listView.getPositionForView(view);
				deleteHandler.onItemClick(position);
			}

			break;
		}
		return true;
	}

	public float getDeleteThreshold() {
		return deleteThreshold;
	}

	public void setDeleteThreshold(float deleteThreshold) {
		this.deleteThreshold = deleteThreshold;
	}

	public int getSwipeThreshold() {
		return swipeThreshold;
	}

	public void setSwipeThreshold(int swipeThreshold) {
		this.swipeThreshold = swipeThreshold;
	}
}
