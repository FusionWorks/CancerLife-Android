package com.platforms.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.platforms.objects.Comment;
import com.platforms.objects.JournalItem;
import com.platforms.objects.SideEffect;
import com.platforms.async.ATGetJournal;
import com.platforms.async.ATNewComment;
import com.platforms.R;
import com.platforms.main.CommentsActivity;
import com.platforms.main.JournalActivity;
import com.platforms.utils.Endpoints;
import com.platforms.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AGalkin on 10/26/13.
 */
public class JournalAdapter extends ArrayAdapter<JournalItem> {
    private ArrayList<JournalItem> data;
    private JournalActivity activity;
    JournalItem journalItem;
    Dialog addCommentDialog;
    public JournalAdapter(JournalActivity activity, ArrayList<JournalItem> data) {
        super(activity, R.layout.activity_journal ,data);
        this.activity=activity;
        this.data=data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_item_journal, parent, false);
        journalItem = data.get(position);
        TextView time = (TextView)rowView.findViewById(R.id.time);
        ImageView image = (ImageView)rowView.findViewById(R.id.status_image);
        TextView ownComment = (TextView)rowView.findViewById(R.id.ownComment);
        TextView providers = (TextView)rowView.findViewById(R.id.providers);

        time.setText(journalItem.time);

        Utils.setBackgroundBySDK(image, journalItem.image);
        if(journalItem.ownComment.length() != 0){
            rowView.findViewById(R.id.triangle).setVisibility(View.VISIBLE);
            ownComment.setVisibility(View.VISIBLE);
            ownComment.setText(journalItem.ownComment);
        }

        if(journalItem.providers.length() != 0){
            providers.setVisibility(View.VISIBLE);
            providers.setText("   "+journalItem.providers);
        }

        ArrayList<SideEffect> sideEffects = new ArrayList<SideEffect>();
        try{
            sideEffects = journalItem.sideEffects;
            setSideEffects(sideEffects, rowView);
        }catch(NullPointerException e){

        }

        ArrayList<Comment> comments = new ArrayList<Comment>();
        comments = journalItem.comments;
        setComments(comments, rowView);

        Button moreComments = (Button)rowView.findViewById(R.id.moreComments);
        moreComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalItem JI = data.get(position);
                Log.v("CL", "ashs "+JI.id);
                toComments(JI.id);
            }
        });

        Button newComment = (Button)rowView.findViewById(R.id.newComment);
        newComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalItem JI = data.get(position);
                Log.v("CL", "ashs "+JI.id);
                newComment(JI.id);
            }
        });

        if (position == data.size()-1){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            int newPage = prefs.getInt("page", 0)+1;
            prefs.edit().putInt("page", newPage).commit();
            activity.curentListPosition = position;
            ATGetJournal ATGJ = new ATGetJournal(activity, activity.loading, Endpoints.journal+prefs.getString("userId","")+"/"+newPage, data);
            ATGJ.execute();
        }

        return rowView;
    }

    public void roundandColorCorners(View view, int color){
       RoundRectShape rect = new RoundRectShape(new float[] {15,15, 15,15, 15,15, 15,15}, null, null);
       ShapeDrawable bg = new ShapeDrawable(rect);
       bg.getPaint().setColor(color);
       Utils.setBackgroundBySDK(view, bg);
    }

    public void setSideEffects(ArrayList<SideEffect> sideEffects, View rowView) {
        LinearLayout spinnersLayout = (LinearLayout)rowView.findViewById(R.id.spinnersLayout);
        LinearLayout moreStatuses = (LinearLayout)rowView.findViewById(R.id.moreStatuses);
        for(SideEffect effect : sideEffects){
            if(effect.level == 0 || effect.name.equals("Fever")){
                TextView status = (TextView)View.inflate(activity.getApplicationContext(), R.layout.side_effect, null);
                Log.v("CL", "name" + effect.name);
                status.setText(effect.name.toUpperCase());
                if (effect.name.equals("Fever"))
                    status.setText(status.getText()+" "+String.valueOf(effect.level));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,5,0);

                moreStatuses.addView(status, params);
            }else{
                RelativeLayout bars =  (RelativeLayout)View.inflate(activity.getApplicationContext(), R.layout.bar_side_effect, null);
                TextView name = (TextView)bars.findViewById(R.id.name);
                TextView middleBarNumber = (TextView)bars.findViewById(R.id.middle_bar_number);
                LinearLayout middleBar = (LinearLayout)bars.findViewById(R.id.middle_bar);
                LinearLayout bottomBar = (LinearLayout)bars.findViewById(R.id.bottom_bar);

                name.setText(effect.name.toUpperCase());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,0,10);

                middleBarNumber.setText(String.valueOf(effect.level));

                TextView midBar = new TextView(activity.getApplicationContext());
                midBar.setLayoutParams(new LinearLayout.LayoutParams(
                        middleBar.getLayoutParams().width/10*effect.level,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                roundandColorCorners(midBar,0x990000ff);

                middleBar.addView(midBar);
                if(effect.level2 != 0){
                    RelativeLayout smallBar = (RelativeLayout)bars.findViewById(R.id.small_bar);
                    smallBar.setVisibility(View.VISIBLE);
                    TextView bar = new TextView(activity.getApplicationContext());
                    bar.setLayoutParams(new LinearLayout.LayoutParams(
                            bottomBar.getLayoutParams().width/10*effect.level2,
                            LinearLayout.LayoutParams.MATCH_PARENT));

                    roundandColorCorners(bar,0x99ff0000);
                    bottomBar.addView(bar);
                }

                spinnersLayout.addView(bars,params);
            }
        }
    }
    public void setComments(ArrayList<Comment> comments, View rowView) {
        RelativeLayout firstComment =  (RelativeLayout)rowView.findViewById(R.id.firstComment);
        RelativeLayout secondComment =  (RelativeLayout)rowView.findViewById(R.id.secondComment);

        for(Comment comment : comments){
            firstComment.setVisibility(View.VISIBLE);
            ImageView photo = (ImageView)firstComment.findViewById(R.id.photo);
            TextView name = (TextView)firstComment.findViewById(R.id.name);
            TextView text = (TextView)firstComment.findViewById(R.id.text);
            TextView time = (TextView)firstComment.findViewById(R.id.time);

            Utils.setBackgroundBySDK(photo, comment.photo);
            name.setText(comment.name);
            text.setText(comment.comment);
            time.setText(comment.time);
            if (comments.size() == 2){
                secondComment.setVisibility(View.VISIBLE);
                ImageView secondPhoto = (ImageView)secondComment.findViewById(R.id.photo);
                TextView secondName = (TextView)secondComment.findViewById(R.id.name);
                TextView secondText = (TextView)secondComment.findViewById(R.id.text);
                TextView secondTime = (TextView)secondComment.findViewById(R.id.time);

                Utils.setBackgroundBySDK(secondPhoto, comment.photo);
                secondName.setText(comment.name);
                secondText.setText(comment.comment);
                secondTime.setText(comment.time);
            }
        }
    }

    public void toComments(int postId){
        Intent intent = new Intent(activity, CommentsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("postId", postId);

        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void newComment(final int postId){
        Log.v("CL", "bum");

        addCommentDialog = new Dialog(activity);
        addCommentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addCommentDialog.setContentView(R.layout.dialog_add_comment);
        final EditText commentText = (EditText) addCommentDialog.findViewById(R.id.commentText);
        Button dialogButton = (Button) addCommentDialog.findViewById(R.id.sendComment);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentDialog.findViewById(R.id.failText).setVisibility(View.GONE);
                ProgressBar loadingAnimationContent = (ProgressBar)addCommentDialog.findViewById(R.id.animationLoading);
                String endPoint = Endpoints.newComment;
                JSONObject data = new JSONObject();
                try {
                    data.put("post_id", postId);
                    data.put("message", commentText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ATNewComment ATSI = new ATNewComment(endPoint, activity, loadingAnimationContent, data, addCommentDialog);
                ATSI.execute();
            }
        });
        addCommentDialog.show();
    }

}
