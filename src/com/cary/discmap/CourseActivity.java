package com.cary.discmap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cary.discmap.server.ServerCommentTask;
import com.cary.discmap.server.ServerCourseCommentsTask;
import com.cary.discmap.server.ServerCourseHolesTask;
import com.cary.discmap.server.ServerCourseInfoTask;
import com.cary.discmap.server.ServerCourseRatingsTask;
import com.cary.discmap.views.Comment;
import com.cary.discmap.views.CommentAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class CourseActivity extends DiscActivity implements HoleLoader {
	
	private TextView courseNameView;
	private TextView rating;
	private TextView numRatings;
	private ListView commentList;
	private EditText addComment;
	private Button playCourse;
	private Button submitComment;
	
	private Integer courseId;
	private String holesJSON;
	private String courseName;
	
	private List<AsyncTask> activeTasks;
	
	private GoogleMap map;
	private float[] neBound;
	private float[] swBound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SessionManager.get(getApplicationContext()).checkLogin();
		setContentView(R.layout.activity_course);
		
		courseNameView = (TextView) findViewById(R.id.courseNameTextView);
		rating = (TextView) findViewById(R.id.courseRatingTextView);
		numRatings = (TextView) findViewById(R.id.courseNumRatingsTextView);
		commentList = (ListView) findViewById(R.id.commentsListView);
		addComment = (EditText) findViewById(R.id.commentEditText);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.courseMap)).getMap();
		
		Intent i = getIntent();
		courseId = i.getIntExtra("course", 0);
		
		submitComment = (Button) findViewById(R.id.commentSubmit);
		submitComment.setEnabled(false); // only enable when comment is typed
		submitComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String comment = addComment.getText().toString();
				String user = String.valueOf(SessionManager.get(getApplicationContext()).getId());
				String course = String.valueOf(getId());
				
				new ServerCommentTask(CourseActivity.this).execute(user,course,comment);
				
				addComment.setText("");
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(addComment.getWindowToken(), 0);
			}
		});
		
		// disable submit if there is no comment
		addComment.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				submitComment.setEnabled((addComment.getText().toString().length() > 0) ? true : false); 
			}
		});
		
		playCourse = (Button) findViewById(R.id.playCourseButton);
		playCourse.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), HoleActivity.class);
				i.putExtra("course", courseId);
				i.putExtra("hole", 1);
				i.putExtra("courseName",courseName);
				i.putExtra("holesJSON", holesJSON);
				startActivity(i);
			}
			
		});
		// Don't enable button until holes and course are loaded
		playCourse.setEnabled(false);
		
		activeTasks = new ArrayList<AsyncTask>() {{
			add(new ServerCourseInfoTask(CourseActivity.this).execute(courseId));
			add(new ServerCourseHolesTask(CourseActivity.this).execute(courseId));
			add(new ServerCourseCommentsTask(CourseActivity.this).execute(courseId));
			add(new ServerCourseRatingsTask(CourseActivity.this).execute(courseId));
		}};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.course, menu);
		return true;
	}
	
	@Override
	public void loading() {
		//TODO loading behavior
	}
	
	public int getId() {
		return courseId;
	}
	
	public void courseInfoLoaded(String json) {
		if (!json.startsWith("{"))  {
			cancelTasks();
			networkAlertDialog();
			return;
		}
		try {
			JSONObject data = new JSONObject(json);
			courseName = data.getString("course_name");
			courseNameView.setText(courseName);
			updatePlayButtons();
			
		} catch (JSONException e) {
			alertDialog("Data Error", getString(R.string.JSON_error_message) + " Error Message:"+e.getMessage());
		}
	}
	
	private void cancelTasks() {
		for(int i=0; i<activeTasks.size();i++) {
			activeTasks.get(i).cancel(true);
		}
	}

	@Override
	public void courseHolesLoaded(String json) {
		if (!json.startsWith("["))  {
			cancelTasks();
			networkAlertDialog();
			return;
		}
		holesJSON = json;
		updatePlayButtons();
		try {
			JSONArray list = new JSONArray(json);
			if (list.length() == 0) return; // show no map if no map data
			boolean noMap = true; // set to remove map if necessary
			for (int i=0; i < list.length(); i++) {
				JSONObject data = list.getJSONObject(i);
				if( data.getString("tee_lon") != null) {
					noMap = false;
					float[] tee = new float[2];
					float[] hole = new float[2];
					tee[0] = Float.valueOf(data.getString("tee_lat"));
					tee[1] = Float.valueOf(data.getString("tee_lon"));
					hole[0] = Float.valueOf(data.getString("hole_lat"));
					hole[1] = Float.valueOf(data.getString("hole_lon"));
					String holeNum = data.getString("hole_number");
					mapHole(tee,hole,holeNum);
				}
			}
			if(!noMap) {
				setMapBounds();
			}
		} catch (JSONException e) {
			alertDialog("Data Error", getString(R.string.JSON_error_message) + " Error Message:"+e.getMessage());
		}
	}
	
	private void updatePlayButtons() {
		if((courseName != null) & (holesJSON != null)) {
			playCourse.setEnabled(true);
		} else { playCourse.setEnabled(false); }
	}

	public void courseCommentsLoaded(String json) {
		if (!json.startsWith("["))  {
			cancelTasks();
			networkAlertDialog();
			return;
		}
		ArrayList<Comment> comments = new ArrayList<Comment>();
		try {
			JSONArray list = new JSONArray(json);
			((TextView) findViewById(R.id.commentsTitleTextView)).setText(
					list.length()+" comments");
			for (int i=list.length()-1; i >= 0 ; i--) {
				JSONObject data = list.getJSONObject(i);
				String user = data.getString("user");
				String date = data.getString("post_time");
				String comment = data.getString("comment_text");
				
				comments.add(new Comment(user,date,comment));
			}
		} catch (JSONException e) {
			alertDialog("Data Error", getString(R.string.JSON_error_message) + " Error Message:"+e.getMessage());
		}
		
		commentList.setAdapter(new CommentAdapter(this,R.layout.comment,comments.toArray(new Comment[comments.size()])));
	}
	
	public void courseRatingsLoaded(String json) {
		if (!json.startsWith("["))  {
			cancelTasks();
			networkAlertDialog();
			return;
		}
		try {
			JSONArray list = new JSONArray(json);
			if (list.getInt(1) == 1) numRatings.setText("1 rating");
			else numRatings.setText(list.getInt(1)+" ratings");
			
			this.rating.setText(list.getString(0));
			
		} catch (JSONException e) {
			alertDialog("Data Error", getString(R.string.JSON_error_message) + " Error Message:"+e.getMessage());
		}
	}

	/**
	 * put each hole on the map, updating bound info if necessary
	 * @param tee
	 * @param hole
	 */
	private void mapHole(float[] tee, float[] hole, String holeNum) {
		LatLng teeCoords = new LatLng(tee[0], tee[1]);
		LatLng holeCoords = new LatLng(hole[0], hole[1]);
		BitmapDescriptor holeDescriptor = BitmapDescriptorFactory.fromAsset("hole_marker.png");
		map.addMarker(new MarkerOptions().position(holeCoords).icon(holeDescriptor));
		map.addMarker(new MarkerOptions().position(teeCoords).title("hole "+holeNum));
		map.addPolyline(new PolylineOptions().add(teeCoords,holeCoords)
				.width(Constants.LINE_WIDTH)
				.color(Constants.LINE_COLOR));
		
		//update bound info
		if(neBound == null) {
			neBound = new float[2];
			neBound[0] = Math.max(tee[0], hole[0]);
			neBound[1] = Math.max(tee[1], hole[1]);
		} else {
		neBound[0] = Math.max(neBound[0],Math.max(tee[0], hole[0]));
		neBound[1] = Math.max(neBound[1],Math.max(tee[1], hole[1]));
		}
		if (swBound == null) {
			swBound = new float[2];
			swBound[0] = Math.min(tee[0], hole[0]);
			swBound[1] = Math.min(tee[1], hole[1]);
		} else {
		swBound[0] = Math.min(swBound[0],Math.min(tee[0], hole[0]));
		swBound[1] = Math.min(swBound[1],Math.min(tee[1], hole[1]));
		}
	}

	/**
	 * Once all holes are mapped, set bounds
	 */
	private void setMapBounds() {
		if(map == null) return;
		LatLng ne = new LatLng(neBound[0],neBound[1]);
		LatLng sw = new LatLng(swBound[0],swBound[1]);
		LatLngBounds bounds = new LatLngBounds(sw, ne);

		map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
		map.setMyLocationEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

	}

}
