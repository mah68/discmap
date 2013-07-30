package com.cary.discmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cary.discmap.views.CoordMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class HoleActivity extends DiscActivity implements
		HoleLoader {

	private GoogleMap map;
	private float[] tee;
	private float[] hole;
	private String lastEdited;
	int editedBy;
	private String courseName;
	private int courseId;
	private int holeNum;
	private String holesJSON;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SessionManager.get(getApplicationContext()).checkLogin();
		setContentView(R.layout.activity_hole);
		map = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.holeMap)).getMap();

		Intent i = getIntent();
		courseId = i.getIntExtra("course", 0);
		courseName = i.getStringExtra("courseName");
		((TextView) findViewById(R.id.holeCourseTitleView)).setText(courseName);
		holesJSON = i.getStringExtra("holesJSON");

		init(i.getIntExtra("hole", 0));

	}

	private void init(final int holeNum) {
		this.holeNum = holeNum;
		// reset tee and hole, mostly for mapping purposes
		// null means location has not been mapped
		tee = null;
		hole = null;

		((LinearLayout) findViewById(R.id.mappingHoleContainer)).removeAllViews();
		((TextView) findViewById(R.id.holeNumberView)).setText("Hole #"
				+ holeNum);

		try {
			JSONArray holes = new JSONArray(holesJSON);
			boolean mapData = false;
			for (int j = 0; j < holes.length(); j++) {
				JSONObject data = holes.getJSONObject(j);
				if (data.getInt("hole_number") == holeNum) {
					holeInfoLoaded(data);
					mapData = true;
					break;
				}
			}
			if (!mapData)
				noHoleData();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Button prev = (Button) findViewById(R.id.holePrevButton);
		Button next = (Button) findViewById(R.id.holeNextButton);
		prev.setEnabled((holeNum == 1) ? false : true);
		next.setEnabled((holeNum == 18) ? false : true);

		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				init(holeNum + 1);
			}
		});

		prev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				init(holeNum - 1);
			}
		});
	}

	private void noHoleData() {
		map.clear();
		map.setMapType(GoogleMap.MAP_TYPE_NONE);

		LinearLayout mappingContainer = (LinearLayout) findViewById(R.id.mappingHoleContainer);
		View mappingView = new CoordMapper(getApplicationContext(), this);
		mappingContainer.addView(mappingView);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hole, menu);
		return true;
	}

	public void updateMap() {
		LatLng teeCoords = new LatLng(tee[0], tee[1]);
		LatLng holeCoords = new LatLng(hole[0], hole[1]);
		map.clear();
		BitmapDescriptor holeDescriptor = BitmapDescriptorFactory
				.fromAsset("hole_marker.png");
		map.addMarker(new MarkerOptions().position(teeCoords));
		map.addMarker(new MarkerOptions().position(holeCoords).icon(
				holeDescriptor));
		map.addPolyline(new PolylineOptions().add(teeCoords, holeCoords)
				.width(Constants.LINE_WIDTH).color(Constants.LINE_COLOR));

		LatLng neBound = new LatLng(Math.max(tee[0], hole[0]), Math.max(tee[1],
				hole[1]));
		LatLng swBound = new LatLng(Math.min(tee[0], hole[0]), Math.min(tee[1],
				hole[1]));
		final LatLngBounds bounds = new LatLngBounds(swBound, neBound);
		try {
			// trycatch in case map hasn't been laid out
			map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 75));
		} catch (IllegalStateException e) {
			map.setOnCameraChangeListener(new OnCameraChangeListener() {

				@Override
				public void onCameraChange(CameraPosition arg0) {
					map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
							75));
					map.setOnCameraChangeListener(null);
				}
			});
		}
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	}

	public void loading() {
		// TODO: EXECUTE LOADING BEHAVIOR
	}

	public void holeInfoLoaded(JSONObject data) {
		
		try {
			tee = new float[2];
			hole = new float[2];
			tee[0] = Float.valueOf(data.getString("tee_lat"));
			tee[1] = Float.valueOf(data.getString("tee_lon"));
			hole[0] = Float.valueOf(data.getString("hole_lat"));
			hole[1] = Float.valueOf(data.getString("hole_lon"));

			lastEdited = data.getString("last_edited");
			editedBy = Integer.parseInt(data.getString("edited_by"));

			updateMap();

		} catch (JSONException e) {
			alertDialog("Data Error", getString(R.string.JSON_error_message) + " Error Message:"+e.getMessage());
		}

	}
	
	public int getHoleNumber() {
		return holeNum;
	}
	
	public int getCourseId() {
		return courseId;
	}

	@Override
	public void courseHolesLoaded(String json) {
		if (!json.startsWith("["))  {
			networkAlertDialog();
			return;
		}
		holesJSON = json;
		init(holeNum);
	}

}
