package com.platforms.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.platforms.main.RegisterActivity;
import com.platforms.utils.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AGalkin on 10/12/13.
 */
public class ATGetRegFields extends AsyncTask<Void, Void, Void> {
    public String endPoint;
    public RegisterActivity register;
    ProgressBar loading;
    JSONObject resultData;

    public ATGetRegFields(String endPoint, RegisterActivity register, ProgressBar loading){
        this.register = register;
        this.endPoint = endPoint;
        this.loading = loading;
    }
    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
    }
    @Override
    protected Void doInBackground(Void... params) {

        JsonParser JP = new JsonParser();
        Log.v("RPS", "no active invites");
        try {
            load(JP.parseObject(endPoint));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);

        loading.setVisibility(View.GONE);
        register.recieveRegFields(resultData);

    }

    public void load(JSONObject json) throws JSONException{
        Log.v("RPS","JSON "+json);
        resultData = json;

    }
}
