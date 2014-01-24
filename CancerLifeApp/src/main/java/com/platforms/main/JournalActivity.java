package com.platforms.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.platforms.objects.JournalItem;
import com.platforms.adapter.JournalAdapter;
import com.platforms.async.ATGetJournal;
import com.platforms.R;
import com.platforms.handlers.JournalHandler;
import com.platforms.main.steps.StepOne;
import com.platforms.menu.NavMenu;
import com.platforms.menu.SlideHolder;
import com.platforms.utils.Endpoints;
import com.platforms.utils.Reachability;

import java.util.ArrayList;

/**
 * Created by AGalkin on 10/19/13.
 */
public class JournalActivity extends Activity{

    SlideHolder mSlideHolder;
    NavMenu navMenu;
    JournalHandler journalHandler;
    Context context;
    String userId;
    SharedPreferences prefs;
    public RelativeLayout loading;
    public int curentListPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);
        navMenu = new NavMenu(this, mSlideHolder, 1);
        journalHandler = new JournalHandler();
        loading = (RelativeLayout) findViewById(R.id.loadingAnimationContent);
        curentListPosition = 0;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("patient",false))
            userId = prefs.getString("userId","");
        else {
            Bundle extras = getIntent().getExtras();
            userId = extras.getString("userId");
        }
        getJournal();
    }

    public void initiateJournal(){
        ListView usersList = (ListView)findViewById(R.id.journalList);
        JournalAdapter userAdapter = new JournalAdapter(this, journalHandler.getJournal());
        Log.v("CL", "curentListPosition " + curentListPosition);
        usersList.setAdapter(userAdapter);
        usersList.setSelection(curentListPosition);
        userAdapter.notifyDataSetChanged();
    }

    public void showMenu(View view){
        mSlideHolder.toggle();
    }

    public void toPostEntry(View view){
        Intent intent = new Intent();
        intent.setClass(this, StepOne.class);
        startActivity(intent);
    }

    public void hadleResponse(ArrayList<JournalItem> array){
        journalHandler.setJournal(array);
        initiateJournal();
    }

    public void getJournal(){
        context = this.getApplicationContext();
        int page = 1;
        prefs.edit().putInt("page", page).commit();
        Log.v("CL", "page " +Endpoints.journal+userId+"/"+page);
        Reachability a = new Reachability(this);
        if(a.isOnline()){
            ATGetJournal ATGJ = new ATGetJournal(this, loading, Endpoints.journal+userId+"/"+page, journalHandler.getJournal());
            ATGJ.execute();
        }
    }

    public void reloadJournal(View view){
        curentListPosition = 0;
        getJournal();
    }
}
