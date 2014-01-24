package com.platforms.main.steps;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.platforms.async.ATNewJournal;
import com.platforms.R;
import com.platforms.main.JournalActivity;
import com.platforms.utils.Endpoints;
import com.platforms.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by AGalkin on 11/9/13.
 */
public class StepThree extends Activity {
    EditText message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_three);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        message = (EditText)findViewById(R.id.text);
    }

    public void postPublic(View view){
        try {
            StepOne.dataToSend.put("message",message.getText().toString());
            circleSelect();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("CL","data " + StepOne.dataToSend);
    }

    public void postPrivate(View view){
        try {
            if (StepOne.dataToSend.has("circle")) StepOne.dataToSend.put("circle","");
            StepOne.dataToSend.put("message",message.getText().toString());
            StepOne.dataToSend.put("private", true);
            Log.v("CL","data " + StepOne.dataToSend);
            goData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void circleSelect() throws JSONException {
        ArrayList<String> names = new ArrayList<String>();
        JSONArray tmp = StepOne.data.getJSONArray("UserCircles");

        for(int i=0; tmp.length()>i;i++){
            names.add(tmp.getString(i));
        }

        final Dialog circlesListDialog = new Dialog(this);
        circlesListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        circlesListDialog.setContentView(R.layout.dialog_symptom_list);
        final ListView circlesList = (ListView) circlesListDialog.findViewById(R.id.symptomsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_symptoms, names);
        circlesList.setAdapter(adapter);
        circlesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            TextView text = (TextView)view;
                try {
                    StepOne.dataToSend.put("circle", (text.getText()));
                    StepOne.dataToSend.put("private", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                goData();
                circlesListDialog.dismiss();


            }
        });
        circlesListDialog.show();
    }

    public void goData(){
        RelativeLayout loading = (RelativeLayout) findViewById(R.id.loadingAnimationContent);
        ATNewJournal ATNJ = new ATNewJournal(Endpoints.newJournal ,this, loading, StepOne.dataToSend);
        ATNJ.execute();
    }

    public void postResponse(int result){
        if (result == 1){
            Intent intent = new Intent(this, JournalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else{
            Utils.alertView("Try one more time",this);
        }
    }
}
