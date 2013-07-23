package com.cary.discmap.views;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cary.discmap.AlertDialogManager;
import com.cary.discmap.HoleActivity;
import com.cary.discmap.R;
import com.cary.discmap.SessionManager;
import com.cary.discmap.server.ServerCourseHolesTask;
import com.cary.discmap.server.ServerMapHoleTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class CoordMapper extends LinearLayout implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	public static final float MAX_ACCURACY = 3; // in meters

	private float[] hole;
	private float[] tee;
	private HoleActivity parent;
	private LocationClient mClient;
	private boolean isConnected;
	private AlertDialogManager alert;

	Button teeMapper;
	Button holeMapper;
	Button reset;
	Button submit;

	public CoordMapper(Context context, final HoleActivity parent) {
		super(context);
		this.tee = null;
		this.hole = null;
		this.parent = parent;
		isConnected = false;
		alert = new AlertDialogManager();

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.map_hole, this, true);

		teeMapper = (Button) findViewById(R.id.teeMappingButton);
		holeMapper = (Button) findViewById(R.id.holeMappingButton);
		reset = (Button) findViewById(R.id.resetMappingButton);
		submit = (Button) findViewById(R.id.submitMappingButton);

		updateView();

		mClient = new LocationClient(parent, this, this);
		mClient.connect();

		teeMapper.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Location loc = mClient.getLastLocation();
				if (loc != null) {
					if (loc.getAccuracy() > MAX_ACCURACY) {
						lowAccuracyMessage();
						return;
					}
					tee = new float[2];
					tee[0] = (float) loc.getLatitude();
					tee[1] = (float) loc.getLongitude();
				} else {
					// TODO null behavior
				}
				updateView();
			}

		});

		holeMapper.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Location loc = mClient.getLastLocation();
				if (loc.getAccuracy() > MAX_ACCURACY) {
					lowAccuracyMessage();
					return;
				}
				if (loc != null) {
					hole = new float[2];
					hole[0] = (float) loc.getLatitude();
					hole[1] = (float) loc.getLongitude();
				} else {
					// TODO null behavior
				}
				updateView();
			}

		});

		reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tee = null;
				hole = null;
				updateView();
			}

		});

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new ServerMapHoleTask(CoordMapper.this).execute(String
						.valueOf(tee[0]), String.valueOf(tee[1]), String
						.valueOf(hole[0]), String.valueOf(hole[1]), String
						.valueOf(parent.getCourseId()), String.valueOf(parent
						.getHoleNumber()), String.valueOf(SessionManager.get(
						getContext()).getId()));
			}

		});

	}

	private void lowAccuracyMessage() {
		alert.showAlertDialog(
				parent,
				"Mapping Error",
				"Your GPS location accuracy is too low to map this hole. Try editing your device's GPS settings," +
				" or wait until you're in a better reception area.",
				null);
	}

	public void loading() {
		// TODO loading behavior after submit
	}

	public void onHoleMapped(String result) {
		// TODO end loading behavior

		if (result.equals("success")) {
			new ServerCourseHolesTask(parent).execute(parent.getCourseId());
		} else {
			// TODO error behavior
		}
	}

	/**
	 * Enable or disable buttons based on whether or not values are set
	 */
	private void updateView() {
		teeMapper.setEnabled((tee == null) ? true : false);
		holeMapper.setEnabled((hole == null) ? true : false);
		submit.setEnabled(((tee != null) && (hole != null)) ? true : false);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO on failed
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		isConnected = true;
	}

	@Override
	public void onDisconnected() {
		isConnected = false;
	}

}
