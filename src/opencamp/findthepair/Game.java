package opencamp.findthepair;

import java.util.ArrayList;
import java.util.Collections;

import opencamp.findthepair.customview.SquareImageView;
import opencamp.findthepair.model.Card;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Game extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		// init gridView
		GridView gridview = (GridView) findViewById(R.id.gridview);
		IImageAdapter iImageAdapter = new IImageAdapter(this);
		gridview.setOnItemClickListener(iImageAdapter);
		// load resource.
		TypedArray ta = getResources().obtainTypedArray(R.array.cards);
		int length = ta.length();
		ArrayList<Integer> resId = new ArrayList<Integer>(length);
		for (int i=0;i<length;i++) {
			resId.add(ta.getResourceId(i, 0));
		}		
		ta.recycle();
		Collections.shuffle(resId);
		resId = new ArrayList<Integer>(resId.subList(0, 10));
		// add resource into adapter properly.
		Collections.shuffle(resId);		
		for(int i : resId) {
			iImageAdapter.add(new Card(i));
		}
		Collections.shuffle(resId);		
		for(int i : resId) {
			iImageAdapter.add(new Card(i));
		}
		// finish init.
		gridview.setAdapter(iImageAdapter);
		
		// init timer
		ProgressBar progressBarTime = (ProgressBar) findViewById(R.id.progressBarTime);
		
		//init timer Text
		TextView textViewTime = (TextView) findViewById(R.id.textViewTime);
		
		new PreSequanceTasker(gridview, progressBarTime, textViewTime).execute();
	}
	
	public static class SequanceTasker extends AsyncTask<Void, Integer, Void> {
		protected GridView gdv;
		protected ProgressBar pbar;
		protected TextView text;
		
		protected int timeLmt = 10;
		protected int tick;
		protected int max;
		
		public SequanceTasker(GridView gdv, ProgressBar pbar, TextView text) {
			this.gdv = gdv;
			this.pbar = pbar;
			this.text = text;
		}
		protected void init() {
			tick = 1000/30;
			max = timeLmt*tick*30;
		}
		
		
		@Override
		protected void onPreExecute() {
			pbar.setMax(max);
			pbar.setProgress(max);
			text.setText(timeLmt+" Sec");
			
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				for(int i=0; i<timeLmt ; i++ ) {
					for(int j=0;j<30;j++) {
						Thread.sleep(tick);
						publishProgress(tick,0);
					}
					publishProgress(0,timeLmt-i-1);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			int progress = values[0];
			int sec = values[1];
			
			if(progress!=0) {
				pbar.incrementProgressBy(-progress);
			} else {
				text.setText(sec+" Sec");
			}
			super.onProgressUpdate(values);
		}
	}
	
	
	public static class MainSequanceTasker extends SequanceTasker {

		public MainSequanceTasker(GridView gdv, ProgressBar pbar, TextView text) {
			super(gdv, pbar, text);
			timeLmt = 60;
			init();
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				for(int i=0; i<timeLmt ; i++ ) {
					for(int j=0;j<30;j++) {
						Thread.sleep(tick);
						publishProgress(tick,0);
						if(isGameClear())
							return null;
					}
					publishProgress(0,timeLmt-i-1);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		private boolean isGameClear() {
			boolean comp = true;
			IImageAdapter adapter = (IImageAdapter) gdv.getAdapter();
			int count = adapter.getCount();
			for(int i=0; i < count && comp; i++) {
				Card item = (Card) adapter.getItem(i);
				comp &= item.isLock();
			}
			return comp;
		}
		@Override
		protected void onPostExecute(Void result) {
			Log.d("part of score",":::"+pbar.getProgress());
			
			IImageAdapter adapter = (IImageAdapter) gdv.getAdapter();
			int count = adapter.getCount();
			for(int i=0; i < count; i++) {
				Card item = (Card) adapter.getItem(i);
				item.lock(); // whatever it is.
			}
			adapter.notifyDataSetChanged();
			
			super.onPostExecute(result);
		}		
		
	}
	
	public static class PreSequanceTasker extends SequanceTasker {
		public PreSequanceTasker(GridView gdv, ProgressBar pbar, TextView text) {
			super(gdv, pbar, text);
			timeLmt = 10;
			init();
		}
		
		
		@Override
		protected void onPreExecute() {
			IImageAdapter adapter = (IImageAdapter) gdv.getAdapter();
			int count = adapter.getCount();
			for(int i=0; i < count; i++) {
				Card item = (Card) adapter.getItem(i);
				item.touch();
			}
			adapter.notifyDataSetChanged();
			
			super.onPreExecute();
		}
		@Override
		protected void onPostExecute(Void result) {
			IImageAdapter adapter = (IImageAdapter) gdv.getAdapter();
			int count = adapter.getCount();
			for(int i=0; i < count; i++) {
				Card item = (Card) adapter.getItem(i);
				item.clear();
			}
			adapter.notifyDataSetChanged();
			
			new MainSequanceTasker(gdv,pbar,text).execute();
			
			super.onPostExecute(result);
		}
		

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

