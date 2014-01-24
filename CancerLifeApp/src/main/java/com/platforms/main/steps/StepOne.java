package com.platforms.main.steps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.platforms.async.ATSteps;
import com.platforms.R;
import com.platforms.handlers.PostsHandler;
import com.platforms.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by AGalkin on 11/9/13.
 */

public class StepOne extends Activity {
    String srcMoods = "moods/";
    String srcEffects = "side_effects/";
    public static JSONObject data;
    public static JSONObject dataToSend;
    public static PostsHandler handler;
    ArrayList<String> symptoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_one);
        data = new JSONObject();
        dataToSend = new JSONObject();
        symptoms = new ArrayList<String>();
        RelativeLayout loading = (RelativeLayout)findViewById(R.id.loadingAnimationContent);
        handler = new PostsHandler();
        ATSteps ATS = new ATSteps(this, loading, handler);
        ATS.execute();
    }

    public void setupMoods(JSONArray moods) throws JSONException, IOException {
        ArrayList<ImageView> moodsImageViews = new ArrayList<ImageView>();
        ImageView moodImageView = (ImageView)findViewById(R.id.mood1);
        moodsImageViews.add(moodImageView);
        moodImageView = (ImageView)findViewById(R.id.mood2);
        moodsImageViews.add(moodImageView);
        moodImageView = (ImageView)findViewById(R.id.mood3);
        moodsImageViews.add(moodImageView);
        moodImageView = (ImageView)findViewById(R.id.mood4);
        moodsImageViews.add(moodImageView);
        moodImageView = (ImageView)findViewById(R.id.mood5);
        moodsImageViews.add(moodImageView);

        for(int i=0; i<moods.length(); i++){
            String mood = moods.getString(i);
            moodsImageViews.get(i).setTag(mood);
            Utils.setBackgroundBySDK(moodsImageViews.get(i), (Drawable.createFromStream(this.getAssets().open(srcMoods + mood), null)));
        }
    }

    public void setupSymptoms(JSONArray symptoms) throws JSONException, IOException {
        ArrayList<ImageView> symptomImageViews = new ArrayList<ImageView>();
        ImageView symptomImageView = (ImageView)findViewById(R.id.symptom1);
        symptomImageViews.add(symptomImageView);
        symptomImageView = (ImageView)findViewById(R.id.symptom2);
        symptomImageViews.add(symptomImageView);
        symptomImageView = (ImageView)findViewById(R.id.symptom3);
        symptomImageViews.add(symptomImageView);

        for(int i=0; i<symptoms.length(); i++){
            String symptom = symptoms.getJSONObject(i).getString("icon");
            symptomImageViews.get(i).setTag(symptom);
            Utils.setBackgroundBySDK(symptomImageViews.get(i), (Drawable.createFromStream(this.getAssets().open(srcEffects + symptom), null)));
        }
    }

    public void moodsClick(View view){
        try {
            setupMoods(this.data.getJSONArray("Moods"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String prevState = view.getTag().toString();
        StringBuilder newState = new StringBuilder(prevState);
        int position = prevState.length()-5;
        char state = prevState.charAt(position);
        if (state == '0'){
            char tmp = prevState.charAt(1);
            if (tmp == '7') symptoms.add(prevState);
            newState.setCharAt(position, '1');
        }else{
            char tmp = prevState.charAt(1);
            if (tmp == '7') symptoms.remove(prevState);
            newState.setCharAt(position, '0');
        }
        view.setTag(newState);

        try {
            Utils.setBackgroundBySDK(view, (Drawable.createFromStream(this.getAssets().open(srcMoods + newState), null)));
            dataToSend.put("mood",prevState);
            Log.v("CL", "mood "+prevState);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sideEffectClick(View view){
        String prevState = view.getTag().toString();
        StringBuilder newState = new StringBuilder(prevState);
        int position = prevState.length()-5;
        char state = prevState.charAt(position);
        if (state == '0'){
            symptoms.add(prevState);
            newState.setCharAt(position, '1');
        }else{
            newState.setCharAt(position, '0');
            Log.v("CL", "stat "+symptoms.get(0));
            Log.v("CL", "remove "+newState+ "re "+symptoms.remove(newState.toString()));
        }
        view.setTag(newState);

        try {
            Utils.setBackgroundBySDK(view, (Drawable.createFromStream(this.getAssets().open(srcEffects + newState), null)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toSecondStep(View view){
        Intent intent = new Intent(this, StepTwo.class);
        intent.putExtra("symptoms", symptoms.toArray(new String[symptoms.size()]));
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        try {
            symptoms.clear();
            setupMoods(data.getJSONArray("Moods"));
            setupSymptoms(data.getJSONArray("SideEffectIcons"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
