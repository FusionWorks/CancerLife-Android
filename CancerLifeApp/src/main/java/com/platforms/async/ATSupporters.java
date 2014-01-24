package com.platforms.async;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.platforms.objects.Supporter;
import com.platforms.R;
import com.platforms.main.SupportersActivity;
import com.platforms.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by AGalkin on 11/6/13.
 */
public class ATSupporters extends AsyncTask<Void, Void, Void> {
    RelativeLayout loading;
    SupportersActivity supportersActivity;
    String endPoint;
    ArrayList<Supporter> resultData;
    Bitmap defaultImage;
    String[] circles;
    public ATSupporters(String endPoint, SupportersActivity supportersActivity, RelativeLayout loading){
        this.supportersActivity = supportersActivity;
        this.endPoint = endPoint;
        this.loading = loading;
    }

    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
        defaultImage = BitmapFactory.decodeResource(supportersActivity.getBaseContext().getResources(), R.drawable.no_photo_available);
    }
    @Override
    protected Void doInBackground(Void... params) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(endPoint);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(supportersActivity.getApplicationContext());
        request.addHeader("token",prefs.getString("loginAuth",""));
        HttpResponse response = null;
        try {
            response = client.execute(request);
            load(new JSONObject(EntityUtils.toString(response.getEntity())));
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("CL", " "+e);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("CL", " "+e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        supportersActivity.getSupportersList(resultData, circles);
    }

    public void load(JSONObject json) throws JSONException, IOException {
        if(json.getString("result").equals("1")){
            JSONArray jsonArray = json.getJSONArray("supporters");
            ArrayList<Supporter> supporters = new ArrayList<Supporter>();
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Drawable photo;
                String status = "0";

                String name = jsonObject.getString("name");
                String userCircle = jsonObject.getString("circle");
                String email = jsonObject.getString("email");
                int friendId = jsonObject.getInt("friend_id");

                if (jsonObject.getInt("status") == 1) status = "Approved";
                else if(jsonObject.getInt("status") == 0) status = "Pending";

                if(jsonObject.getString("public_image").length()<1){
                    Drawable drawable = new BitmapDrawable(supportersActivity.getBaseContext().getResources(), BitmapFactory.decodeResource(supportersActivity.getBaseContext().getResources(), R.drawable.no_photo_available));
                    photo = drawable;
                }else{
                    photo = Utils.drawableFromUrl(jsonObject.getString("photo"), supportersActivity);
                }

                Supporter supporter = new Supporter(name, photo, friendId, userCircle, email, status);
                supporters.add(supporter);

            }
//            for (int i=0; i<json.getJSONArray("circles").length(); i++){
//                circles[i] = json.getJSONArray("circles").getString(i);
//            }
            circles = new String[]{"a","at","at","Ashs"};
            resultData = supporters;
        }
    }


}
