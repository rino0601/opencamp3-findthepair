package opencamp.findthepair;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

public class Rank extends OrmLiteBaseActivity<DatabaseHelper> {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		doSampleDatabaseStuff("onCreate", tv);
		setContentView(tv);
	}
	
	@SuppressWarnings("unchecked")
	private void doSampleDatabaseStuff(String action, TextView tv) {
		RuntimeExceptionDao<SimpleData, Integer> simpleDao = getHelper().getSimpleDataDao();
		QueryBuilder<SimpleData, Integer> qB = simpleDao.queryBuilder();
		qB.orderBy("score", false);
		PreparedQuery<SimpleData> pQ = null;
		StringBuilder sb = new StringBuilder();
		try {
			pQ = qB.prepare();
		} catch (SQLException e) {
			// ignore
		}
		List<SimpleData> list = simpleDao.query(pQ);
		//Collections.sort(list);
		sb.append("got ").append(list.size()).append(" entries in ").append(action).append("\n");
		sb.append("------------------------------------------\n");

		// if we already have items in the database
		int simpleC = 0;
		for (SimpleData simple : list) {
			// USE
			// simple.id
			// simple.name
			// simple.score
			// simple.time
			// simple.acc
			// simple.date
			sb.append("[").append(simpleC+1).append("] ").append(simple).append("\n");
			simpleC++;
		}
		
		

		tv.setText(sb.toString());
		
	}
}

