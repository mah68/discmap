package com.cary.discmap.server;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.cary.discmap.Constants;
import com.cary.discmap.SearchActivity;

public class ServerSearchCoursesTask extends AsyncTask<String,Void,String> {
	
	private SearchActivity parent;
	
	public ServerSearchCoursesTask(SearchActivity parent) {
		this.parent = parent;
	}
	
	@Override
	protected void onPreExecute() {
		parent.loading();
	}

	@Override
	protected String doInBackground(String... params) {
		final String search = params[0];
		
		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"search.php/searchcourses");
		
		return poster.execute(new ArrayList<BasicNameValuePair>() {{
			add(new BasicNameValuePair("search",search));
		}});
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		parent.resultsLoaded(result);
	}
}
