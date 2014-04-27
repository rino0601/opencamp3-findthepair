package opencamp.findthepair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

public class Rank extends OrmLiteBaseActivity<DatabaseHelper> {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rank);
		//doSampleDatabaseStuff("onCreate");
		new NetworkTasker().execute();
	}
	
	private void doSampleDatabaseStuff(String action) {
		RuntimeExceptionDao<SimpleData, Integer> simpleDao = getHelper().getSimpleDataDao();
		QueryBuilder<SimpleData, Integer> qB = simpleDao.queryBuilder();
		qB.orderBy("score", false);
		PreparedQuery<SimpleData> pQ = null;
		try {
			pQ = qB.prepare();
		} catch (SQLException e){}
		
		List<SimpleData> list = simpleDao.query(pQ);
		
		ListView rankList = (ListView)findViewById(R.id.RANK);
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		for (SimpleData simple : list) {
			map = new HashMap<String, String>();
			map.put("name", simple.name);
			map.put("score", ""+simple.score);
			mylist.add(map);
		}
		
		SimpleAdapter mSchedule = new SimpleAdapter(this, mylist, R.layout.row,
	            new String[] {"name", "score"}, new int[] {R.id.NAME_CELL, R.id.SCORE_CELL});
		rankList.setAdapter(mSchedule);
	}
	
	public class NetworkTasker extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result = SendByHttp(""); // 메시지를 서버에 보냄
			ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
			try {
				JSONArray ja = new JSONArray(result); //Json만들고 밑에 루프 돌면서 Parsing
								
				HashMap<String, String> map;
				for(int j=0; j<ja.length(); j++){
					JSONObject order = ja.getJSONObject(j);
					map = new HashMap<String, String>();
					map.put("name", order.getString("name"));
					map.put("score", ""+order.getInt("score"));
					mylist.add(map);
				}
			} catch (Exception e) { e.printStackTrace(); }
			return mylist;
		}
		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			ListView rankList = (ListView)findViewById(R.id.RANK);
			SimpleAdapter mSchedule = new SimpleAdapter(Rank.this, result, R.layout.row,
		            new String[] {"name", "score"}, new int[] {R.id.NAME_CELL, R.id.SCORE_CELL});
			rankList.setAdapter(mSchedule);
			super.onPostExecute(result);
		}
	}
	
	/**
	 * 서버에 데이터를 보내는 메소드
	 * @param msg
	 * @return
	 */
	private String SendByHttp(String msg) {
		if(msg == null)
			msg = "";
		
		// 서버를 설정해주세요!!!
		String URL = "http://165.194.35.212/get/";
		
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* 체크할 id와 pwd값 서버로 전송 */
			HttpPost post = new HttpPost(URL+"?msg="+msg);


			/* 지연시간 최대 3초 */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			/* 데이터 보낸 뒤 서버에서 데이터를 받아오는 과정 */
			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(),
							"utf-8"));

			String line = null;
			String result = "";

			while ((line = bufreader.readLine()) != null) {
				result += line;
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();	// 연결 지연 종료
			return ""; 
		}
		
	}

	/**
	 * 받은 JSON 객체를 파싱하는 메소드
	 * @param page
	 * @return
	 */
	public String[][] jsonParserList(String pRecvServerPage) {
		
		Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);
		
		try {
			JSONObject json = new JSONObject(pRecvServerPage);
			JSONArray jArr = json.getJSONArray("List");


			// 받아온 pRecvServerPage를 분석하는 부분
			String[] jsonName = {"msg1", "msg2", "msg3"};
			String[][] parseredData = new String[jArr.length()][jsonName.length];
			for (int i = 0; i < jArr.length(); i++) {
				json = jArr.getJSONObject(i);
				if(json != null) {
					for(int j = 0; j < jsonName.length; j++) {
						parseredData[i][j] = json.getString(jsonName[j]);
					}
				}
			}
			
			
			// 분해 된 데이터를 확인하기 위한 부분
			for(int i=0; i<parseredData.length; i++){
				Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][0]);
				Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][1]);
				Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][2]);
			}

			return parseredData;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}

