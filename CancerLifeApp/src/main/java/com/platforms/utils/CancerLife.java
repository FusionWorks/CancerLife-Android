package com.platforms.utils;

/**
 * Created by AGalkin on 12/14/13.
 */

import android.app.Activity;
import android.app.Application;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;

public class CancerLife extends Application {

    private XMPPConnection connection;
    private PacketListener listener;

    public XMPPConnection getConnection() {
        return connection;
    }

    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
    }
//
//    public PacketListener getPacketListener(){
//        return listener;
//    }
//
//    public void setPacketListener(PacketListener listener){
//        this.listener = listener;
//    }
}
