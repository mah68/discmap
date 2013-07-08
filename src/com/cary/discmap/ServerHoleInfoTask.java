package com.cary.discmap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class ServerHoleInfoTask extends AsyncTask<Integer,Void,String> {
	
	private HoleActivity parent;
	
	public ServerHoleInfoTask(HoleActivity parent) {
		this.parent = parent;
	}
	
	@Override
	protected void onPreExecute() {
		parent.loading();
	}

	@Override
	protected String doInBackground(Integer... params) {
		AlertDialogManager alert = new AlertDialogManager();
		final String course = String.valueOf(params[0]);
		final String hole = String.valueOf(params[1]);
		
		String result = null;
		InputStream is = null;
		StringBuilder sb = null;
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.serverURL+"index.php/hole");
			httppost.setEntity(new UrlEncodedFormEntity(new ArrayList<BasicNameValuePair>() {{
				add(new BasicNameValuePair("course",course));
				add(new BasicNameValuePair("hole",hole));
			}}));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
			alert.showAlertDialog(parent.getApplicationContext(), "Network error", "Error connecting to server. " +
					"Please check your network connection and try again.", null);
		}
		
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            sb = new StringBuilder();
            sb.append(reader.readLine() + "\n");
            String line="0";
          
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
             
            is.close();
            result=sb.toString();

		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		
		if (result == null) result = "";
		return result;	
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		parent.holeInfoLoaded(result);
	}
}
