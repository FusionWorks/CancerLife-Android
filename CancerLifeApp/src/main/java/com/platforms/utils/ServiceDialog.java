package com.platforms.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
* Created by AGalkin on 12/14/13.
*/
public class ServiceDialog extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        String body = "";
        String title = "";
        if(intent.hasExtra("body")) {
            body = intent.getStringExtra("body");
            title = intent.getStringExtra("title");

        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setMessage(body);
        alert.setPositiveButton(android.R.string.ok,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ServiceDialog.this.finish();
                }
            });
        alert.show();
    }

}