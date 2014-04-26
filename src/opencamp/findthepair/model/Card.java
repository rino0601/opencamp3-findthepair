package opencamp.findthepair.model;

import opencamp.findthepair.R;


public class Card {	
	private static final int defaultResId = R.drawable.ic_launcher; 
	private int frontResId = defaultResId;
	private int resId;
	public Card(int resId) {
		this.resId = resId;
	}
	public int getFrontResId() {
		return frontResId;
	}

	public void touch() {
		frontResId = resId;
	}
	public void clear() {
		frontResId = defaultResId;
	}
	public int getResId() {
		return resId;
	}
}
