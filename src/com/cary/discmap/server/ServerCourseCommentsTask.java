package com.cary.discmap.server;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.cary.discmap.Constants;
import com.cary.discmap.CourseActivity;

public class ServerCourseCommentsTask extends AsyncTask<Integer,Void,String> {
	
	private CourseActivity parent;
	
	public ServerCourseCommentsTask(CourseActivity parent) {
		this.parent = parent;
	}
	
	@Override
	protected void onPreExecute() {
		parent.loading();
	}

	@Override
	protected String doInBackground(Integer... params) {
		final String course = String.valueOf(params[0]);
		
		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"comments.php/getcomments/"+course);
		return poster.execute();
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(!isCancelled()) parent.courseCommentsLoaded(result);
	}
}
