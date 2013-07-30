package com.cary.discmap.server;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.cary.discmap.Constants;
import com.cary.discmap.CourseActivity;

public class ServerCourseInfoTask extends AsyncTask<Integer,Void,String> {
	
	private CourseActivity parent;
	
	public ServerCourseInfoTask(CourseActivity parent) {
		this.parent = parent;
	}
	
	@Override
	protected void onPreExecute() {
		parent.loading();
	}

	@Override
	protected String doInBackground(Integer... params) {
		final String course = String.valueOf(params[0]);
		
		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"courses.php/course");
		
		return poster.execute(new ArrayList<BasicNameValuePair>() {{
			add(new BasicNameValuePair("course",course));
		}});
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(!isCancelled()) parent.courseInfoLoaded(result);
	}
}
