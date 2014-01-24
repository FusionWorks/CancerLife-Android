package com.platforms.async;

/**
 * Created by AGalkin on 12/14/13.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.platforms.main.LoginActivity;
import com.platforms.utils.Endpoints;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;


//settings get input and then connection is established

public class XMppSettings extends AsyncTask<Void, Void, Void> {
    private LoginActivity loginActivty;
    String username;
    String password;
    XMPPConnection connection;
    String exception;
    XMPPConnection internalConnection;
    RelativeLayout loading;
    public XMppSettings(LoginActivity loginActivty, String username, String password, RelativeLayout loading){
        this.loginActivty = loginActivty;
        this.username = username;
        this.password =  password;
        this.loading = loading;
    }


    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
    }
    @Override
    protected Void doInBackground(Void... params) {
        connection = start();
        Presence presence = new Presence(Presence.Type.available);
        connection.sendPacket(presence);
        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        loading.setVisibility(View.GONE);
        if(connection == null){
            loginActivty.haveAProblem(exception);
        }else{
            loginActivty.setUpConnection(connection);
        }

    }

    protected XMPPConnection start() {
        exception=null;
        String host = Endpoints.XMPPhost;
        String port = Endpoints.XMPPport;
        String service = "";

        ConnectionConfiguration connectionConfig = new ConnectionConfiguration(host, Integer.parseInt(port));
        connectionConfig.setSendPresence(true);
        XMPPConnection connection = new XMPPConnection(connectionConfig);

        try {
            Log.v("RPS", "times");

            connection.connect();
            connection.login(username, password, "");


        } catch (XMPPException ex) {

            Log.v("RPS", "exception "+ex.getMessage().substring(ex.getMessage().length()-4, ex.getMessage().length()-1));
            Log.v("RPS", "exception "+connection.isAuthenticated());
            exception = ex.getMessage().substring(ex.getMessage().length()-4, ex.getMessage().length()-1);
            connection=null;
        }
        if(connection!=null){
        }
        // Set status to online / available



        return connection;
    }

}