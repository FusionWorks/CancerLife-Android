package com.platforms.async;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.platforms.main.SupportersActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
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
 * Created by AGalkin on 11/11/13.
 */
public class ATSupporterInvite extends AsyncTask<Void, Void, Void> {
    ProgressBar loading;
    SupportersActivity supportersActivity;
    String endPoint;
    int resultData;
    JSONObject data;

    public ATSupporterInvite(String endPoint, SupportersActivity supportersActivity, ProgressBar loading, JSONObject data){
        this.supportersActivity = supportersActivity;
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
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = null;

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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(supportersActivity.getApplicationContext());
        httppost.addHeader("token", prefs.getString("loginAuth", ""));
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();

        try {
            HttpResponse response = httpclient.execute(httppost);

            JSONObject jsonResult = (new JSONObject(EntityUtils.toString(response.getEntity())));
            resultData = jsonResult.getInt("result");
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("CancerLife", "exception" + e);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        loading.setVisibility(View.GONE);
        supportersActivity.inviteSupporterResult(resultData);
    }
}
