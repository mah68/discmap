package com.cary.discmap.server;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.cary.discmap.Constants;
import com.cary.discmap.views.CoordMapper;

public class ServerMapHoleTask extends AsyncTask<String, Void, String> {
	CoordMapper parent;
	
	public ServerMapHoleTask(CoordMapper parent) {
		this.parent = parent;
	}
	
	@Override
	protected void onPreExecute() {
		parent.loading();
	}

	@Override
	protected String doInBackground(String... params) {
		final String tee_lat = params[0];
		final String tee_lon = params[1];
		final String hole_lat = params[2];
		final String hole_lon = params[3];
		final String course_id = params[4];
		final String hole_number = params[5];
		final String posted_by = params[6];

		HttpStringPoster poster = new HttpStringPoster(Constants.serverURL+"courses.php/maphole");
		
		return poster.execute(new ArrayList<BasicNameValuePair>() {{
			add(new BasicNameValuePair("tee_lat",tee_lat));
			add(new BasicNameValuePair("tee_lon",tee_lon));
			add(new BasicNameValuePair("hole_lat",hole_lat));
			add(new BasicNameValuePair("hole_lon",hole_lon));
			add(new BasicNameValuePair("course_id",course_id));
			add(new BasicNameValuePair("hole_number",hole_number));
			add(new BasicNameValuePair("posted_by",posted_by));
		}});
	}
	
	@Override
	protected void onPostExecute(String result) {
		parent.onHoleMapped(result);
	}

}
