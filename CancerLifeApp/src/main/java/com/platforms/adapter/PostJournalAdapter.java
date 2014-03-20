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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.platforms.R;
import com.platforms.main.steps.StepTwo;
import com.platforms.objects.Level;
import com.platforms.objects.PostItem;
import com.platforms.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by AGalkin on 11/9/13.
 */
public class PostJournalAdapter extends ArrayAdapter<PostItem> {
    private ArrayList<PostItem> data;
    private StepTwo activity;
    PostItem item;
    Button spinner;
    public PostJournalAdapter(StepTwo activity, ArrayList<PostItem> data) {
        super(activity, R.layout.activity_step_two ,data);
        this.activity=activity;
        this.data=data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.new_post_item, parent, false);
        item = data.get(position);

        TextView name = (TextView) rowView.findViewById(R.id.title);
        ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
        RelativeLayout levelOne = (RelativeLayout) rowView.findViewById(R.id.levelOne);
        RelativeLayout levelTwo = (RelativeLayout) rowView.findViewById(R.id.levelTwo);
        TextView levelOneTitle = (TextView) rowView.findViewById(R.id.levelOneTitle);
        TextView levelTwoTitle = (TextView) rowView.findViewById(R.id.levelTwoTitle);

        final TextView minLevel = (TextView) levelOne.findViewById(R.id.minLevel);
        final TextView minLevel2 = (TextView) levelTwo.findViewById(R.id.minLevel);
        TextView maxLevel = (TextView) levelOne.findViewById(R.id.maxLevel);
        TextView maxLevel2 = (TextView) levelTwo.findViewById(R.id.maxLevel);
        TextView minusButton = (TextView) levelOne.findViewById(R.id.minusButton);
        TextView minusButton2 = (TextView) levelTwo.findViewById(R.id.minusButton);
        TextView plusButton = (TextView) levelOne.findViewById(R.id.plusButton);
        TextView plusButton2 = (TextView) levelTwo.findViewById(R.id.plusButton);

        final TextView toolTip = (TextView) rowView.findViewById(R.id.toolTip);
        TextView question1Title = (TextView) rowView.findViewById(R.id.question1);
        TextView question2Title = (TextView) rowView.findViewById(R.id.question2);
        spinner = (Button) rowView.findViewById(R.id.spinner);

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(minLevel.getText().toString());
                if(number > 1){
                    number -= 1;
                    minLevel.setText(""+number);
                    Level level = item.level1;
                    Level newLevel = new Level(level.question, level.min, level.max, level.step, number, level.options);
                    PostItem b = new PostItem(item.name, item.order, newLevel, item.level2, item.question1Type, item.question1Text, item.question2Type, item.question2Text, item.question2options);
                    data.set(position, b);
                    Collections.copy(StepTwo.selectedArray, data);
                }
            }
        });

        minusButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(minLevel2.getText().toString());
                if(number > 1){
                    number -= 1;
                    minLevel2.setText(""+number);
                    if(item.level2.options.size()>1)
                        toolTip.setText(item.level2.options.get(number-1));
                    Level level = item.level2;
                    Level newLevel = new Level(level.question, level.min, level.max, level.step, number, level.options);
                    PostItem b = new PostItem(item.name, item.order, item.level1, newLevel, item.question1Type, item.question1Text, item.question2Type, item.question2Text, item.question2options);
                    data.set(position, b);
                    Collections.copy(StepTwo.selectedArray, data);
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(minLevel.getText().toString());
                if(number < item.level1.max){
                    number += 1;
                    minLevel.setText(""+number);
                    Level level = item.level1;
                    Level newLevel = new Level(level.question, level.min, level.max, level.step, number, level.options);
                    PostItem b = new PostItem(item.name, item.order, item.level1, newLevel, item.question1Type, item.question1Text, item.question2Type, item.question2Text, item.question2options);
                    data.set(position, b);
                    Collections.copy(StepTwo.selectedArray, data);
                }
            }
        });

        plusButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(minLevel2.getText().toString());
                if(number < item.level2.max){
                    number += 1;
                    minLevel2.setText(""+number);
                    if(item.level2.options.size()>1)
                        toolTip.setText(item.level2.options.get(number-1));
                    Level level = item.level2;
                    Level newLevel = new Level(level.question, level.min, level.max, level.step, number, level.options);
                    PostItem b = new PostItem(item.name, item.order, item.level1, newLevel, item.question1Type, item.question1Text, item.question2Type, item.question2Text, item.question2options);
                    data.set(position, b);
                    Collections.copy(StepTwo.selectedArray, data);
                }
            }
        });


        roundandColorCorners(name, 0x99cccccc);

        item = data.get(position);
        name.setText(item.name);
        try{
            if(!item.level1.question.equals("")){
                levelOneTitle.setText(item.level1.question);
                maxLevel.setText(String.valueOf(item.level1.max));
                minLevel.setText(String.valueOf(item.level1.defaultNum));
            }
        }catch(NullPointerException e){
            levelOne.setVisibility(View.GONE);
            levelOneTitle.setVisibility(View.GONE);
        }
        try{
            if(!item.level2.question.equals("")){
                levelTwoTitle.setText(item.level2.question);
                maxLevel2.setText(String.valueOf(item.level2.max));
                minLevel2.setText(String.valueOf(item.level2.defaultNum));
                if(item.level2.options.size()>1){
                    toolTip.setVisibility(View.VISIBLE);
                    toolTip.setText(item.level2.options.get(item.level2.defaultNum-1));
                }
            }
        }catch (NullPointerException e){
            levelTwo.setVisibility(View.GONE);
            levelTwoTitle.setVisibility(View.GONE);
        }

        if(item.question1Text.length()>1){
            question1Title.setText(item.question1Text);
        }else{
            LinearLayout question1Layout = (LinearLayout) rowView.findViewById(R.id.question1Layout);
            question1Layout.setVisibility(View.GONE);
        }

        if(item.question2Text.length()>1){
            question2Title.setText(item.question2Text);
            activity.painOptions = item.question2options;
            Log.v("CL","pa "+ activity.painOptions);

        }else{
            LinearLayout question2Layout = (LinearLayout) rowView.findViewById(R.id.question2Layout);
            question2Layout.setVisibility(View.GONE);

        }
        return rowView;
    }

    public void roundandColorCorners(View view, int color){
        RoundRectShape rect = new RoundRectShape(new float[] {15,15, 15,15, 15,15, 15,15}, null, null);
        ShapeDrawable bg = new ShapeDrawable(rect);
        bg.getPaint().setColor(color);
        Utils.setBackgroundBySDK(view, bg);
    }
}
