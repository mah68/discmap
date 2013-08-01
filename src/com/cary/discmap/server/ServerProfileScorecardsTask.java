package com.cary.discmap.server;

import android.os.AsyncTask;

import com.cary.discmap.Constants;
import com.cary.discmap.UserActivity;

public class ServerProfileScorecardsTask extends AsyncTask<Integer,Void,String> {
	
	private UserActivity parent;
	
	public ServerProfileScorecardsTask(UserActivity parent) {
		this.parent = parent;
	}
	
	@Override
	protected void onPreExecute() {
		parent.loading();
	}

	@Override
	protected String doInBackground(Integer... params) {
		final String user = String.valueOf(params[0]);
		
		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"scorecards.php/getscorecards/"+user);
		return poster.execute();
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(!isCancelled()) parent.scorecardsLoaded(result);
	}
}
