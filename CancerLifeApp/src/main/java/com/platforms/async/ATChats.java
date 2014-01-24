package com.platforms.async;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.platforms.objects.Chat;
import com.platforms.R;
import com.platforms.main.ChatsListActivity;
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
public class ATChats extends AsyncTask<Void, Void, Void> {
    RelativeLayout loading;
    String endPoint;
    ChatsListActivity activity;
    JSONObject data;
    ArrayList<Chat> chats = new ArrayList<Chat>();
    public ATChats(String endPoint, ChatsListActivity activity, RelativeLayout loading){
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
            if (data.getInt("result") == 1){
                JSONArray array = data.getJSONArray("chats");
                for(int i=0; i<array.length(); i++){
                    JSONObject chat = array.getJSONObject(i);
                    Drawable picture;
                    if(chat.getString("picture").length()<1){
                        Drawable drawable = new BitmapDrawable(activity.getBaseContext().getResources(), BitmapFactory.decodeResource(activity.getBaseContext().getResources(), R.drawable.no_photo_available));
                        picture = drawable;
                    }else{
                        picture = Utils.drawableFromUrl(chat.getString("picture"), activity);
                    }
                    String name = chat.getString("name");
                    String text = chat.getString("text");
                    String jid = chat.getString("jid");
                    boolean self = chat.getBoolean("self");
                    Chat newChat = new Chat(jid, name, picture, text, self);
                    chats.add(newChat);
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
        activity.hadleResponse(chats);
    }

}
