package com.cary.discmap.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareLayout extends LinearLayout {

	public SquareLayout(Context context) {
		super(context);
	}
	
	public SquareLayout(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	@Override
	public void onMeasure(int width, int height) {
	    super.onMeasure(width, width);
	}

}
