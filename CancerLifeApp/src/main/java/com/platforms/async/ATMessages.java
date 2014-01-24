package com.platforms.async;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.platforms.objects.MessageApp;
import com.platforms.main.MessagesActivity;
import com.platforms.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by AGalkin on 12/20/13.
 */
public class ATMessages extends AsyncTask<Void, Void, Void> {
    RelativeLayout loading;
    String endPoint;
    MessagesActivity activity;
    JSONObject data;
    ArrayList<MessageApp> messageApps = new ArrayList<MessageApp>();
    public ATMessages(String endPoint, MessagesActivity activity, RelativeLayout loading){
        this.activity = activity;
        this.endPoint = endPoint;
        this.loading = loading;
    }

    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
    }
    @Override
    protected Void doInBackground(Void... params) {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(endPoint);
        Log.v("CL", "endPoint " + endPoint);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        request.addHeader("token",prefs.getString("loginAuth",""));
        HttpResponse response = null;
        try {
            response = client.execute(request);
            data = new JSONObject(EntityUtils.toString(response.getEntity()));
            Log.v("CL", "comm" + data);
            if (data.getInt("result") == 1){
                JSONArray array = data.getJSONArray("messages");
                for(int i=0; i<array.length(); i++){
                    JSONObject message = array.getJSONObject(i);
                    String text = message.getString("text");
                    String time = Utils.dateConvert(message.getLong("created"));
                    boolean self = message.getBoolean("self");
                    MessageApp newMessageApp = new MessageApp(text, time, self);
                    messageApps.add(newMessageApp);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }
    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        loading.setVisibility(View.GONE);
        activity.hadleResponse(messageApps);
    }

}