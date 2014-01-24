package com.platforms.async;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.platforms.utils.Endpoints;
import com.platforms.utils.JsonParser;
import com.platforms.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ATLogout extends AsyncTask<Void, Void, Void>  {
    public Activity activity;
    public String authKey;
    private int status;

    public ATLogout(Activity activity, String authKey){
        this.authKey = authKey;
        this.activity = activity;
    }
    @Override
    protected Void doInBackground(Void... params) {

        JsonParser JP = new JsonParser();
        JSONObject json = JP.parseObject(Endpoints.logout+authKey);
        try {
            status = json.getInt("status");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        if(status==1){
            Utils.alertView("You successfully logged off", activity);
            SharedPreferences sPref = activity.getSharedPreferences("Login", 0);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("key", "");
            ed.putString("currentName", "");
            ed.commit();

        }else{
            Utils.alertView("Try Again", activity);
        }
    }

}