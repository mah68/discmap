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

public class ServerHoleInfoTask extends AsyncTask<Integer,Void,String> {
	
	private HoleActivity parent;
	
	public ServerHoleInfoTask(HoleActivity parent) {
		this.parent = parent;
	}
	
	@Override
	protected void onPreExecute() {
		parent.loading();
	}

	@Override
	protected String doInBackground(Integer... params) {
		final String course = String.valueOf(params[0]);
		final String hole = String.valueOf(params[1]);
		
		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"courses.php/hole");
		
		return poster.execute(new ArrayList<BasicNameValuePair>() {{
			add(new BasicNameValuePair("course",course));
			add(new BasicNameValuePair("hole",hole));
		}});
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		parent.holeInfoLoaded(result);
	}
}
