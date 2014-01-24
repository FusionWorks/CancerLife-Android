package com.platforms.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.platforms.objects.PatientItem;
import com.platforms.adapter.PatientsAdapter;
import com.platforms.async.ATPatientList;
import com.platforms.R;
import com.platforms.handlers.PatientHandler;
import com.platforms.menu.NavMenu;
import com.platforms.menu.SlideHolder;
import com.platforms.utils.Endpoints;
import com.platforms.utils.Reachability;
import com.platforms.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by AGalkin on 11/30/13.
 */
public class PatientsActivity extends Activity {
    ArrayList<Button> buttons;
    SlideHolder mSlideHolder;
    NavMenu navMenu;
    PatientHandler patientHandler;
    HashMap<String, ArrayList<PatientItem>> hash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);
        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);
        navMenu = new NavMenu(this, mSlideHolder, 1);
        patientHandler = new PatientHandler();

        Button firstSegment = (Button)findViewById(R.id.firstSegment);
        Button secondSegment = (Button)findViewById(R.id.secondSegment);
        Button thirdSegment = (Button)findViewById(R.id.thirdSegment);
        Button fourthSegment = (Button)findViewById(R.id.fourthSegment);
        Button searchSegment = (Button)findViewById(R.id.searchSegment);
        buttons = new ArrayList<Button>(Arrays.asList(firstSegment, secondSegment, thirdSegment, fourthSegment, searchSegment));
        getPatients();
    }

    public void initiatePatients(ArrayList<PatientItem> patients){
        ListView patientsList = (ListView)findViewById(R.id.patientList);
        PatientsAdapter patientsAdapter = new PatientsAdapter(this, patients);
        patientsList.setAdapter(patientsAdapter);
        patientsAdapter.notifyDataSetChanged();
    }

    public void onTabs(View view){
        for (Button but : buttons){
            String tag = view.getTag().toString();
            if (but.getTag().equals(tag)){
                if(tag.equals("1"))
                    Utils.setBackgroundBySDK(but, this.getResources().getDrawable(R.drawable.left_segment_button_sel));
                else if (tag.equals("5"))
                    Utils.setBackgroundBySDK(but, this.getResources().getDrawable(R.drawable.right_segment_button_sel));
                else
                    but.setBackgroundColor(Color.parseColor("#a5aaac"));
                but.setTextColor(Color.parseColor("#ffffff"));
            }else{
                if(but.getTag().equals("1"))
                    Utils.setBackgroundBySDK(but, this.getResources().getDrawable(R.drawable.left_segment_button_un));
                else if (but.getTag().equals("5"))
                    Utils.setBackgroundBySDK(but, this.getResources().getDrawable(R.drawable.right_segment_button_un));
                else
                    Utils.setBackgroundBySDK(but, this.getResources().getDrawable(R.drawable.central_segment));
                but.setTextColor(Color.parseColor("#a5aaac"));
            }
        }
        int pos = Integer.parseInt(view.getTag().toString());
        if (pos != 5){
            initiatePatients(patientHandler.getPatients(pos));
        }

    }

    public void showMenu(View view){
        mSlideHolder.toggle();
    }

    public void teUserJournal(View view){
        String userId = view.getTag().toString();
        Intent intent = new Intent(this, JournalActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    public void callPatient(View view){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+view.getTag().toString()));
        startActivity(callIntent);
    }

    public void toMessages(View view){
        PatientItem patientItem = null;
        for(PatientItem item : patientHandler.getAllPatients()){
            if (item.id == Integer.parseInt(view.getTag().toString())){
                patientItem = item;
                break;
            }
        }
        Log.v("CL", "patient "+patientItem.name + "patient ima "+patientItem.image);
        String jid = patientItem.jid;
        String name = patientItem.name;
        Bitmap photo = ((BitmapDrawable)patientItem.image).getBitmap();

        Intent intent = new Intent(PatientsActivity.this, MessagesActivity.class);
        intent.putExtra("jid", jid);
        intent.putExtra("name", name);
        try {
            Utils.saveImage(photo, jid);
        } catch (IOException e) {
            e.printStackTrace();
        }

        startActivity(intent);
    }

    public void toReports(View view){
        String userId = view.getTag().toString();
        Intent intent = new Intent(this, ReportsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    public void hadleResponse(ArrayList<PatientItem> patients){
        patientHandler.setPatients(patients);
        initiatePatients(patientHandler.getPatients(1));
    }

    public void getPatients(){
        RelativeLayout loading = (RelativeLayout)findViewById(R.id.loadingAnimationContent);
        Context context = this.getApplicationContext();
        Reachability a = new Reachability(this);
        if(a.isOnline()){
            ATPatientList ATPL = new ATPatientList(this, loading, Endpoints.patients);
            ATPL.execute();
        }
    }

}
