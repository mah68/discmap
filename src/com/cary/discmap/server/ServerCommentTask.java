package com.cary.discmap.server;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

import com.cary.discmap.Constants;
import com.cary.discmap.CourseActivity;

public class ServerCommentTask extends AsyncTask<String, Void, String> {
	CourseActivity parent;
	
	public ServerCommentTask(CourseActivity parent) {
		this.parent = parent;
	}
	
	@Override
	protected void onPreExecute() {
		parent.loading();
	}

	@Override
	protected String doInBackground(String... params) {
		final String user = params[0];
		final String course = params[1];
		final String comment = params[2];

		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"comments.php/comment");
		
		return poster.execute(new ArrayList<BasicNameValuePair>() {{
			add(new BasicNameValuePair("user",user));
			add(new BasicNameValuePair("course",course));
			add(new BasicNameValuePair("comment",comment));
		}});
	}
	
	@Override
	protected void onPostExecute(String result) {
		new ServerCourseCommentsTask(parent).execute(parent.getId());
	}

}
