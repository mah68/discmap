package com.cary.discmap;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cary.discmap.server.ServerProfileScorecardsTask;
import com.cary.discmap.server.ServerProfileUserLookupTask;
import com.cary.discmap.views.Scorecard;
import com.cary.discmap.views.ScorecardAdapter;

public class UserActivity extends DiscActivity {
	ListView scoresList;
	TextView userName;
	EditText refineCourse;
	
	ArrayList<Scorecard> scorecards;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SessionManager.get(getApplicationContext()).checkLogin();
		setContentView(R.layout.activity_user);

		scorecards = new ArrayList<Scorecard>();
		scoresList = (ListView) findViewById(R.id.profileScoresListView);
		userName = (TextView) findViewById(R.id.profileUserTextView);
		refineCourse = (EditText) findViewById(R.id.profileRefineCourse);
		refineCourse.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				filterByCourse(refineCourse.getText().toString());
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			
		});

		Intent i = getIntent();
		int user = i.getIntExtra("user", 0);
		new ServerProfileScorecardsTask(this).execute(user);
		new ServerProfileUserLookupTask(this).execute(user);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user, menu);
		return true;
	}

	public void loading() {
		// TODO
	}
	
	public void filterByCourse(String filter) {
		ArrayList<Scorecard> filteredScores = new ArrayList<Scorecard>();
		for (Scorecard s : scorecards) {
			if (s.containsCourse(filter)) {
				filteredScores.add(s);
			}
		}
		scoresList.setAdapter(new ScorecardAdapter(this,
				R.layout.scorecard_listitem, filteredScores
						.toArray(new Scorecard[filteredScores.size()])));
	}

	public void scorecardsLoaded(String json) {
		if (!json.startsWith("[")) {
			networkAlertDialog();
			return;
		}
		scorecards = new ArrayList<Scorecard>();
		try {
			JSONArray list = new JSONArray(json);
			for (int i = 0; i < list.length(); i++) {
				JSONObject data = list.getJSONObject(i);
				String timeStamp = data.getString("time_played");
				int[] scores = new int[18];
				for (int j = 1; j <= 18; j++) {
					scores[j - 1] = data.getInt("hole" + j);
				}
				int courseId = data.getInt("course_id");
				String courseName = data.getString("course_name");

				scorecards.add(new Scorecard(scores, courseId, courseName,
						timeStamp));
			}
		} catch (JSONException e) {
			alertDialog("Data Error", getString(R.string.JSON_error_message)
					+ " Error Message:" + e.getMessage());
		}

		scoresList.setAdapter(new ScorecardAdapter(this,
				R.layout.scorecard_listitem, scorecards
						.toArray(new Scorecard[scorecards.size()])));

	}

	public void userNameLoaded(String user) {
		//TODO: check for errors in query, such as user does not exist
		userName.setText(user);
	}
}
