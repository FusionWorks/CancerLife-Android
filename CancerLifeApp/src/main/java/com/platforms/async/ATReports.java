package com.platforms.async;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.platforms.objects.ReportItem;
import com.platforms.handlers.ReportsHandler;
import com.platforms.main.ReportsActivity;
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
import java.util.HashMap;

/**
 * Created by AGalkin on 12/12/13.
 */
public class ATReports extends AsyncTask<Void, Void, Void> {
    RelativeLayout loading;
    String endPoint;
    ReportsActivity activity;
    JSONObject data;
    ReportsHandler reportsHandler;
    String[] reportNames;
    String[] tableTitles = {"frequent_symptoms", "new_symptoms", "quality_of_life"};
    boolean result;

    public ATReports(String endPoint, ReportsActivity activity, RelativeLayout loading, ReportsHandler reportsHandler){
        this.activity = activity;
        this.endPoint = endPoint;
        this.loading = loading;
        this.reportsHandler = reportsHandler;
    }

    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
        result = false;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.v(Utils.TAG, "endpoint "+endPoint);
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(endPoint);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        request.addHeader("token",prefs.getString("loginAuth",""));
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
        activity.dataReceived(reportNames, result);
    }

    public void load() throws JSONException {
        if (data.getInt("result") == 1){
            result = true;
            Log.v(Utils.TAG,"data "+data);
            reportNames = new String[9];
            reportNames[0] = "Reports";
            for(int i=1; i!=9; i++){
                reportNames[i]= data.getJSONObject("reports").getString(String.valueOf(i));
            }
            for(String title : tableTitles){
                HashMap<Integer, ArrayList<ReportItem>> map = new HashMap<Integer, ArrayList<ReportItem>>();
                for(int i=7;i<60;i+=7){
                    if(i==28) i=60;
                    ArrayList<ReportItem> intervals = new ArrayList<ReportItem>();
                    Log.v(Utils.TAG, "titles "+title);
                    JSONArray jsonArray = data.getJSONObject(title).getJSONArray(String.valueOf(i));
                    Log.v(Utils.TAG, "jsonArray "+ jsonArray);
                    for(int y=0;y<jsonArray.length();y++){
                        ReportItem item = null;
                        try{
                            JSONObject object =  jsonArray.getJSONObject(y);
                            if(object.has("name")){
                                String name = object.getString("name");
                                float avg_score = (float)object.getDouble("avg_score");
                                float percentage = (float)object.getDouble("percentage");
                                item = new ReportItem(name, avg_score,percentage, 0.0f, 0.0f, "");
                            }else if(object.has("current")){
                                float current = (float)object.getDouble("current");
                                float previous = (float)object.getDouble("previous");
                                String change = object.getString("change");
                                item = new ReportItem("", 0.0f, 0.0f, current, previous, change);
                            }
                        }
                        catch(JSONException e){
                            String name = jsonArray.getString(y);
                            item = new ReportItem(name,0.0f, 0.0f, 0.0f, 0.0f, "");

                        }

                        intervals.add(item);
                    }
                    map.put(i,intervals);
                }
                if(title.equals("frequent_symptoms"))
                    reportsHandler.setFrequentSymptoms(map);
                else if (title.equals("new_symptoms"))
                    reportsHandler.setNewSymptoms(map);
                else if (title.equals("quality_of_life"))
                    reportsHandler.setQualityOfLife(map);
            }
            Log.v(Utils.TAG,"reportsHandler "+reportsHandler.getFrequentSymptoms().size());
            Log.v(Utils.TAG,"reportsHandler "+reportsHandler.getNewSymptoms().size());
            Log.v(Utils.TAG,"reportsHandler "+reportsHandler.getQualityOfLife().size());
        }
    }
}
