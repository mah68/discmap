package com.cary.discmap;

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

import android.os.AsyncTask;
import android.util.Log;

public class ServerCreateAccountTask extends AsyncTask<String, Void, String> {
	LoginActivity parent;
	
	public ServerCreateAccountTask(LoginActivity parent) {
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
		
		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"users.php/create");
		
		return poster.execute(new ArrayList<BasicNameValuePair>() {{
			add(new BasicNameValuePair("user",user));
			add(new BasicNameValuePair("password",pass));
		}});
	}
	
	@Override
	protected void onPostExecute(String result) {
		parent.createTaskDone(result);
	}

}
