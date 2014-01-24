package com.platforms.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by AGalkin on 9/26/13.
 */

public class Utils {
    public static AlertDialog alertDialog;
    public static String TAG = "CL";
    public static String myName = "";


    public static void alertView(String message, final Activity activity){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        // set title
        alertDialogBuilder.setTitle("Ooops");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        alertDialog.dismiss();
                    }
                });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static Bitmap scaleBitmap(Bitmap avatar, int h, int w){
        double width;
        double height;
        if(avatar.getHeight()<avatar.getWidth()){
            height = h;
            double rate = (double)avatar.getHeight()/(double)h;
            width = avatar.getWidth()/rate;
            Bitmap scaled = Bitmap.createScaledBitmap(avatar, (int)width, (int)height, true);
//	   		 Log.v("RPS","height"+height+"width"+width+"rate "+rate);
            return Bitmap.createBitmap(
                    scaled,
                    scaled.getWidth()/2 - scaled.getHeight()/2,
                    0,
                    scaled.getHeight(),
                    scaled.getHeight()
            );
        }else{
            width = w;
            double rate = (double)avatar.getWidth()/(double)w;
            height = avatar.getHeight()/rate;
            Bitmap scaled = Bitmap.createScaledBitmap(avatar, (int)width, (int)height, true);
//	   		 Log.v("RPS","height"+height+"width"+width+"rate "+rate);
            return Bitmap.createBitmap(
                    scaled,
                    0,
                    scaled.getHeight()/2 - scaled.getWidth()/2,
                    scaled.getWidth(),
                    scaled.getWidth()
            );
        }
//	   	 return Bitmap.createScaledBitmap(avatar, (int)width, (int)height, true);
    }

    public static Bitmap bitmapFromUrl(String url){
        Bitmap x;

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(Endpoints.main+url).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream input = null;
        try {
            input = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        x = BitmapFactory.decodeStream(input);
        return x;
    }

    public static Drawable drawableFromUrl(String url, Activity activity) throws IOException {
        if(url.length()>0){
            Bitmap x;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();
            x = BitmapFactory.decodeStream(input);
            return new BitmapDrawable(activity.getApplicationContext().getResources(), x);
        }else{
            return null;
        }
    }

    public static void setBackgroundBySDK(View view, Drawable bg){
        int sdkVersion = android.os.Build.VERSION.SDK_INT;

        if(sdkVersion < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(bg);
        } else {
            view.setBackground(bg);
        }
    }


    public static String dateConvert(long unixSeconds){
        CharSequence dateOut= DateUtils.getRelativeTimeSpanString(unixSeconds*1000,System.currentTimeMillis(),0L, DateUtils.FORMAT_ABBREV_ALL);
        return dateOut.toString();
    }

    public static Drawable loadImage(Activity activity, String name)
    {
        File f = new File(Environment.getExternalStorageDirectory().toString(), name+".PNG");
        Bitmap bm = Utils.scaleBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()), 100, 100);
        Drawable profilePicture = new BitmapDrawable(activity.getResources(), bm);
        return profilePicture;
    }

    public static void saveImage(Bitmap image, String name) throws IOException {
        OutputStream outStream = null;
        File file = new File(Environment.getExternalStorageDirectory().toString(), name+".PNG");
        outStream = new FileOutputStream(file);
        image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        outStream.flush();
        outStream.close();
    }
}
