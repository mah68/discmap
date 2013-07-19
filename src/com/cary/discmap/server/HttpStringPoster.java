package com.cary.discmap.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class HttpStringPoster {
	String url;
	
	public HttpStringPoster(String url) {
		this.url = url;
	}
	
	public String execute(List<BasicNameValuePair> pairs) {
		String result = null;
		InputStream is = null;
		StringBuilder sb = null;
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
			return "Network Error";
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
            result = result.trim(); // remove extra newline if needed

		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
			return "Error";
		}
		
		if (result == null) result = "";
		return result;	
	}
	
	public String execute() {
		return execute(new ArrayList<BasicNameValuePair>());
	}

}
