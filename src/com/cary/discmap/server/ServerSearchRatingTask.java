package com.cary.discmap.server;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.widget.TextView;

import com.cary.discmap.Constants;

public class ServerSearchRatingTask extends AsyncTask<Integer,Void,String> {
	
	private TextView ratingView;
	
	public ServerSearchRatingTask(TextView rating) {
		this.ratingView = rating;
	}
	
	@Override
	protected void onPreExecute() {
		ratingView.setText("N/A");
	}

	@Override
	protected String doInBackground(Integer... params) {
		final Integer course = params[0];
		
		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"comments.php/getratings/"+course);
		return poster.execute();
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		try {
			JSONArray j = new JSONArray(result);
			ratingView.setText(j.getString(0));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
