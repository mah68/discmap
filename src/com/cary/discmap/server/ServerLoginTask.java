package com.cary.discmap.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.cary.discmap.Constants;
import com.cary.discmap.LoginActivity;

import android.os.AsyncTask;
import android.util.Log;

public class ServerLoginTask extends AsyncTask<String, Void, String> {
	
	private LoginActivity parent;
	
	public ServerLoginTask(LoginActivity parent) {
		this.parent = parent;
	}
	
	@Override
	protected void onPreExecute() {
		parent.loading();
	}

	@Override
	protected String doInBackground(String... params) {
		final String user = params[0];
		final String pass = params[1];

		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"users.php/login");
		
		return poster.execute(new ArrayList<BasicNameValuePair>() {{
			add(new BasicNameValuePair("user",user));
			add(new BasicNameValuePair("password",pass));
		}});
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		parent.loginTaskDone(result);
	}

}
