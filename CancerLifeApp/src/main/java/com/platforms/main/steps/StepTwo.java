package com.platforms.main.steps;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.platforms.objects.PostItem;
import com.platforms.adapter.PostJournalAdapter;
import com.platforms.R;
import com.platforms.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by AGalkin on 11/9/13.
 */
public class StepTwo extends Activity{

    int DIALOG_DATE = 1;
    int myYear = 2011;
    int myMonth = 02;
    int myDay = 03;

    public boolean firstInit = true;

    public static ArrayList<PostItem> selectedArray;
    public ArrayList<String> symptomsNames;
    PostJournalAdapter userAdapter;
    Activity activity;
    public ArrayList<String> painOptions = new ArrayList<String>();
    ArrayList<String> postOptionList = new ArrayList<String>();
    long painDate;
    Button dateToAppear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_two);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        activity = this;
        selectedArray = new ArrayList<PostItem>();
        symptomsNames = new ArrayList<String>();
        initiateStepTwo();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String[] symptoms = extras.getStringArray("symptoms");
            try {
                initialEnter(symptoms);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void initiateStepTwo(){
        ListView usersList = (ListView)findViewById(R.id.postsList);
        userAdapter = new PostJournalAdapter(this, selectedArray);
        addFooter();
        usersList.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
    }

    public void addFooter(){
        if(firstInit){
            ListView usersList = (ListView)findViewById(R.id.postsList);
            View footer = getLayoutInflater().inflate(R.layout.new_post_item_footer, null);
            usersList.addFooterView(footer);
            firstInit = false;
        }
    }

    public void toThirdStep(View view){
        try {
            addSideEffectsToJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setClass(this, StepThree.class);
        startActivity(intent);
    }

    public void symptomList(View view){
        ArrayList<String> names = new ArrayList<String>();
        for (PostItem item : StepOne.handler.getPosts()){
            names.add(item.name);
        }
        final Dialog symptomListDialog = new Dialog(this);
        symptomListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        symptomListDialog.setContentView(R.layout.dialog_symptom_list);
        final ListView symptomList = (ListView) symptomListDialog.findViewById(R.id.symptomsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_symptoms, names);
        symptomList.setAdapter(adapter);
        symptomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<PostItem> temp = StepOne.handler.getPosts();
                for (PostItem item : temp){
                    TextView text = (TextView)view;
                    if(item.name.equals(text.getText())){
                        if(symptomsNames.contains(item.name)){
                            Utils.alertView("You already have it in the list", activity);
                        }else{
                            selectedArray.add(item);
                            symptomsNames.add(item.name);
                            userAdapter.notifyDataSetChanged();
                            symptomListDialog.dismiss();
                        }
                    }
                }
            }
        });
        symptomListDialog.show();
    }

    public void removeFromListView(View view){
        ListView lv = (ListView)findViewById(R.id.postsList);
        int position = lv.getPositionForView(view);
        selectedArray.remove(position);
        symptomsNames.remove(position);
        initiateStepTwo();
    }

    public void addSideEffectsToJSON() throws JSONException {
        JSONArray sideEffects = new JSONArray();
        for(PostItem item : selectedArray){
            JSONObject sideEffect = new JSONObject();
            Log.v("CL", "name "+ item.name + "level1 " + item.level1);
            sideEffect.put("name", item.name);
            try{
                sideEffect.put("level", item.level1.defaultNum);
                sideEffect.put("level2", item.level2.defaultNum);
            }catch(NullPointerException e){

            }
            if(item.question1Text.length()>1)
                sideEffect.put("question1", painDate);
            if(item.question2Text.length()>1)
                sideEffect.put("question2", postOptionList);
            sideEffects.put(sideEffect);
        }
        StepOne.dataToSend.put("side_effects", sideEffects);
    }

    public void initialEnter(String[] symptoms) throws JSONException {
        ArrayList<String> tmpSymptoms = new ArrayList<String>();
        JSONArray symptomsArray = StepOne.data.getJSONArray("SideEffectIcons");
        Log.v("CL", "sArray "+symptomsArray);
        for(int i=0; i<symptomsArray.length(); i++){
            for (String symptom : symptoms){
                if (symptomsArray.getJSONObject(i).getString("icon").equals(symptom))
                    tmpSymptoms.add(symptomsArray.getJSONObject(i).getString("name"));
            }
        }
        for (String item : symptoms){
            char tmp = item.charAt(1);
            if (tmp == '7') {
                tmpSymptoms.add("Feeling Down");
            }
        }

        ArrayList<PostItem> temp = StepOne.handler.getPosts();
        for (PostItem postItem : temp){
            for(String name : tmpSymptoms){
                Log.v("CL","post Item "+postItem.name+" name "+name);
                if(postItem.name.equals(name)){
                    selectedArray.add(postItem);
                }
            }
        }
        userAdapter.notifyDataSetChanged();
    }

    public void question2OptionList(final View mainView){
        final Dialog symptomListDialog = new Dialog(activity);
        symptomListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        symptomListDialog.setContentView(R.layout.dialog_symptom_list);
        final ListView symptomList = (ListView) symptomListDialog.findViewById(R.id.symptomsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.list_item_checkbox, painOptions);
        symptomList.setAdapter(adapter);
        symptomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(postOptionList.contains(painOptions.get(position))){
                    Utils.alertView("You already have it in the list", activity);
                }else if(position == painOptions.size()-1){
                    postOptionList.clear();
                }else{
                    postOptionList.add(painOptions.get(position));
                    Button but = (Button)mainView;
                    but.setText(painOptions.get(position).concat(", " + but.getText().toString()));
                    symptomListDialog.dismiss();
                }
            }
        });
        symptomListDialog.show();
    }
    public void  question1OptionTime(View view){
        dateToAppear = (Button)view;
        showDialog(DIALOG_DATE);
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;
            dateToAppear.setText(new StringBuilder().append(myDay)
                    .append("-").append(myMonth).append("-").append(myYear));
            Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
            painDate = calendar.getTimeInMillis()/1000;
        }
    };
}