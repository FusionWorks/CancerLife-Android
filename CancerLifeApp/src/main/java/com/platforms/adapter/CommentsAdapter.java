package com.platforms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.platforms.objects.Comment;
import com.platforms.R;
import com.platforms.main.CommentsActivity;
import com.platforms.utils.Utils;

import java.util.ArrayList;

/**
 * Created by AGalkin on 11/14/13.
 */

public class CommentsAdapter extends ArrayAdapter<Comment> {
    private ArrayList<Comment> data;
    private CommentsActivity activity;

    public CommentsAdapter(CommentsActivity activity, ArrayList<Comment> data) {
        super(activity, R.layout.activity_comments ,data);
        this.activity=activity;
        this.data=data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_item_comment, parent, false);

        Comment item = data.get(position);
        TextView name = (TextView)rowView.findViewById(R.id.name);
        TextView text = (TextView)rowView.findViewById(R.id.text);
        TextView time = (TextView)rowView.findViewById(R.id.time);
        ImageView photo = (ImageView) rowView.findViewById(R.id.photo);

        name.setText(item.name);
        text.setText(item.comment);
        time.setText(item.time);
        Utils.setBackgroundBySDK(photo, item.photo);

        return rowView;
    }

}
