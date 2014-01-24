package com.platforms.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.platforms.menu.NavMenu;
import com.platforms.menu.SlideHolder;
import com.platforms.objects.Chat;
import com.platforms.adapter.ChatsAdapter;
import com.platforms.async.ATChats;
import com.platforms.R;
import com.platforms.utils.Endpoints;
import com.platforms.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by AGalkin on 12/18/13.
 */
public class ChatsListActivity extends Activity {
    ListView usersList;
    ArrayList<Chat> data;
    SlideHolder mSlideHolder;
    NavMenu navMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chats);

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);
        navMenu = new NavMenu(this, mSlideHolder, 2);
        RelativeLayout loading = (RelativeLayout)findViewById(R.id.loadingAnimationContent);
        usersList = (ListView)findViewById(R.id.chatList);
        String endPoint = Endpoints.getChats;
        ATChats ATC = new ATChats(endPoint, this, loading);
        ATC.execute();

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String jid = data.get(position).jid;
                String name = data.get(position).name;
                Bitmap photo = ((BitmapDrawable)data.get(position).picture).getBitmap();

                Intent intent = new Intent(ChatsListActivity.this, MessagesActivity.class);
                intent.putExtra("jid", jid);
                intent.putExtra("name", name);
                try {
                    Utils.saveImage(photo, jid);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                startActivity(intent);
            }
        });
    }

    public void hadleResponse(ArrayList<Chat> data){
        initiateChats(data);
    }

    public void initiateChats(ArrayList<Chat> data){
        this.data = data;
        ChatsAdapter userAdapter = new ChatsAdapter(this, data);
        usersList.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
    }

    public void showMenu(View view){
        mSlideHolder.toggle();
    }


}