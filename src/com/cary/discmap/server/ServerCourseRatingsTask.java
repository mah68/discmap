package com.cary.discmap.server;

import android.os.AsyncTask;
import android.util.Log;

import com.cary.discmap.Constants;
import com.cary.discmap.CourseActivity;

public class ServerCourseRatingsTask extends AsyncTask<Integer,Void,String> {
	
	private CourseActivity parent;
	
	public ServerCourseRatingsTask(CourseActivity parent) {
		this.parent = parent;
	}
	
	@Override
	protected void onPreExecute() {
		parent.loading();
	}

	@Override
	protected String doInBackground(Integer... params) {
		final String course = String.valueOf(params[0]);
		
		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"comments.php/getratings/"+course);
		return poster.execute();
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(!isCancelled()) parent.courseRatingsLoaded(result);
	}
}
