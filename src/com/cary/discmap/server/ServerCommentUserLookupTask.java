package com.cary.discmap.server;

import android.os.AsyncTask;
import android.widget.TextView;

import com.cary.discmap.Constants;

public class ServerCommentUserLookupTask extends AsyncTask<Integer,Void,String> {
	
	private TextView userView;
	
	public ServerCommentUserLookupTask(TextView user) {
		this.userView = user;
	}
	
	@Override
	protected void onPreExecute() {
		userView.setText("loading user");
	}

	@Override
	protected String doInBackground(Integer... params) {
		final Integer user = params[0];
		
		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"users.php/user/"+user);
		return poster.execute();
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		userView.setText(result);
	}
}
