package opencamp.findthepair;	

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity {

	Button btn_game, btn_rank;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		btn_game = (Button)findViewById(R.id.btn_game);
		btn_rank = (Button)findViewById(R.id.btn_rank);
		
		btn_game.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent1 = new Intent(Main.this, Game.class);
				startActivity(intent1);
			}
		});

		btn_rank.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent2 = new Intent(Main.this, Rank.class);
				startActivity(intent2);
			}
		});
	}
}