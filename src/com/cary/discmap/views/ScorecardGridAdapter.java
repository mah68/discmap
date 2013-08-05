package com.cary.discmap.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.cary.discmap.R;

public class ScorecardGridAdapter extends BaseAdapter {
	private Context context;
	private int[] scores;

	public ScorecardGridAdapter(Context context, int[] scores) {
		this.context = context;
		this.scores = scores;
	}

	@Override
	public int getCount() {
		return 36;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView;
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			textView = new TextView(context);
			textView.setLayoutParams(new GridView.LayoutParams(25, 25));
		} else {
			textView = (TextView) convertView;
		}
		if (position < 18)
			textView.setText(String.valueOf(position+1));
		else {
			position -= 18;
			textView.setTypeface(null, Typeface.BOLD);
			if (scores[position] > 0) {
				textView.setTextColor(context.getResources().getColor(
						R.color.red));
				textView.setText("+" + String.valueOf(scores[position]));
			} else {
				if (scores[position] < 0)
					textView.setTextColor(context.getResources().getColor(
							R.color.blue));
				textView.setText(String.valueOf(scores[position]));
			}
		}
		return textView;
	}

}
