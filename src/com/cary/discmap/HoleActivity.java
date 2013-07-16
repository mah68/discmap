package com.cary.discmap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class HoleActivity extends Activity {
	
	private GoogleMap map;
	private float[] tee;
	private float[] hole;
	String lastEdited;
	int editedBy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hole);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.holeMap)).getMap();
		
		Intent i = getIntent();
		Integer course = i.getIntExtra("course", 0);
		Integer hole = i.getIntExtra("hole", 0);
		
		new ServerHoleInfoTask(this).execute(course, hole);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hole, menu);
		return true;
	}
	
	public void updateMap() {
		//TODO CHECK IF TEE DATA EXISTS
		
		LatLng teeCoords = new LatLng(tee[0], tee[1]);
		LatLng holeCoords = new LatLng(hole[0], hole[1]);
		map.addMarker(new MarkerOptions().position(teeCoords).title("tee"));
		map.addMarker(new MarkerOptions().position(holeCoords).title("hole"));
		
		LatLng neBound = new LatLng(Math.max(tee[0], hole[0]), Math.max(tee[1],hole[1]));
		LatLng swBound = new LatLng(Math.min(tee[0], hole[0]), Math.min(tee[1],hole[1]));
		
		map.addPolyline(new PolylineOptions().add(teeCoords,holeCoords)
				.width(Constants.LINE_WIDTH)
				.color(Constants.LINE_COLOR));
		
		LatLngBounds bounds = new LatLngBounds(swBound, neBound);
		map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 75));

	}
	
	public void loading() {
		//TODO: EXECUTE LOADING BEHAVIOR
	}
	
	public void holeInfoLoaded(String json) {
		//TODO: END LOADING BEHAVIOR
		
		try {
			JSONObject data = new JSONObject(json);
			
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
			Log.e("log_tag","Error converting to JSON "+e.getMessage());
		}
		
	}

}
