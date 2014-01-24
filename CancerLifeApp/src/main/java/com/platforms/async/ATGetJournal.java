package com.platforms.async;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.platforms.objects.Comment;
import com.platforms.objects.JournalItem;
import com.platforms.objects.SideEffect;
import com.platforms.R;
import com.platforms.main.JournalActivity;
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
 * Created by AGalkin on 10/28/13.
 */
public class ATGetJournal extends AsyncTask<Void, Void, Void> {
    public String endPoint;
    JournalActivity journalActivity;
    RelativeLayout loading;
    ArrayList<JournalItem> journal = new ArrayList<JournalItem>();

    public ATGetJournal( JournalActivity journalActivity, RelativeLayout loading, String endPoint, ArrayList<JournalItem> journal){
        this.journalActivity = journalActivity;
        this.loading = loading;
        this.endPoint = endPoint;
        if(journal != null){
            this.journal.addAll(journal);
        }
    }

    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
    }
    @Override
    protected Void doInBackground(Void... params) {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(endPoint);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(journalActivity.getApplicationContext());
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
        journalActivity.hadleResponse(journal);
    }

    public void load(JSONObject response) throws JSONException, IOException {
        Log.v("RPS","JSON "+response);

        if(response.getInt("result") == 1){
            JSONArray array = response.getJSONArray("Updates");
            for(int i = 0; array.length() > i; i++){
                JSONObject json = array.getJSONObject(i);
                int id = json.getInt("id");
                String message = json.getString("message");
                String image_path = "moods/"+json.getString("mood");
                String time = Utils.dateConvert(json.getLong("created"));
                Drawable image = null;
                try {
                    image = Drawable.createFromStream(journalActivity.getAssets().open(image_path), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String providers = json.getString("user_circle");
                ArrayList<SideEffect> sideEffects = null;
                if (json.has("sideEffects")){
                    sideEffects = getSideEffects(json, sideEffects);
                }

                ArrayList<Comment> comments = new ArrayList<Comment>();
                if (json.getInt("comments_total") > 0 ) {
                    comments = getComments(json, comments);
                }

                JournalItem item = new JournalItem(id, image, message, time, sideEffects, providers, comments);
                journal.add(item);
            }
        }
    }
    public ArrayList<SideEffect> getSideEffects(JSONObject json, ArrayList<SideEffect> sideEffects) throws JSONException {
        JSONArray arraySideEffects = json.getJSONArray("sideEffects");
        sideEffects = new ArrayList<SideEffect>();
        for(int z=0; arraySideEffects.length() > z; z++){
            JSONObject jsonSideEffects = arraySideEffects.getJSONObject(z);
            String sideEffectName = jsonSideEffects.getString("name");
            int level = jsonSideEffects.getInt("level");
            int level2 = jsonSideEffects.getInt("level2");
            String question1 =  jsonSideEffects.getString("question1");
            String question2 =  jsonSideEffects.getString("question2");
            SideEffect effect = new SideEffect(sideEffectName, level, level2, question1, question2);
            sideEffects.add(effect);
            Log.v("CL","name  ------------------- " +sideEffects.size());
            Log.v("CL","side effects size  ------------------- " +arraySideEffects.length());
        }

        return sideEffects;
    }

    public ArrayList<Comment> getComments (JSONObject json, ArrayList<Comment> comments) throws JSONException, IOException {
        Log.v("CL", "comments "+json.getJSONArray("comments"));
        JSONArray arrayComments = json.getJSONArray("comments");
        for(int y=0; arrayComments.length() > y; y++){
            JSONObject jsonComment = arrayComments.getJSONObject(y);
            Drawable commentPhoto;
            if(jsonComment.getString("photo").length()<1){
                Drawable drawable = new BitmapDrawable(journalActivity.getBaseContext().getResources(), BitmapFactory.decodeResource(journalActivity.getBaseContext().getResources(), R.drawable.no_photo_available));
                commentPhoto = drawable;
            }else{
                commentPhoto = Utils.drawableFromUrl(jsonComment.getString("photo"), journalActivity);
            }
            String comment_name = jsonComment.getString("name");
            String comment_text = jsonComment.getString("message");
            String date = Utils.dateConvert(jsonComment.optLong("created"));
            Comment comment = new Comment(commentPhoto, comment_name, comment_text, date);
            comments.add(comment);

        }

        return comments;
    }
}
