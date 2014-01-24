package com.platforms.adapter;

import android.graphics.Color;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.platforms.objects.ReportItem;
import com.platforms.main.ReportsActivity;
import com.platforms.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by AGalkin on 12/12/13.
 */
public class ReportsCustomAdapter {
    public TableLayout table;
    public ArrayList<ReportItem> reports;
    public ReportsActivity activity;
    public ReportsCustomAdapter(ReportsActivity activity, TableLayout table, ArrayList<ReportItem> reports){
        super();
        this.table = table;
        this.reports = reports;
        this.activity = activity;
    }

    public void addReportsItems(){
        for(int i=0; i<table.getChildCount(); i++){
            if(i!=0)
                table.removeViewAt(i);
        }

        for(ReportItem item : reports){
            TableRow tableRow = new TableRow(activity);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            TextView symptom = new TextView(activity);
            TextView avgScore = new TextView(activity);
            TextView precent = new TextView(activity);
            TextView current = new TextView(activity);
            TextView previous = new TextView(activity);
            TextView change = new TextView(activity);

            ArrayList<TextView> textViews = new ArrayList<TextView>(Arrays.asList(symptom, avgScore, precent, current, previous, change));
            for(TextView textView : textViews){
                textView.setPadding(5,5,5,5);
                textView.setTextColor(Color.BLACK);
            }
            if (!item.name.equals("") && item.avgScore != 0.0){

                Log.v(Utils.TAG, "item name " + item.name + "avg score " + item.avgScore);
                symptom.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.4f));
                avgScore.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.4f));
                precent.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.2f));

                symptom.setText(item.name);
                avgScore.setText(String.valueOf(item.avgScore));
                precent.setText(String.valueOf(item.percentage));

                tableRow.addView(symptom);
                tableRow.addView(avgScore);
                tableRow.addView(precent);

            }else if(!item.name.equals("")){

                Log.v(Utils.TAG, "item name " + item.name);
                symptom.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                symptom.setText(item.name);
                tableRow.addView(symptom);

            }else{

                Log.v(Utils.TAG, "item current " + item.current);
                current.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.33f));
                previous.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.33f));
                change.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.33f));

                current.setText(String.valueOf(item.current));
                previous.setText(String.valueOf(item.previous));
                change.setText(item.change);

                tableRow.addView(current);
                tableRow.addView(previous);
                tableRow.addView(change);
            }
                table.addView(tableRow);
        }

    }
}
