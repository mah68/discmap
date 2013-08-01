package com.cary.discmap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.cary.discmap.server.ServerProfileScorecardsTask;

public class UserActivity extends DiscActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SessionManager.get(getApplicationContext()).checkLogin();
		setContentView(R.layout.activity_user);
		
		Intent i = getIntent();
		int user = i.getIntExtra("user", 0);
		new ServerProfileScorecardsTask(this).execute(user);
		
	}
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user, menu);
		return true;
	}
	
	public void loading() {
		//TODO
	}
	
	public void scorecardsLoaded(String json) {
		//TODO obviously
	}

}
