package opencamp.findthepair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import opencamp.findthepair.customview.SquareImageView;
import opencamp.findthepair.model.Card;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class Game extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
		IImageAdapter iImageAdapter = new IImageAdapter(this);
		gridview.setOnItemClickListener(iImageAdapter);
		
		TypedArray ta = getResources().obtainTypedArray(R.array.cards);
		
		int length = ta.length();
		ArrayList<Integer> resId = new ArrayList<Integer>(length);
		for (int i=0;i<length;i++) {
			resId.add(ta.getResourceId(i, 0));
		}		
		ta.recycle();
		
		Collections.shuffle(resId);		
		for(int i : resId) {
			iImageAdapter.add(new Card(i));
		}
		Collections.shuffle(resId);		
		for(int i : resId) {
			iImageAdapter.add(new Card(i));
		}
		
		gridview.setAdapter(iImageAdapter);
	}
	
	public class IImageAdapter extends ArrayAdapter<Card> implements OnItemClickListener{
		private Card openedCard1;
		private Card openedCard2;
		private boolean handlerLockerOn;

		public IImageAdapter(Context context) {
			super(context, R.layout.adapter_game);
		}
		
		 // create a new ImageView for each item referenced by the Adapter
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	SquareImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	        	imageView = new SquareImageView(getContext());
	        	imageView.setLayoutParams(
	            		new GridView.LayoutParams(
	            				GridView.LayoutParams.MATCH_PARENT, 
	            				GridView.LayoutParams.MATCH_PARENT
	            				));
	        } else {
	        	imageView = (SquareImageView) convertView;
	        }
	        Card item = getItem(position);
        	imageView.setBackgroundResource(item.getFrontResId());
	        return imageView;
	    }
	    
	    @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	    	if(openedCard1!=null && openedCard2 != null)
	    		return;
	    	
    		Card item = getItem(position);
    		if(item.isLock())
    			return ;
    		
    		if(openedCard1==null) {
    			openedCard1 = item;
    		} else {
    			if(openedCard1==item) // if touch same one. 
    				return; // no reaction.
    			openedCard2 = item;
    		}
    		item.touch();
            notifyDataSetChanged();
            
            if(openedCard2!=null && handlerLockerOn==false) {
            	handlerLockerOn = true;
            	new Handler().postDelayed(new Runnable() {
    				@Override
    				public void run() {
    					if(openedCard1.getResId()==openedCard2.getResId()) {
    						//get point;
    						openedCard1.lock();
    						openedCard2.lock();
    					} else {
    						openedCard1.clear();
    						openedCard2.clear();
    					}
    					openedCard1=openedCard2=null;
    					handlerLockerOn=false;
    					notifyDataSetChanged();
    				}
    			}, 500);	
            }
        }
	}
	
	public boolean scoreSend(String name, int score, double time, double acc)
	{
//		SimpleData simple = new SimpleData(name, score, time, acc, new Date());
		
		return true;
	}
}

