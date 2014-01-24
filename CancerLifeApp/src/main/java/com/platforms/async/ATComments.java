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

import com.platforms.objects.Comment;
import com.platforms.R;
import com.platforms.main.CommentsActivity;
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
 * Created by AGalkin on 11/14/13.
 */
public class ATComments extends AsyncTask<Void, Void, Void> {
        RelativeLayout loading;
        String endPoint;
        CommentsActivity activity;
        JSONObject data;
        ArrayList<Comment> comments = new ArrayList<Comment>();
    public ATComments(String endPoint, CommentsActivity activity, RelativeLayout loading){
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
        Log.v("CL", "endPoint "+endPoint);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        request.addHeader("token",prefs.getString("loginAuth",""));
        HttpResponse response = null;
        try {
            response = client.execute(request);
            data = new JSONObject(EntityUtils.toString(response.getEntity()));
            Log.v("CL", "comm" + data);
            if (data.getInt("result") == 1){
                JSONArray array = data.getJSONArray("comments");
                for(int i=0; i<array.length(); i++){
                    JSONObject comment = array.getJSONObject(i);
                    Drawable photo;
                    if(comment.getString("photo").length()<1){
                        Drawable drawable = new BitmapDrawable(activity.getBaseContext().getResources(), BitmapFactory.decodeResource(activity.getBaseContext().getResources(), R.drawable.no_photo_available));
                        photo = drawable;
                    }else{
                         photo = Utils.drawableFromUrl(comment.getString("photo"), activity);
                    }
                    String name = comment.getString("name");
                    String commentText = comment.getString("message");
                    String time = Utils.dateConvert(comment.getLong("created"));
                    Log.v("CL","comment Time"+ comment.getLong("created"));
                    Comment newComment = new Comment(photo, name, commentText, time);
                    comments.add(newComment);
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
        activity.hadleResponse(comments);
    }

}

