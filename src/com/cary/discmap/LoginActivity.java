package com.cary.discmap;

import com.cary.discmap.server.ServerCreateAccountTask;
import com.cary.discmap.server.ServerLoginTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends Activity {
	EditText username;
	EditText password;
	EditText confirm;
	Button submit;
	Button create;
	TextView error;
	ImageView loading;
	SessionManager manager;
	AlertDialogManager alert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		username = (EditText) findViewById(R.id.userEditText);
		password = (EditText) findViewById(R.id.passwordEditText);
		confirm = (EditText) findViewById(R.id.passwordConfirmEditText);
		submit = (Button) findViewById(R.id.LoginButton);
		create = (Button) findViewById(R.id.createAccountButton);
		error = (TextView) findViewById(R.id.loginErrorView);
		loading = (ImageView) findViewById(R.id.loginLoadingIcon);
		manager = SessionManager.get(getApplicationContext());
		alert = new AlertDialogManager();
		
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String user = username.getText().toString();
				String pass = password.getText().toString();
				new ServerLoginTask(LoginActivity.this).execute(user,pass);
			}
			
		});
		
		create.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String user = username.getText().toString();
				String pass = password.getText().toString();
				String conf = confirm.getText().toString();
				
				// first to make sure formatting is correct
				if (!checkUsername(user)) {
					error.setText("Username invalid. Please choose a name with at least 3 characters " +
							"consisting of only alphanumeric characters or underscores.");
					username.setText("");
				} else if (!pass.equals(conf)) {
					error.setText("Passwords do not match.");
					password.setText("");
					confirm.setText("");
				} else if (! checkPassword(pass)) {
					error.setText("Password invalid. Please choose a password at least 5 characters long.");
					password.setText("");
					password.setText("");
				} else {
					new ServerCreateAccountTask(LoginActivity.this).execute(user,pass);
				}
			}
			
		});
	}
	
	protected boolean checkPassword(String pass) {
		if (pass.length() < 5) return false;
		return true;
	}

	protected boolean checkUsername(String user) {
		if (user.length() < 3) return false;
		for (char c : user.toCharArray()) {
			if (! Constants.acceptedUserChars.contains(String.valueOf(c))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Show loading image, make fields not editable
	 */
	public void loading() {
		loading.setImageResource(R.drawable.loading);
		error.setText("");
		// make sure user cannot edit fields or resubmit during loading
		username.setFocusable(false);
		password.setFocusable(false);
		confirm.setFocusable(false);
		submit.setClickable(false);
		create.setClickable(false);
	}
	
	/**
	 * Performed when server login task is complete.
	 * Removes loading image and processes result.
	 */
	public void loginTaskDone(String result) {
		if (result.startsWith("authenticated")) {
			Integer id = Integer.parseInt(result.split(":")[1]);
			manager.createLoginSession(username.getText().toString(),id);
			loginRedirect();
			return;
		}
		
		// login failed, make fields re-editable
		username.setFocusable(true);
		password.setFocusable(true);
		confirm.setFocusable(true);
		username.setFocusableInTouchMode(true);
		password.setFocusableInTouchMode(true);
		confirm.setFocusableInTouchMode(true);
		submit.setClickable(true);
		create.setClickable(true);

		error.setText("Incorrect username or password");
		
		loading.setImageResource(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	/**
	 * On successful login, redirects to main page
	 */
	public void loginRedirect() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
	}

	public void createTaskDone(String result) {
		if (result.startsWith("success")) {
			Integer id = Integer.parseInt(result.split(":")[1]);
			manager.createLoginSession(username.getText().toString(),id);
			loginRedirect();
			return;
		}
		
		// create failed, make fields re-editable
		username.setFocusable(true);
		password.setFocusable(true);
		confirm.setFocusable(true);
		username.setFocusableInTouchMode(true);
		password.setFocusableInTouchMode(true);
		confirm.setFocusableInTouchMode(true);
		submit.setClickable(true);
		create.setClickable(true);
		
		/*
		 * Error Codes:
		 * 0: User already exists
		 * 1: query error (server problem)
		 * 4: not enough arguments passed (shouldn't ever happen)
		 */
		
		if(result.startsWith("0")) {
			error.setText("Username already exists.");
			username.setText("");
		} else {
			alert.showAlertDialog(getApplicationContext(), "Server Error", "Error contacting server, " +
					"please try again soon or contact us at "+getString(R.string.contact_email)+
					" to report the problem", null);
		}
		
		loading.setImageResource(0);
		
	}

}
