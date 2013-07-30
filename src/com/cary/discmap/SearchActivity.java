package com.cary.discmap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cary.discmap.server.ServerSearchCoursesTask;
import com.cary.discmap.views.SearchResult;
import com.cary.discmap.views.SearchResultAdapter;

public class SearchActivity extends DiscActivity {

	private EditText searchBar;
	private ListView results;
	private AsyncTask searchTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SessionManager.get(getApplicationContext()).checkLogin();
		setContentView(R.layout.activity_search);

		searchBar = (EditText) findViewById(R.id.searchEditText);
		results = (ListView) findViewById(R.id.searchResultsListView);
		
		searchBar.setOnEditorActionListener(
		        new EditText.OnEditorActionListener() {
		            @Override
		            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		                    search();
		                    return true;
		                }
		                return false;
		            }
		        });
		
		searchBar.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				search();
			}
			
		});

	}

	private void search() {
		String search = searchBar.getText().toString();
		if (!search.equals("")) {
			if(searchTask != null) {
				searchTask.cancel(true);
			}
			searchTask = new ServerSearchCoursesTask(SearchActivity.this)
					.execute(search);
		}
	}
	
	public void loading() {
		// TODO loading behavior
	}

	public void resultsLoaded(String json) {
		if (!json.startsWith("["))  {
			networkAlertDialog();
			return;
		}
		List<SearchResult> resultList = new ArrayList<SearchResult>();
		try {
			JSONArray jarray = new JSONArray(json);
			for (int i = 0; i < jarray.length(); i++) {
				JSONObject result = jarray.getJSONObject(i);
				SearchResult r = new SearchResult(result.getString("course_name"),
						result.getInt("course_id"));
				resultList.add(r);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		results.setAdapter(new SearchResultAdapter(this,
				R.layout.search_result, resultList
						.toArray(new SearchResult[resultList.size()])));
		if (resultList.size() == 0 ) {
			//TODO Add no results/add course button
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

}
