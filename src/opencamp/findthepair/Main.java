package opencamp.findthepair;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity {

	Button btn_start, btn_rank;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		btn_start = (Button)findViewById(R.id.btn_start);
		btn_rank = (Button)findViewById(R.id.btn_rank);
		
		btn_start.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});

		btn_rank.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});
	}
}