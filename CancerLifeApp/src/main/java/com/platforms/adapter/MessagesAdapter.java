package com.platforms.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.platforms.objects.MessageApp;
import com.platforms.R;
import com.platforms.main.MessagesActivity;

import java.util.ArrayList;

/**
 * Created by AGalkin on 12/18/13.
 */
public class MessagesAdapter extends ArrayAdapter<MessageApp> {
    private ArrayList<MessageApp> data;
    private MessagesActivity activity;
    public String name;
    public String myName;
    public Bitmap photo;
    public Bitmap myPhoto;

    public MessagesAdapter(MessagesActivity activity, ArrayList<MessageApp> data, String name, String myName, Bitmap photo, Bitmap myPhoto) {
        super(activity, R.layout.activity_messages ,data);
        this.activity=activity;
        this.data=data;
        this.name = name;
        this.myName = myName;
        this.photo = photo;
        this.myPhoto = myPhoto;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MessageApp item = data.get(position);
        final View rowView;
        Bitmap selectedPhoto;
        String selectedName;
        if(item.self){
            rowView = inflater.inflate(R.layout.list_item_message_receiver, parent, false);
            selectedName = myName;
            selectedPhoto = myPhoto;
        }else{
            rowView = inflater.inflate(R.layout.list_item_message_sender, parent, false);
            selectedName = name;
            selectedPhoto = photo;
        }

        TextView nameView = (TextView)rowView.findViewById(R.id.name);
        TextView textView = (TextView)rowView.findViewById(R.id.text);
        TextView timeView = (TextView)rowView.findViewById(R.id.time);
        ImageView photoView = (ImageView) rowView.findViewById(R.id.photo);

        nameView.setText(selectedName);
        textView.setText(item.text);
        timeView.setText(item.time);
        photoView.setImageBitmap(selectedPhoto);

        return rowView;
    }

}
