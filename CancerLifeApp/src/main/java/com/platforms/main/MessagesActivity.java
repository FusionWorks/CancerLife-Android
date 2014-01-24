package com.platforms.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.platforms.objects.MessageApp;
import com.platforms.adapter.MessagesAdapter;
import com.platforms.async.ATMessages;
import com.platforms.R;
import com.platforms.utils.CancerLife;
import com.platforms.utils.Endpoints;
import com.platforms.utils.Utils;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Message.Type;

import java.util.ArrayList;

/**
 * Created by AGalkin on 12/12/13.
 */
public class MessagesActivity extends Activity {
    private PacketListener packetListener;
    ArrayList<MessageApp> data;
    String name;
    String myName;
    Bitmap photo;
    Bitmap myPhoto;
    String jid;
    XMPPConnection connection;
    ListView usersList;
    MessagesAdapter userAdapter;
    PacketListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        RelativeLayout loading = (RelativeLayout)findViewById(R.id.loadingAnimationContent);
        Intent i = getIntent();
        TextView nameTitle = (TextView) findViewById(R.id.nameTitle);
        jid = i.getStringExtra("jid");
        name = i.getStringExtra("name");
        myName = prefs.getString("name","");
        data = new ArrayList<MessageApp>();
        photo = ((BitmapDrawable)Utils.loadImage(this, jid)).getBitmap();
        myPhoto = ((BitmapDrawable)Utils.loadImage(this, "profile")).getBitmap();

        nameTitle.setText(name);

        CancerLife cancerLife = (CancerLife) this.getApplication();
        connection = cancerLife.getConnection();
//        listener = cancerLife.getPacketListener();
//        connection.removePacketListener(listener);

        String endPoint = Endpoints.getMessages+jid;
        ATMessages ATM = new ATMessages(endPoint, this, loading);
        ATM.execute();

    }

    public void hadleResponse(ArrayList<MessageApp> receivedData){
        data = receivedData;
        initiateMessages();
        initiateListener();
        reloadListView();
    }

    public void initiateMessages(){
        usersList = (ListView)findViewById(R.id.messagesList);
        userAdapter = new MessagesAdapter(this, data, name, myName, photo, myPhoto);
        usersList.setAdapter(userAdapter);
        Log.v("CL", "array length " + data.size());

    }

    @Override
    public void onPause () {
        super.onPause();
    }

    public void initiateListener(){
        CancerLife cancerLife = (CancerLife) this.getApplication();
        connection = cancerLife.getConnection();
        Log.v("CL", "connection 1" + connection);
        if (connection != null) {
            Log.v("CL", "connection 2" + connection);
            //Packet listener to get messages sent to logged in user
            PacketFilter filter = new MessageTypeFilter(org.jivesoftware.smack.packet.Message.Type.chat);
            packetListener= new PacketListener() {
                public void processPacket(Packet packet) {
                    Log.v("CL","packet "+packet);
                    org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) packet;
                    String fromName = message.getFrom();
                    String messageText = message.getBody();
                    Log.v("CL","player get "+fromName +" mes "+messageText +"array length "+data.size() );
                    long curTime = System.currentTimeMillis() / 1000L;
                    MessageApp newMessageApp = new MessageApp(messageText, Utils.dateConvert(curTime), false);
                    data.add(newMessageApp);
                    reloadListView();
                }
            };
            connection.addPacketListener(packetListener, filter);
        }
    }

    public void sendMessage(View view){
        EditText message = (EditText)findViewById(R.id.textMessage);
        String text = message.getText().toString();
        String unixTime = Utils.dateConvert(System.currentTimeMillis() / 1000L);

        Message msg = new Message("listen@beta.cancerlife.net", Type.chat);
        msg.setBody(jid+":"+text);
        try{
            connection.sendPacket(msg);
        }catch (NullPointerException e){
            Utils.alertView("Please relogin", this);
        }

        msg = new Message(jid, Type.chat);
        msg.setBody(text);try{
            connection.sendPacket(msg);
        }catch (NullPointerException e){
        }

        MessageApp msgApp = new MessageApp(text, unixTime, true);
        data.add(msgApp);

        message.setText("");
        reloadListView();

    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.v("CL", "onStop");

//        CancerLife cancerLife = (CancerLife) this.getApplication();
//        listener = cancerLife.getPacketListener();
//        PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
//        connection.addPacketListener(listener, filter);
    }

    private void reloadListView() {
        usersList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view..
                userAdapter.notifyDataSetChanged();
                usersList.setSelection(data.size() - 1);
            }
        });
    }


}