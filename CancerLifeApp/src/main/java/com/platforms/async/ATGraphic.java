package com.platforms.async;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.platforms.handlers.ReportsHandler;
import com.platforms.main.ReportsActivity;
import com.platforms.utils.Endpoints;
import com.platforms.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by AGalkin on 12/13/13.
 */
public class ATGraphic extends AsyncTask<Void, Void, Void> {
    RelativeLayout loading;
    String endPoint;
    ReportsActivity activity;
    JSONObject data;
    ReportsHandler reportsHandler;
    boolean result;
    HashMap<Integer, Drawable> drawables;

    public ATGraphic(String endPoint, ReportsActivity activity, RelativeLayout loading, ReportsHandler reportsHandler){
        this.activity = activity;
        this.endPoint = endPoint;
        this.loading = loading;
        this.reportsHandler = reportsHandler;
    }

    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
        result = false;
        drawables = new HashMap<Integer, Drawable>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.v(Utils.TAG, "endpoint " + endPoint);
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(endPoint);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        request.addHeader("token",prefs.getString("loginAuth",""));
        request.addHeader("width",getDisplayWidth());
        HttpResponse response = null;
        try {
            response = client.execute(request);
            data = new JSONObject(EntityUtils.toString(response.getEntity()));
            load();
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
        activity.graphicsReceived(result);
    }

    public void load() throws JSONException, IOException {
        if(data.getInt("result") == 1){
            result = true;
            for(int i=7;i<60;i+=7){
                if(i==28) i=60;
                String url = Endpoints.main + data.getString(String.valueOf(i));
                Drawable drawable = Utils.drawableFromUrl(url, activity);
                Log.v("CL", "drawable "+drawable);
                drawables.put(i,drawable);
                reportsHandler.setGraphic(drawables);
            }
        }
    }

    public String getDisplayWidth(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        String width = String.valueOf(displaymetrics.widthPixels);
        return width;
    }
}