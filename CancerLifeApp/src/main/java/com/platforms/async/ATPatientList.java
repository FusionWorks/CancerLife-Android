package com.platforms.async;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.platforms.R;
import com.platforms.objects.PatientItem;
import com.platforms.objects.SideEffect;
import com.platforms.main.PatientsActivity;
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
 * Created by AGalkin on 11/30/13.
 */
public class ATPatientList extends AsyncTask<Void, Void, Void> {
    public String endPoint;
    PatientsActivity patientsActivity;
    RelativeLayout loading;
    ArrayList<PatientItem> patients = new ArrayList<PatientItem>();

    public ATPatientList( PatientsActivity patientsActivity, RelativeLayout loading, String endPoint){
        this.patientsActivity = patientsActivity;
        this.loading = loading;
        this.endPoint = endPoint;
    }


    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
    }
    @Override
    protected Void doInBackground(Void... params) {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(endPoint);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(patientsActivity.getApplicationContext());
        request.addHeader("token",prefs.getString("loginAuth",""));
        HttpResponse response = null;
        try {
            response = client.execute(request);
            load(new JSONObject(EntityUtils.toString(response.getEntity())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        loading.setVisibility(View.GONE);
        patientsActivity.hadleResponse(patients);
    }

    private void load(JSONObject jsonObject) throws JSONException, IOException {
        Log.v("CL", "result " + jsonObject);
        if (jsonObject.getInt("result") == 1) {
            JSONArray patientsArray = jsonObject.getJSONArray("patients");
            for (int i = 0; i<patientsArray.length(); i++){
                JSONObject patient = patientsArray.getJSONObject(i);
                int id = patient.getInt("id");
                String jid = patient.getString("jid");
                String name = patient.getString("name");
                String cellPhone = patient.getString("cell_phone");
                Drawable image = Utils.drawableFromUrl(patient.getString("public_image"), patientsActivity);
                image = replaceIfNull(image, patientsActivity.getResources().getDrawable( R.drawable.no_photo_available ));

                boolean pending = patient.getInt("pending") == 1;
                int severity = patient.getInt("severity");

                ArrayList<String> cancerTypes = new ArrayList<String>();
                for (int y = 0; y<patient.getJSONArray("cancer_types").length(); y++)
                    cancerTypes.add(patient.getJSONArray("cancer_types").getString(y));

                ArrayList<SideEffect> sideEffects = new ArrayList<SideEffect>();
                for (int z = 0; z<patient.getJSONArray("side_effects").length(); z++){
                    JSONObject object = patient.getJSONArray("side_effects").getJSONObject(z);
                    String sideEffectName = object.getString("name");
                    int level = object.getInt("level");
                    int level2 = object.getInt("level2");
                    SideEffect sideEffect = new SideEffect(sideEffectName, level, level2, null, null);
                    sideEffects.add(sideEffect);
                }
                String message = patient.getString("message");
                int time = patient.getInt("time");
                PatientItem patientObject = new PatientItem(id,
                        jid,
                        name,
                        cellPhone,
                        image,
                        pending,
                        severity,
                        cancerTypes,
                        sideEffects,
                        time,
                        message);

                patients.add(patientObject);
            }
        }
    }

    public static <T> T replaceIfNull(T objectToCheck, T defaultValue) {
        return objectToCheck==null ? defaultValue : objectToCheck;
    }

}
