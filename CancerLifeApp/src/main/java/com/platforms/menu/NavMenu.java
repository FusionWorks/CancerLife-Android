package com.platforms.menu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.platforms.R;
import com.platforms.main.ChatsListActivity;
import com.platforms.main.HomeActivity;
import com.platforms.main.JournalActivity;
import com.platforms.main.PatientsActivity;
import com.platforms.main.ProfileActivity;
import com.platforms.main.ReportsActivity;
import com.platforms.main.SupportersActivity;
import com.platforms.utils.Utils;

import java.io.File;

public class NavMenu {
	public SlideHolder mSlideHolder;
	public Activity screen;
	private int position_id;
	//public static
    String[] menuContent;
    String[] buttonTitles;
    SharedPreferences prefs;

	public NavMenu(Activity in_screen, SlideHolder in_mSlideHolder, int in_position_id){
		Log.v("NavMenu", "NAVMENU CREATED");
		mSlideHolder = in_mSlideHolder;
        screen = in_screen;
		position_id = in_position_id;
        prefs = PreferenceManager.getDefaultSharedPreferences(screen);
		initiateMenu();
	}
	
	public View getView(int id){
		return screen.findViewById(id);
	}
	public void initiateMenu(){

        if (prefs.getBoolean("patient", false)){
            menuContent = new String[]{
                    "Home",
                    "Journal",
                    "Messages",
                    "Invites",
                    "My Reports"
            };
            buttonTitles = new String[]{
                    "",
                    "New",
                    "New",
                    "Add",
                    ""
            };
        }else{
            menuContent = new String[]{
                    "Home",
                    "Patients",
                    "Messages"
            };
            buttonTitles = new String[]{
                    "",
                    "New",
                    "New"
            };
        }


		int[] menuIcons = {
//
						    };
		ListView  myList = (ListView) screen.findViewById(R.id.menu);
		// Populate the list, through the adapter
		MenuItem[] menuItems = new MenuItem[menuContent.length];
        for(int i = 0; i < menuContent.length; i++){
        	menuItems[i] = (new MenuItem(menuContent[i], 0, buttonTitles[i]));
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(screen.getApplicationContext());
        RelativeLayout profile = (RelativeLayout)screen.findViewById(R.id.profile);
        ImageView profilePicture = (ImageView)profile.findViewById(R.id.profilePicture);
        TextView profileName = (TextView) profile.findViewById(R.id.profileName);
        profileName.setText(prefs.getString("name",""));
        Utils.setBackgroundBySDK(profilePicture, Utils.loadImage(screen, "profile"));
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(screen, ProfileActivity.class);
                screen.startActivity(i);
                screen.finish();
                mSlideHolder.close();
            }
        });

        MenuAdapter newsEntryAdapter = new MenuAdapter(screen, menuItems, R.layout.list_item_menu, this);
        myList.setAdapter(newsEntryAdapter);

		myList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//TextView agencyText = (TextView) screen.findViewById(R.id.agencyText);
				//agencyText.setText(LocationListActivity.currentSelectedLocation);
                    Log.v("CL",""+parent.getTag());
					Log.d("Double map", "itemClick: position = " + position + ", id = " + id);
					selectedItem(position);
			}
		});

    }
	public void selectedItem(int position){
		switch(position){
			case 0:
                if(position != position_id){
                    Intent i = new Intent(screen, HomeActivity.class);
                    screen.startActivity(i);
                    screen.finish();
                }
                Log.v("CL","pos "+position);
                mSlideHolder.close();
				break;
			case 1:
                if(position != position_id){
                    Intent i;
                    if(prefs.getBoolean("patient", false))
                        i = new Intent(screen, JournalActivity.class);
                    else
                        i = new Intent(screen, PatientsActivity.class);

                    screen.startActivity(i);
                    screen.finish();
                }
                Log.v("CL","pos "+position);
                mSlideHolder.close();
				break;
			case 2:
                if(position != position_id){
                    Intent i = new Intent(screen, ChatsListActivity.class);
                    screen.startActivity(i);
                    screen.finish();
                }
                mSlideHolder.close();
				break;
            case 3:
                if(position != position_id){
                    Intent i = new Intent(screen, SupportersActivity.class);
                    screen.startActivity(i);
                    screen.finish();
                }
                mSlideHolder.close();
                break;
            case 4:
                if(position != position_id){
                    Intent intent = new Intent(screen, ReportsActivity.class);
                    intent.putExtra("userId", prefs.getString("userId",""));
                    screen.startActivity(intent);
                    screen.finish();
                }
                mSlideHolder.close();
                break;
		}
	}

	public void showMenu(View view){
		Log.v("DoubleMap", "Show menu");
		mSlideHolder.toggle();
		/*
		if (mSlideHolder.isOpened()) {
			Log.d("showMenu", "isOpen");
			mSlideHolder.close();
		}else{
			Log.d("showMenu", "isClosed");
			mSlideHolder.open();
		}
		*/
	}


}
