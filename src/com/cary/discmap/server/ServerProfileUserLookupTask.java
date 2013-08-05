package com.cary.discmap.server;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.cary.discmap.Constants;
import com.cary.discmap.UserActivity;

public class ServerProfileUserLookupTask extends AsyncTask<Integer,Void,String> {
	
	private UserActivity parent;
	
	public ServerProfileUserLookupTask(UserActivity parent) {
		this.parent = parent;
	}
	
	@Override
	protected void onPreExecute() {
		parent.loading();
	}

	@Override
	protected String doInBackground(Integer... params) {
		final String user = String.valueOf(params[0]);
		
		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"users.php/user/"+user);
		
		return poster.execute(new ArrayList<BasicNameValuePair>());
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(!isCancelled()) parent.userNameLoaded(result);
	}
}
