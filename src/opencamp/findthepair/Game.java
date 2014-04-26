package opencamp.findthepair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import opencamp.findthepair.customview.SquareImageView;
import opencamp.findthepair.model.Card;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
    		Card item = getItem(position);
    		item.touch();
            notifyDataSetChanged();
        }
	}
}
