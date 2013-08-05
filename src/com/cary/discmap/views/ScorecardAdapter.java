package com.cary.discmap.views;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cary.discmap.CourseActivity;
import com.cary.discmap.R;

public class ScorecardAdapter extends ArrayAdapter<Scorecard> {
	Context context; 
    int layoutResourceId;    
    Scorecard data[] = null;
    
    public ScorecardAdapter(Context context, int layoutResourceId, Scorecard[] data) {
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
            holder.course = (TextView) row.findViewById(R.id.scorecardCourseName);
            holder.date = (TextView) row.findViewById(R.id.scorecardDate);
            holder.overallScore = (TextView) row.findViewById(R.id.scorecardOverallScore);
            holder.container = (LinearLayout) row.findViewById(R.id.scorecardContainer);
            holder.expand = (Button) row.findViewById(R.id.scorecardExpandButton);
            
//            LinearLayout fullContainer = (LinearLayout) row.findViewById(R.id.scorecardListItemContainer);
//            LayoutTransition transition  = fullContainer.getLayoutTransition();
//            transition.enableTransitionType(LayoutTransition.CHANGING);
            
            row.setTag(holder);
        }
        else
        {
            holder = (ResultHolder) row.getTag();
        }
        
        final Scorecard result = data[position];

        holder.course.setText(result.courseName);
        holder.course.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context.getApplicationContext(), CourseActivity.class);
				i.putExtra("course", result.courseId);
				context.startActivity(i);
			}
        	
        });
        
        holder.date.setText(result.timeStamp);
        if(result.overallScore > 0) {
        	holder.overallScore.setTextColor(context.getResources().getColor(R.color.red));
            holder.overallScore.setText("+"+String.valueOf(result.overallScore));
        } else {
        	if(result.overallScore < 0) 
            	holder.overallScore.setTextColor(context.getResources().getColor(R.color.blue));
        	if(result.overallScore == 0)
            	holder.overallScore.setTextColor(context.getResources().getColor(R.color.black));
            	
            holder.overallScore.setText(String.valueOf(result.overallScore));
        }
        
        holder.expand.setOnClickListener(new ExpandOnClickListener(holder.container,holder.expand,result.scores));
        
        return row;
    }
    
    static class ResultHolder
    {
        TextView course;
        TextView overallScore;
        TextView date;
        LinearLayout container;
        Button expand;
    }
    
    private class ExpandOnClickListener implements OnClickListener {
    	private LinearLayout container;
    	private Button expandButton;
    	private int[] scores;
    	
    	public ExpandOnClickListener(LinearLayout container, Button expand, int[] scores) {
    		this.container = container;
    		this.expandButton = expand;
    		this.scores = scores;
    	}

		@Override
		public void onClick(View v) {
			expandButton.setEnabled(false);
			View child = LayoutInflater.from(context).inflate(R.layout.scorecard_fullview,
	                null);
			GridView grid = (GridView) child.findViewById(R.id.scorecardGridView);
			grid.setAdapter(new ScorecardGridAdapter(context,scores));
			container.addView(child);
			Animation slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
			child.startAnimation(slideIn);
			expandButton.setOnClickListener(new ContractOnClickListener(container,expandButton,scores));
			expandButton.setEnabled(true);
		}
    	
    }
    
    private class ContractOnClickListener implements OnClickListener {
    	private LinearLayout container;
    	private Button expandButton;
    	private int[] scores;
    	
    	public ContractOnClickListener(LinearLayout container, Button expand, int[] scores) {
    		this.container = container;
    		this.expandButton = expand;
    		this.scores = scores;
    	}

		@Override
		public void onClick(View v) {
			expandButton.setEnabled(false);
			View child = container.findViewById(R.id.scorecardGridView);
			Animation slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);
			slideOut.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation arg0) {
					container.removeAllViews();
					expandButton.setEnabled(true);
				}
				@Override
				public void onAnimationRepeat(Animation arg0) {}
				@Override
				public void onAnimationStart(Animation arg0) {}
			});
			child.startAnimation(slideOut);
			expandButton.setOnClickListener(new ExpandOnClickListener(container,expandButton,scores));
		}
    	
    }
}
