package com.example.habitathumanityapp;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class OnFlingGestureListener implements OnTouchListener 
{
	@SuppressWarnings("deprecation")
	private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());
	
	@Override
	public boolean onTouch(final View v, final MotionEvent event)
	{
		return gestureDetector.onTouchEvent(event);
	}

	private final class GestureListener extends SimpleOnGestureListener
	{
		private final int SWIPE_MIN_DISTANCE = 100;
		private final int SWIPE_THRESHOLD_VELOCITY = 100;
		
		public GestureListener()
		{}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float xVelocity, float yVelocity)
		{
			// Right-to-left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(xVelocity) > SWIPE_THRESHOLD_VELOCITY)
			{
				onRightToLeftSwipe();
				return true;
			}
			// Left-to-right swipe
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(xVelocity) > SWIPE_THRESHOLD_VELOCITY)
			{
				onLeftToRightSwipe();
				return true;
			}
			// Bottom-to-top swipe
			else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(yVelocity) > SWIPE_THRESHOLD_VELOCITY)
			{
				onBottomToTopSwipe();
				return true;
			}
			// Top-to-bottom swipe
			else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(yVelocity) > SWIPE_THRESHOLD_VELOCITY)
			{
				onTopToBottomSwipe();
				return true;
			}
			
			return false;
		}
	}
	
	public abstract void onRightToLeftSwipe();
	public abstract void onLeftToRightSwipe();
	public abstract void onBottomToTopSwipe();
	public abstract void onTopToBottomSwipe();
}
