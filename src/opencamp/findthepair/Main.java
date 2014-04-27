package opencamp.findthepair;	

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, Menu.NONE, "Select Level");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			final String[] items = {"Easy", "Hard", "Very Hard"};
			final SharedPreferences sp = getSharedPreferences("pref", MODE_PRIVATE);
			int setLevel = sp.getInt("level", 0);
			AlertDialog.Builder ab = new AlertDialog.Builder(Main.this);
			ab.setTitle("Select Level");
			ab.setSingleChoiceItems(items, setLevel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences.Editor editor = sp.edit();
					editor.putInt("level", which);
					editor.commit();
					
					dialog.dismiss();
				}
			});
			ab.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}