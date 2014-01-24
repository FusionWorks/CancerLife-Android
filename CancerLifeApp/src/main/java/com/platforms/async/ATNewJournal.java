package com.platforms.async;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.platforms.main.steps.StepThree;

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
 * Created by AGalkin on 11/26/13.
 */
public class ATNewJournal extends AsyncTask<Void, Void, Void> {
    RelativeLayout loading;
    StepThree stepThree;
    String endPoint;
    int result;
    JSONObject data;

    public ATNewJournal(String endPoint, StepThree stepThree, RelativeLayout loading, JSONObject data ){
        this.stepThree = stepThree;
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(stepThree.getApplicationContext());
        httppost.addHeader("token", prefs.getString("loginAuth", ""));
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();

        try {
            HttpResponse response = httpclient.execute(httppost);
            JSONObject jsonResult = (new JSONObject(EntityUtils.toString(response.getEntity())));
            Log.v("CL","result " + jsonResult);
            result = jsonResult.getInt("result");
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
        stepThree.postResponse(result);
        loading.setVisibility(View.GONE);
    }

}

