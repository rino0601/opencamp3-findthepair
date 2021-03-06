package opencamp.findthepair.model;

import opencamp.findthepair.R;


public class Card {	
	private static final int defaultResId = R.drawable.r000; 
	private int frontResId = defaultResId;
	private int resId;
	private boolean isLocked = false;
	
	public Card(int resId) {
		this.resId = resId;
	}
	public int getFrontResId() {
		return frontResId;
	}

	public void touch() {
		if(!isLocked)
			frontResId = resId;
	}
	public void clear() {
		if(!isLocked)
			frontResId = defaultResId;
	}
	public int getResId() {
		return resId;
	}
	public void lock(boolean lock) {
		isLocked = lock;		
	}
	public boolean isLock() {
		return isLocked;
	}
}
