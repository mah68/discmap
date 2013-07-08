package com.cary.discmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	Button logout;
	Button testHole;
	TextView welcome;
	SessionManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manager = SessionManager.get(getApplicationContext());
		manager.checkLogin();
		setContentView(R.layout.activity_main);
		
		welcome = (TextView) findViewById(R.id.userWelcomeText);
		welcome.setText("Welcome, "+manager.getUser());
		
		logout = (Button) findViewById(R.id.logoutMainButton);
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				manager.logoutUser();
			}
			
		});
		
		testHole = (Button) findViewById(R.id.testHoleMainButton);
		testHole.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), HoleActivity.class);
				i.putExtra("course", 1);
				i.putExtra("hole", 1);
				startActivity(i);
				
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
