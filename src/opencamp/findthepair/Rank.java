package opencamp.findthepair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

public class Rank extends OrmLiteBaseActivity<DatabaseHelper> {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rank);
		doSampleDatabaseStuff("onCreate");
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
}

