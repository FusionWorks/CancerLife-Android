package com.platforms.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.platforms.R;
import com.platforms.menu.NavMenu;
import com.platforms.menu.SlideHolder;
public class HomeActivity extends Activity {
    SlideHolder mSlideHolder;
    NavMenu navMenu;
    String offline;
    boolean isPatient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);
        Button journal = (Button)findViewById(R.id.journalButton);
        navMenu = new NavMenu(this, mSlideHolder, 0);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isPatient = prefs.getBoolean("patient", false);
        if(!isPatient){
            journal.setText("Patients");
            Button invites = (Button)findViewById(R.id.invitesButton);
            invites.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void showMenu(View view){
        mSlideHolder.toggle();
    }

    public void toJournal(View view){
        if(isPatient){
            Intent intent = new Intent();
            intent.setClass(this, JournalActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent();
            intent.setClass(this, PatientsActivity.class);
            startActivity(intent);
        }
    }

    public void toMessages(View view){
        Intent intent = new Intent();
        intent.setClass(this, ChatsListActivity.class);
        startActivity(intent);
    }

    public void toInvites(View view){
        Intent intent = new Intent();
        intent.setClass(this, SupportersActivity.class);
        startActivity(intent);
    }

}

