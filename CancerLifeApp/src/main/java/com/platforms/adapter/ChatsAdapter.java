package com.platforms.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.platforms.objects.Chat;
import com.platforms.R;
import com.platforms.main.ChatsListActivity;
import com.platforms.utils.Utils;

import java.util.ArrayList;

/**
 * Created by AGalkin on 12/18/13.
 */
public class ChatsAdapter extends ArrayAdapter<Chat> {
    private ArrayList<Chat> data;
    private ChatsListActivity activity;

    public ChatsAdapter(ChatsListActivity activity, ArrayList<Chat> data) {
        super(activity, R.layout.activity_list_chats ,data);
        this.activity=activity;
        this.data=data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_item_chat, parent, false);

        Chat item = data.get(position);
        TextView name = (TextView)rowView.findViewById(R.id.name);
        TextView text = (TextView)rowView.findViewById(R.id.text);
        ImageView photo = (ImageView) rowView.findViewById(R.id.picture);

        name.setText(item.name);
        text.setText(item.text);
        Log.v("CL","name "+ item.name +"text "+ item.text);
        Utils.setBackgroundBySDK(photo, item.picture);

        return rowView;
    }

}
