package com.platforms.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.List;

/**
* Created by AGalkin on 12/14/13.
*/
public class MessagesListener extends Service {
    final String LOG_TAG = "CL";
    private XMPPConnection connection;
    private PacketListener packetListener;

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        addMessagesListener();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    void addMessagesListener(){
        CancerLife cancerLife = (CancerLife) this.getApplication();
        connection = cancerLife.getConnection();
        if (connection != null) {
            //Packet listener to get messages sent to logged in user
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            packetListener= new PacketListener() {
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    String fromName = message.getFrom();
                    String messageText = message.getBody();
                    Log.v("RPS","player get "+fromName +" mes "+messageText );
                    showDialog(fromName, messageText);
                }
            };
            connection.addPacketListener(packetListener, filter);
//            cancerLife.setPacketListener(packetListener);

        }
    }

    void showDialog(String title, String body){
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClass(this, ServiceDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("body", body);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}
