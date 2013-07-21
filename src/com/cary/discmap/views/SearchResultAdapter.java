package com.cary.discmap.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cary.discmap.CourseActivity;
import com.cary.discmap.R;
import com.cary.discmap.server.ServerSearchRatingTask;

public class SearchResultAdapter extends ArrayAdapter<SearchResult> {
	Context context; 
    int layoutResourceId;    
    SearchResult data[] = null;
    
    public SearchResultAdapter(Context context, int layoutResourceId, SearchResult[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ResultHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ResultHolder();
            holder.course = (TextView) row.findViewById(R.id.searchResultCourseName);
            holder.rating = (TextView) row.findViewById(R.id.searchResultRating);
            holder.layout = (LinearLayout) row.findViewById(R.id.searchResultLayout);
            
            row.setTag(holder);
        }
        else
        {
            holder = (ResultHolder) row.getTag();
        }
        
        SearchResult result = data[position];
        // another async task to lookup rating
        new ServerSearchRatingTask(holder.rating).execute(result.course_id);
        holder.course.setText(result.course);
        
        final LinearLayout layout = holder.layout;
        layout.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, CourseActivity.class);
				i.putExtra("course", 1);
				context.startActivity(i);
			}
        	
        });
        
        return row;
    }
    
    static class ResultHolder
    {
        TextView course;
        TextView rating;
        LinearLayout layout;
    }
}
