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
	Button testCourse;
	TextView welcome;
	SessionManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manager = SessionManager.get(getApplicationContext());
		manager.checkLogin();
		setContentView(R.layout.activity_main);
		
		//TODO check if the device has google play services
		
		welcome = (TextView) findViewById(R.id.userWelcomeText);
		welcome.setText("Welcome, "+manager.getUser());
		
		logout = (Button) findViewById(R.id.logoutMainButton);
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				manager.logoutUser();
			}
			
		});
		
		testCourse = (Button) findViewById(R.id.testCourseMainButton);
		testCourse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), CourseActivity.class);
				i.putExtra("course", 1);
				startActivity(i);
			}
			
		});
		
		Button testProfile = (Button) findViewById(R.id.testProfileMainButton);
		testProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), UserActivity.class);
				i.putExtra("user", 7);
				startActivity(i);
			}
			
		});
		
		Button search = (Button) findViewById(R.id.searchCoursesMainButton);
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), SearchActivity.class);
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
