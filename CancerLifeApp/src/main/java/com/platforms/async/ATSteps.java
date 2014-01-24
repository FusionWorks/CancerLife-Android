package com.platforms.async;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.platforms.objects.Level;
import com.platforms.objects.PostItem;
import com.platforms.handlers.PostsHandler;
import com.platforms.main.steps.StepOne;
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
import java.util.ArrayList;

/**
 * Created by AGalkin on 11/21/13.
 */
public class ATSteps extends AsyncTask<Void, Void, Void> {
    public StepOne activity;
    private int result;
    public JSONObject data;
    RelativeLayout loading;
    PostsHandler handler;

    public ATSteps(StepOne activity, RelativeLayout loading, PostsHandler handler){
        this.activity = activity;
        this.loading = loading;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(Endpoints.journalFields);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        request.addHeader("token",prefs.getString("loginAuth",""));
        HttpResponse response = null;
        try {
            response = client.execute(request);
            data = new JSONObject(EntityUtils.toString(response.getEntity()));
            StepOne.data = data;
            result = data.getInt("result");
            if (result == 1){
                load();
            }
            Log.v("CL", "JSON " + data);
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
            if(result == 1){
            try {
                activity.setupMoods(data.getJSONArray("Moods"));
//                Vitalik services
                activity.setupSymptoms(data.getJSONArray("SideEffectIcons"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Utils.alertView("Try Again", activity);

        }
    }

    public void load() throws JSONException {
        ArrayList<PostItem> posts = new ArrayList<PostItem>();
        for (int i = 0; i<data.getJSONArray("SideEffects").length(); i++){
            JSONObject object = data.getJSONArray("SideEffects").getJSONObject(i);
            String name = object.getString("name");
            Level level1 = null;
            Level level2 = null;
            String levelString = "level";
            for (int z = 0; z<2; z++){
                if (object.has(levelString)) {
                    JSONObject level = object.getJSONObject(levelString);

                    String question = "";
                    if (level.has("q"))  question = level.getString("q");

                    int min = 0;
                    if (level.has("min"))   min = level.getInt("min");

                    int max = 0;
                    if (level.has("max"))   max = level.getInt("max");

                    int step = 0;
                    if (level.has("step"))   step = level.getInt("step");

                    int defaultNum = 1;
                    if (level.has("default"))   defaultNum = level.getInt("default");

                    ArrayList<String> options = new ArrayList<String>();
                    if (level.has("options")){
                            int y = 1;
                            JSONObject optionsJson = level.getJSONObject("options");
                            while(optionsJson.has(""+y)){
                                options.add(level.getJSONObject("options").getString("" + y));
                                y++;
                            }
                    }
                    if (levelString.equals("level")){
                        level1 = new Level(question, min, max, step, defaultNum, options);
                    }else{
                        level2 = new Level(question, min, max, step, defaultNum, options);
                        Log.v("CL", "options" +"  "+options);
                    }
                }
                levelString = "level2";
            }
            String questionType1 = "";
            String questionText1 = "";
            if (object.has("question1")) {
                 questionType1 = object.getJSONObject("question1").getString("type");
                 questionText1 = object.getJSONObject("question1").getString("q");
            }

            String questionType2 = "";
            String questionText2 = "";
            ArrayList<String> options = null;
            if (object.has("question2")) {
                JSONObject qObj = object.getJSONObject("question2");
                questionType2 = qObj.getString("type");
                questionText2 = qObj.getString("q");
                options = new ArrayList<String>();
                if (qObj.has("options")){
                    int y = 1;
                    while(qObj.getJSONObject("options").has("" + y)){
                        options.add(qObj.getJSONObject("options").getString("" + y));
                        y++;
                    }
                }
            }
            PostItem postItem = new PostItem( name, 0, level1, level2, questionType1, questionText1, questionType2, questionText2, options);
            posts.add(postItem);
        }

        handler.setPosts(posts);

    }

}
