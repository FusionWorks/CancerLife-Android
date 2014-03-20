package com.platforms.adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.platforms.objects.PatientItem;
import com.platforms.objects.SideEffect;
import com.platforms.R;
import com.platforms.main.PatientsActivity;
import com.platforms.utils.Utils;

import java.util.ArrayList;

/**
 * Created by AGalkin on 11/30/13.
 */
public class PatientsAdapter  extends ArrayAdapter<PatientItem> {
    private ArrayList<PatientItem> data;
    private PatientsActivity activity;
    PatientItem patientItem;
    public PatientsAdapter(PatientsActivity activity, ArrayList<PatientItem> data) {
        super(activity, R.layout.activity_patients ,data);
        this.activity=activity;
        this.data=data;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_item_patient, parent, false);
        patientItem = data.get(position);

        RelativeLayout patientPhoto = (RelativeLayout)rowView.findViewById(R.id.patientPhoto);
        TextView pendingText = (TextView)rowView.findViewById(R.id.pendingText);
        TextView patientName = (TextView)rowView.findViewById(R.id.patientName);
        TextView patientComment = (TextView)rowView.findViewById(R.id.patientComment);
        Button journalButton = (Button)rowView.findViewById(R.id.journalButton);
        Button reportsButton = (Button)rowView.findViewById(R.id.reportsButton);
        Button messageButton = (Button)rowView.findViewById(R.id.sendMessage);
        Button callButton = (Button)rowView.findViewById(R.id.makeCall);

        Log.v(Utils.TAG,"patient "+patientItem.id);

        journalButton.setTag(patientItem.id);
        reportsButton.setTag(patientItem.id);
        messageButton.setTag(patientItem.id);
        callButton.setTag(patientItem.cellPhone);

        if (patientItem.image != null)
            Utils.setBackgroundBySDK(patientPhoto, patientItem.image);
        if (patientItem.pending)
            pendingText.setVisibility(View.VISIBLE);
        patientName.setText(patientItem.name);
        patientComment.setText(patientItem.message);
        setSideEffects(patientItem.sideEffects, rowView);
        return rowView;
    }

    public void setSideEffects(ArrayList<SideEffect> sideEffects, View rowView) {
        LinearLayout spinnersLayout = (LinearLayout)rowView.findViewById(R.id.spinnersLayout);
        LinearLayout moreStatuses = (LinearLayout)rowView.findViewById(R.id.moreStatuses);
        for(SideEffect effect : sideEffects){
            Log.v("CL", "fever "+ effect.name);
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
                RelativeLayout bars =  (RelativeLayout)View.inflate(activity.getApplicationContext(), R.layout.bar_side_effect_patients, null);
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
                            bottomBar.getLayoutParams().width/4*effect.level2,
                            LinearLayout.LayoutParams.MATCH_PARENT));

                    roundandColorCorners(bar,0x99ff0000);
                    bottomBar.addView(bar);
                }

                spinnersLayout.addView(bars,params);
            }
        }
    }

    public void roundandColorCorners(View view, int color){
        RoundRectShape rect = new RoundRectShape(new float[] {15,15, 15,15, 15,15, 15,15}, null, null);
        ShapeDrawable bg = new ShapeDrawable(rect);
        bg.getPaint().setColor(color);
        Utils.setBackgroundBySDK(view, bg);
    }
}
