package com.platforms.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.platforms.R;
import com.platforms.main.LoginActivity;
import com.platforms.main.RegisterActivity;
import com.platforms.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ATLoginRegister extends AsyncTask<Void, Void, Void> {
    public String endPoint;
    public LoginActivity login;
    public RegisterActivity register;
    JSONObject data;
    RelativeLayout loading;
    JSONObject resultData;
    Bitmap defaultImage;
    public ATLoginRegister(String endPoint, LoginActivity login,RegisterActivity register, JSONObject data, RelativeLayout loading){
        this.register = register;
        this.login = login;
        this.data = data;
        this.endPoint = endPoint;
        this.loading = loading;

    }
    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
        defaultImage = BitmapFactory.decodeResource(login.getApplicationContext().getResources(), R.drawable.no_photo_available);
    }
    @Override
    protected Void doInBackground(Void... params) {

        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = null;

        httppost = new HttpPost(endPoint);
        Log.v("CancerLife", "url "+ endPoint);

        //passes the results to a string builder/entity
        StringEntity se = null;

        try {
            se = new StringEntity(data.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.v("CancerLife", "exception" + e);
        }
        Log.v("CancerLife", "POST data" + data);

        httppost.setEntity(se);
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();

        try {
            HttpResponse response = httpclient.execute(httppost);

           load(new JSONObject(EntityUtils.toString(response.getEntity())));
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("CancerLife", "exception" + e);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        loading.setVisibility(View.GONE);
        if(login!=null){
            login.checkStatus(resultData);
        }else if(register!=null){
            register.checkStatus(resultData);
        }
    }

    public void load(JSONObject json) throws JSONException {
        Log.v("RPS","JSON "+json);
        resultData = json;
        if(resultData.getInt("result") == 1){
            if (login != null) {
               String imageUrl = resultData.getString("public_image");
                Log.v("CL", "imageUrl "+imageUrl);
                if(imageUrl.length() != 0){
                    defaultImage = Utils.bitmapFromUrl(imageUrl);
                }
                Log.v("CL", "defaultImage "+defaultImage);
                try {
                    Utils.saveImage(defaultImage, "profile");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

