package com.platforms.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gcm.GCMRegistrar;
import com.platforms.async.ATLoginRegister;
import com.platforms.async.XMppSettings;
import com.platforms.R;
import com.platforms.GCMIntentService;
import com.platforms.utils.CancerLife;
import com.platforms.utils.Endpoints;
import com.platforms.utils.Reachability;
import com.platforms.utils.Utils;

import org.jivesoftware.smack.XMPPConnection;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AGalkin on 9/26/13.
 */
public class LoginActivity extends Activity {
    String email;
    String password;
    String userId;
    boolean patient;
    static SharedPreferences prefs;
    Context context;
    private XMPPConnection connection;
    String jid;
    RelativeLayout loadingLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        pushRegister();
        EditText loginField = (EditText)findViewById(R.id.emailField);
        EditText passField = (EditText)findViewById(R.id.passField);
        if(checkIfAlreadyLogedIn()){

        }
//        loginField.setText("mr.charlietest@gmail.com");
        loginField.setText("mike.theoncdoctor@yahoo.com");
        passField.setText("5551212");

    }

    public boolean checkIfAlreadyLogedIn(){

        String status = prefs.getString("email", "");
        if(status.equals("")){
            Log.v("CL", "status" + status + "currentName" + prefs.getString("currentName", null));
            return false;
        }else{
            return true;
        }
    }

    public void recoverClick(View view){
        Utils.alertView("Here will be recover module", this);
    }

    public void rememberMeClick(View view){
    }

    public void signInClick(View view){

        JSONObject data = new JSONObject();
        Log.v("RPS","by");
        EditText loginField = (EditText)findViewById(R.id.emailField);
        EditText passField = (EditText)findViewById(R.id.passField);
        loadingLayout = (RelativeLayout)findViewById(R.id.loadingAnimationContent);
        email = loginField.getText().toString();
        password = passField.getText().toString();
        try {
            data.put("email",email);
            data.put("password",password);
            data.put("android_id", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Reachability a = new Reachability(this);
        if(a.isOnline()){
            Bitmap def_image = BitmapFactory.decodeResource(getResources(), R.drawable.no_photo_available);
            ATLoginRegister ATL = new ATLoginRegister(Endpoints.login,this, null, data, loadingLayout);
            ATL.execute();
        }else{

        }
    }

    public void checkStatus(JSONObject result){
        int resultStatus = 0;
        String loginToken = "";
        int gender = 3;
        String name = "";
        try {
           resultStatus = result.getInt("result");
           loginToken = result.getString("auth_token");
           userId = result.getString("id");
           gender = result.getInt("gender");
           name = result.getString("first_name") + " " + result.getString("last_name");
           int role = result.getJSONArray("roles").getInt(0);
           jid = result.getString("jid");
           if (role == 8){
               patient = false;
           }else{
               patient = true;
           }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(resultStatus == 1){
            Reachability a = new Reachability(this);
            if(a.isOnline()){
                XMppSettings XMS = new XMppSettings(this, jid, password, loadingLayout);
                XMS.execute();
            }

            prefs.edit().putString("email", email).commit();
            prefs.edit().putString("loginAuth", loginToken).commit();
            prefs.edit().putString("userId", userId).commit();
            prefs.edit().putString("name", name).commit();
            prefs.edit().putBoolean("patient", patient).commit();
            Log.v("CL","token "+loginToken);



        }else if(resultStatus == 0){
            Utils.alertView("Wrong username or password", this);
        }
    }

    public void registerClick(View view){
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void hideAlertAnimation(View view){
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.sliding_top_out);
        anim.setInterpolator((new AccelerateDecelerateInterpolator()));
        anim.setFillAfter(true);
        view.setAnimation(anim);
    }

    public void setUpConnection (XMPPConnection connection) {
        this.connection = connection;
        Log.v("CL", "user " + this.connection.getUser());
        loginToXMPP();
        Intent intent = new Intent();
        intent.setClass(this, HomeActivity.class);
        startActivity(intent);
        finish();

    }
    public void haveAProblem(String exception){
        if(exception.equals("-MD")){
            Utils.alertView("Wrong userName or password", this);
        }else if(exception.equals("409")){
            Utils.alertView("You are already logged in", this);
        }else if(exception.equals("0.;")){
            Utils.alertView("Server is offline", this);
        }
    }

    public void loginToXMPP(){
        Log.v("CL","this "+this);
        Log.v("CL","cancer " + this.getApplication());
        CancerLife cancerLife = (CancerLife) this.getApplication();
        cancerLife.setConnection(connection);
    }

    public void unregister(){
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        GCMRegistrar.unregister(this);
    }

    public String pushRegister(){

        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            GCMRegistrar.register(this, GCMIntentService.SENDER_ID);
        } else {
            Log.v("CL", "Already registered : " + regId);
        }
        return regId;
    }
}
