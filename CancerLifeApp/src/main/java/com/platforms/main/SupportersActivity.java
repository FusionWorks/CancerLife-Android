package com.platforms.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.platforms.objects.Supporter;
import com.platforms.async.ATSupporterInvite;
import com.platforms.async.ATSupporters;
import com.platforms.R;
import com.platforms.menu.NavMenu;
import com.platforms.menu.SlideHolder;
import com.platforms.utils.Endpoints;
import com.platforms.utils.Reachability;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AGalkin on 11/6/13.
 */
public class SupportersActivity extends Activity {
    RelativeLayout loading;
    SlideHolder mSlideHolder;
    NavMenu navMenu;
    Dialog addSupporterAlert;
    SharedPreferences prefs;
    String[] circlesTitles;
    Spinner circlesType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supporters);

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);
        navMenu = new NavMenu(this, mSlideHolder, 3);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        loading = (RelativeLayout)findViewById(R.id.loadingAnimationContent);
        Reachability a = new Reachability(this);
        if(a.isOnline()){
            ATSupporters ATS = new ATSupporters(Endpoints.getSupportersList, this, loading);
            ATS.execute();
        }
    }

    public void getSupportersList(ArrayList<Supporter> supporters, String[] circles){
        LinearLayout supportersList = (LinearLayout)findViewById(R.id.middleContent);
        circlesTitles = circles;
        for(Supporter supporter : supporters){
            RelativeLayout supporterItem =  (RelativeLayout) View.inflate(getApplicationContext(), R.layout.supporter_item, null);

            ImageView photo = (ImageView) supporterItem.findViewById(R.id.photo);
            TextView name = (TextView) supporterItem.findViewById(R.id.name);
            TextView email = (TextView) supporterItem.findViewById(R.id.email);
            TextView status = (TextView) supporterItem.findViewById(R.id.status);

            ScaleDrawable sd = new ScaleDrawable(supporter.photo, 0, 100, 100);
            photo.setImageDrawable(sd.getDrawable());
            name.setText(supporter.name);
            email.setText(supporter.email);
            status.setText(supporter.status);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,10,0,0);
            supportersList.addView(supporterItem, params);
        }
        loading.setVisibility(View.GONE);
    }

    public void showMenu(View view){
        mSlideHolder.toggle();
    }

    public void inviteSupporterRequest(View view){

        Log.v("CL", "bum");

        addSupporterAlert = new Dialog(this);
        addSupporterAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addSupporterAlert.setContentView(R.layout.dialog_add_supporter);
        circlesType = (Spinner) addSupporterAlert.findViewById(R.id.circlesSpinner);
        initCirclesSpinner(circlesTitles);
        final EditText emailField = (EditText) addSupporterAlert.findViewById(R.id.emailField);
        Button dialogButton = (Button) addSupporterAlert.findViewById(R.id.sendInvite);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSupporterAlert.findViewById(R.id.failText).setVisibility(View.GONE);
                ProgressBar loadingAnimationContent = (ProgressBar)addSupporterAlert.findViewById(R.id.animationLoading);
                String endPoint = Endpoints.inviteSupporter+prefs.getString("userId","");
                JSONObject data = new JSONObject();
                try {
                    data.put("circle", circlesType.getSelectedItem().toString());
                    data.put("email", emailField.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ATSupporterInvite ATSI = new ATSupporterInvite(endPoint, SupportersActivity.this, loadingAnimationContent, data);
                ATSI.execute();
            }
        });
        addSupporterAlert.show();
    }

    public void initCirclesSpinner(String[] data){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        circlesType.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void inviteSupporterResult(int result){
        if (result == 1) {
            addSupporterAlert.dismiss();
        }else{
            addSupporterAlert.findViewById(R.id.failText).setVisibility(View.VISIBLE);

        }
    }
}
