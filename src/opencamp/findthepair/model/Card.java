package opencamp.findthepair.model;

import opencamp.findthepair.R;


public class Card {	
	private static final int defaultResId = R.drawable.ic_launcher; 
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
	public void lock() {
		isLocked = true;		
	}
	public boolean isLock() {
		return isLocked;
	}
}
