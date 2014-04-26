package opencamp.findthepair;

import java.util.ArrayList;
import java.util.Collections;

import opencamp.findthepair.customview.SquareImageView;
import opencamp.findthepair.model.Card;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
		
		String test = sendByHttp("test", 100);
		Log.i("postsend test", test);
		
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
	
	private String sendByHttp(String name, int score) {
		if(name == null)
			return "NULL_NAME";
		
		// 서버를 설정해주세요!!!
		String URL = "http://165.194.35.212/post";
		HttpClient http = new DefaultHttpClient();
		try { 

			ArrayList<NameValuePair> nameValuePairs = 
					new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("score", ""+score));

			HttpParams params = http.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			HttpConnectionParams.setSoTimeout(params, 5000);

			HttpPost httpPost = new HttpPost(URL);
			UrlEncodedFormEntity entityRequest = 
					new UrlEncodedFormEntity(nameValuePairs, "utf-8");
			
			httpPost.setEntity(entityRequest);
			
			HttpResponse response = http.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode(); // This will be 200 or 404, etc.
			
			return ""+statusCode;
		}catch(Exception e){e.printStackTrace();return "";}
		
	}
	
	public boolean scoreSend(String name, int score)
	{
//		SimpleData simple = new SimpleData(name, score, time, acc, new Date());
		
		return true;
	}
}

