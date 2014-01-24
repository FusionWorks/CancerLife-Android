package com.platforms.async;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.platforms.R;
import com.platforms.main.ProfileActivity;
import com.platforms.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * Created by AGalkin on 11/4/13.
 */
public class ATPhotoUpload extends AsyncTask<Void, Void, Void> {

    RelativeLayout loading;
    String endPoint;
    ProfileActivity profile;
    Bitmap photo;
    Bitmap public_image;
    Bitmap private_image;
    String photoType;

    JSONObject responseJson;
    public ATPhotoUpload(String endPoint, ProfileActivity profile, Bitmap photo, String photoType, RelativeLayout loading){
        this.profile = profile;
        this.endPoint = endPoint;
        this.loading = loading;
        this.photo = photo;
        this.photoType = photoType;

    }

    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
        this.public_image =  BitmapFactory.decodeResource(profile.getApplicationContext().getResources(), R.drawable.tap_to_select_image);
        this.private_image =  BitmapFactory.decodeResource(profile.getApplicationContext().getResources(), R.drawable.tap_to_select_image);
    }
    @Override
    protected Void doInBackground(Void... params) {

        if (photo != null){
            uploadImage(photo, endPoint);
        }else{
            getPhoto(endPoint);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        loading.setVisibility(View.GONE);
        profile.photoResponse(public_image, private_image);
    }

    public void getPhoto(String endPoint){

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(endPoint);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(profile.getApplicationContext());
        request.addHeader("token",prefs.getString("loginAuth",""));
        HttpResponse response = null;
        try {
            response = client.execute(request);

            responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
            Log.v("CL","responseJson "+responseJson);
            if (responseJson.getString("result").toString().equals("1")){
                if(responseJson.getString("public_image").toString().length()>0){
                    public_image = Utils.bitmapFromUrl(responseJson.getString("public_image").toString());
                }
                if(responseJson.getString("private_image").toString().length()>0){
                    private_image = Utils.bitmapFromUrl(responseJson.getString("private_image").toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void uploadImage(Bitmap bitmap, String endPoint){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); //compress to which format you want.
        byte [] byte_arr = stream.toByteArray();
        String image_str = Base64.encodeToString(byte_arr, 0);
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

         nameValuePairs.add(new BasicNameValuePair(photoType,image_str));

        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(endPoint);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httppost.setHeader("Content-type", "и прав/json");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(profile.getApplicationContext());
            httppost.addHeader("token",prefs.getString("loginAuth",""));
            HttpResponse response = httpclient.execute(httppost);

            Log.v("CL","responseJson "+responseJson);
            responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
            if (responseJson.getString("result").toString().equals("1")){
                if(responseJson.getString("public_image").toString().length()>0){
                    public_image = Utils.bitmapFromUrl(responseJson.getString("public_image").toString());
                    saveImage(bitmap);
                }
                if(responseJson.getString("private_image").toString().length()>0){
                    private_image = Utils.bitmapFromUrl(responseJson.getString("private_image").toString());
                }

            }
            Log.v("CL","after all "+responseJson);
        }catch(Exception e){
            Log.v("CL","ERROR"+e);
            System.out.println("Error in http connection "+e.toString());
        }
    }

    public void saveImage(Bitmap image) throws IOException {
        OutputStream outStream = null;
        File file = new File(Environment.getExternalStorageDirectory().toString(), "profile.PNG");
        outStream = new FileOutputStream(file);
        image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        outStream.flush();
        outStream.close();
    }

}
