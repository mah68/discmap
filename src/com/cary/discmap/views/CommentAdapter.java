package com.cary.discmap.views;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cary.discmap.R;
import com.cary.discmap.server.ServerCommentUserLookupTask;

public class CommentAdapter extends ArrayAdapter<Comment> {
	Context context; 
    int layoutResourceId;    
    Comment data[] = null;
    
    public CommentAdapter(Context context, int layoutResourceId, Comment[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CommentHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new CommentHolder();
            holder.user = (TextView) row.findViewById(R.id.commenterNameTextView);
            holder.date = (TextView) row.findViewById(R.id.commenterDateTextView);
            holder.comment = (TextView) row.findViewById(R.id.commentTextView);
            
            row.setTag(holder);
        }
        else
        {
            holder = (CommentHolder) row.getTag();
        }
        
        Comment comment = data[position];
        // another async task to lookup user from id
        if(holder.user.getText() != comment.user) {
        	new ServerCommentUserLookupTask(holder.user).execute(Integer.parseInt(comment.user));
        }
        holder.date.setText(comment.date);
        holder.comment.setText(comment.comment);
        
        return row;
    }
    
    static class CommentHolder
    {
        TextView user;
        TextView date;
        TextView comment;
    }
}
