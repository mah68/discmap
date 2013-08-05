package com.cary.discmap.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

public class HeightGridView extends GridView {

    public HeightGridView(Context context) {
		super(context);
	}
    public HeightGridView(Context context,AttributeSet attrs) {
		super(context,attrs);
	}
    public HeightGridView(Context context,AttributeSet attrs,int i) {
		super(context,attrs,i);
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Calculate entire height by providing a very large height hint.
        // But do not use the highest 2 bits of this integer; those are
        // reserved for the MeasureSpec mode.
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
    
}
