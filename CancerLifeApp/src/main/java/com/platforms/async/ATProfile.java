package com.platforms.async;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.platforms.main.ProfileActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by AGalkin on 11/2/13.
 */
public class ATProfile extends AsyncTask<Void, Void, Void> {
    RelativeLayout loading;
    String endPoint;
    ProfileActivity profile;
    JSONObject data;
    JSONObject responseJson;
    public ATProfile(String endPoint, ProfileActivity profile, JSONObject data, RelativeLayout loading){
        this.profile = profile;
        this.endPoint = endPoint;
        this.loading = loading;
        this.data = data;

    }

    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
    }
    @Override
    protected Void doInBackground(Void... params) {

        if (data != null){
            setProfile();
        }else{
            getProfile();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        loading.setVisibility(View.GONE);
        if (data != null){
            profile.postProfileResponse(responseJson);
        }else{
            profile.getProfileResponse(responseJson);
        }
    }

    public void getProfile(){

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(endPoint);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(profile.getApplicationContext());
        request.addHeader("token",prefs.getString("loginAuth",""));
        HttpResponse response = null;
        try {
            response = client.execute(request);
            responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setProfile(){
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = null;
        //url with the post data

        httppost = new HttpPost(endPoint);
        Log.v("CancerLife", "url " + endPoint);

        //passes the results to a string builder/entity
        StringEntity se = null;

        try {
            se = new StringEntity(data.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.v("CancerLife", "exception" + e);
        }
        httppost.setEntity(se);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(profile.getApplicationContext());
        httppost.addHeader("token",prefs.getString("loginAuth",""));
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();

        try {
            HttpResponse response = httpclient.execute(httppost);
            responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("CancerLife", "exception" + e);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
