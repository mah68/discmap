package com.cary.discmap.server;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

import com.cary.discmap.Constants;
import com.cary.discmap.HoleLoader;

public class ServerCourseHolesTask extends AsyncTask<Integer,Void,String> {
	
	private HoleLoader parent;
	
	public ServerCourseHolesTask(HoleLoader parent) {
		this.parent = parent;
	}
	
	@Override
	protected void onPreExecute() {
		parent.loading();
	}

	@Override
	protected String doInBackground(Integer... params) {
		final String course = String.valueOf(params[0]);
		
		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"courses.php/allholes");
		
		return poster.execute(new ArrayList<BasicNameValuePair>() {{
			add(new BasicNameValuePair("course",course));
		}});
		
	}
	
	@Override
	protected void onCancelled() {
		Log.d("test","was cancelled");
	}
	@Override
	protected void onPostExecute(String result) {
		if(!isCancelled()) parent.courseHolesLoaded(result);
	}
}
