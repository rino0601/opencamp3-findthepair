package opencamp.findthepair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.UpdateBuilder;

public class Game extends OrmLiteBaseActivity<DatabaseHelper> {

	private ProgressBar progressBarTime;
	private TextView textViewTime;
	private GridView gridview;
	
	private AsyncTask<Void,Integer,Void> taskPreseq;
	private AsyncTask<Void,Integer,Void> taskMainseq;
	
	private Handler handlerMainThread;
	private Runnable runnableCardFlip;
	private Runnable runnableEndOfGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		gridview = (GridView) findViewById(R.id.gridview);
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
		
		progressBarTime = (ProgressBar) findViewById(R.id.progressBarTime);
		
		textViewTime = (TextView) findViewById(R.id.textViewTime);
	}
	
	@Override
	protected void onStart() {
		handlerMainThread = new Handler();
		taskPreseq = new PreSequanceTasker(gridview, progressBarTime, textViewTime).execute();
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		if(handlerMainThread!=null) {
			handlerMainThread.removeCallbacksAndMessages(null); // remove all runnable.
			// see http://stackoverflow.com/a/11299735/1046060
		}
		if(taskMainseq!=null) {
			taskMainseq.cancel(true);
		}
		if(taskPreseq !=null)
		{
			taskPreseq.cancel(true);
		}
		super.onStop();
	}
	
	public class SequanceTasker extends AsyncTask<Void, Integer, Void> {
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
	
	
	public class MainSequanceTasker extends SequanceTasker {
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
			final boolean isClear = isGameClear();
			
			IImageAdapter adapter = (IImageAdapter) gdv.getAdapter();
			int count = adapter.getCount();
			for(int i=0; i < count; i++) {
				Card item = (Card) adapter.getItem(i);
				item.lock(true); // whatever it is.
			}
			adapter.notifyDataSetChanged();
			
			runnableEndOfGame = new Runnable() {
				@Override
				public void run() {
					if(isClear){
						showTextInputeDlgForRanking();
					} else {
						showGameOverDlg();
					}
				}

				private void showGameOverDlg() {
					AlertDialog.Builder builder = new AlertDialog.Builder(gdv.getContext());
					builder.setTitle("GameOver");
					// Set up the buttons
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					        finish();
					    }
					});
					builder.show();
				}

				private void showTextInputeDlgForRanking() {
					AlertDialog.Builder builder = new AlertDialog.Builder(gdv.getContext());
					builder.setTitle("Type your name to record to rank.");

					// Set up the input
					final EditText input = new EditText(gdv.getContext());
					// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
					input.setInputType(InputType.TYPE_CLASS_TEXT);
					builder.setView(input);

					// Set up the buttons
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    	
					        String name = input.getText().toString();
					        int score = (int) (1000000*(pbar.getProgress()/60000.0));
					        Date date = Calendar.getInstance().getTime();
					        
							RuntimeExceptionDao<SimpleData, Integer> simpleDao = getHelper().getSimpleDataDao();
					        try {
					        	List<SimpleData> list = simpleDao.queryBuilder().where().eq("name", name).query();
					    		if ( !list.isEmpty() && list.get(0).score < score ) {
									UpdateBuilder<SimpleData, Integer> updateBuilder = simpleDao.updateBuilder();
									updateBuilder.updateColumnValue("score", ""+score);
									updateBuilder.updateColumnValue("date", date);
									updateBuilder.where().eq("name", name);
									updateBuilder.update();
					    		} else {
									simpleDao.create(new SimpleData(name, score, date));
					    		}
							} catch (SQLException e) {
								e.printStackTrace();
							}
					        finish();
					    }
					});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					        dialog.cancel();
					    }
					});
					builder.show();
				}
			};
			handlerMainThread.postDelayed(runnableEndOfGame, 1000);
			
			super.onPostExecute(result);
		}		
		
	}
	
	public class PreSequanceTasker extends SequanceTasker {
		
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
				item.lock(true);
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
				item.lock(false);
				item.clear();
			}
			adapter.notifyDataSetChanged();
			
			taskMainseq = new MainSequanceTasker(gdv,pbar,text).execute();
			
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
            	runnableCardFlip = new Runnable() {
    				@Override
    				public void run() {
    					if(openedCard1.getResId()==openedCard2.getResId()) {
    						//get point;
    						openedCard1.lock(true);
    						openedCard2.lock(true);
    					} else {
    						openedCard1.clear();
    						openedCard2.clear();
    					}
    					openedCard1=openedCard2=null;
    					handlerLockerOn=false;
    					notifyDataSetChanged();
    				}
    			};
            	handlerMainThread.postDelayed(runnableCardFlip, 500);	
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

