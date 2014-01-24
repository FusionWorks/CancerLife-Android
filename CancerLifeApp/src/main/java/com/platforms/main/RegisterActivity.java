package com.platforms.main;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.platforms.async.ATGetRegFields;
import com.platforms.async.ATLoginRegister;
import com.platforms.R;
import com.platforms.utils.Endpoints;
import com.platforms.utils.Reachability;
import com.platforms.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by AGalkin on 9/26/13.
 */
public class RegisterActivity extends Activity {
    int DIALOG_DATE = 1;
    int myYear = 2011;
    int myMonth = 02;
    int myDay = 03;

    String email = "";
    String password = "";
    String conf_password = "";
    String username = "";
    int roles;
    String first_name = "";
    int gender;
    long birthDate;
    int zip;
    String cancerType;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this.getApplicationContext();
        getRegisterFields();

        TextView datePicker = (TextView)findViewById(R.id.selectedTime);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE);
            }
        });
    }
    public void initSpinner(String[] data){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner cancerType = (Spinner) findViewById(R.id.cancerType);
        cancerType.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void signUpClick(View view){
        JSONObject data = new JSONObject();
        EditText loginField = (EditText) findViewById(R.id.emailField);
        EditText passField = (EditText) findViewById(R.id.passwordField);
        EditText confPasswordField = (EditText) findViewById(R.id.confirmPasswordField);
        EditText usernameField = (EditText) findViewById(R.id.usernameField);
        EditText firstNameField = (EditText) findViewById(R.id.firstNameField);
        RadioButton radioFemale = (RadioButton) findViewById(R.id.radio_female);
        RadioButton radioMale = (RadioButton) findViewById(R.id.radio_male);
        RadioButton patient = (RadioButton) findViewById(R.id.patient);
        RadioButton survivor = (RadioButton) findViewById(R.id.survivor);
        EditText zipField = (EditText) findViewById(R.id.zipCode);
        Spinner cancerTypeSpinner = (Spinner) findViewById(R.id.cancerType);

        RelativeLayout loading = (RelativeLayout)findViewById(R.id.loadingAnimationContent);

        email = loginField.getText().toString();
        password = passField.getText().toString();
        conf_password = confPasswordField.getText().toString();
        username = usernameField.getText().toString();
        first_name = firstNameField.getText().toString();
        if(radioMale.isSelected()) gender = 1;
        if(radioFemale.isSelected()) gender = 2;
        if(patient.isSelected()) roles = 1;
        if(survivor.isSelected()) roles = 2;
        zip = Integer.parseInt(zipField.getText().toString());
        cancerType = cancerTypeSpinner.getSelectedItem().toString();

        try {
            data.put("email", email);
            data.put("password", password);
            data.put("confirm_password", conf_password);
            data.put("username", username);
            data.put("roles", roles);
            data.put("first_name", first_name);
            data.put("gender", gender);
            data.put("date_of_birth", birthDate);
            data.put("zip", zip);
            data.put("cancer_type", cancerType);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Reachability a = new Reachability(this);
        if(a.isOnline()){
            ATLoginRegister ATL = new ATLoginRegister(Endpoints.register, null, this, data,loading);
            ATL.execute();
        }else{

        }


    }

    public void checkStatus(JSONObject result){
        int resultStatus = 0;
        try {
            resultStatus = result.getInt("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(resultStatus==1){
            finish();
        }else if(resultStatus == 0){
            Utils.alertView("User with this name already exists. Or we some problems on the server.", this);
        }
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
            TextView selectedTime = (TextView)findViewById(R.id.selectedTime);
            selectedTime.setText(new StringBuilder().append(myDay)
                    .append("-").append(myMonth).append("-").append(myYear));
            Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
            birthDate = calendar.getTimeInMillis()/1000;
        }
    };

    public void getRegisterFields(){

        Reachability a = new Reachability(this);
        if(a.isOnline()){
            ProgressBar loading = (ProgressBar)findViewById(R.id.cancerTypeLoading);
            ATGetRegFields ATL = new ATGetRegFields(Endpoints.registerFields,this, loading);
            ATL.execute();
        }else{

        }
    }

    public void recieveRegFields(JSONObject result){
        int resultStatus = 0;
        try {
            resultStatus  = result.getInt("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(resultStatus == 1){
            JSONArray arr= null;
            try {
                arr = result.getJSONArray("cancer");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] data=new String[arr.length()];
            for(int i=0; i<arr.length(); i++){
                data[i] = arr.optString(i);
            }
            initSpinner(data);
        }
    }
}