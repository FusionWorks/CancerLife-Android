package com.platforms.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.platforms.R;
import com.platforms.adapter.ReportsCustomAdapter;
import com.platforms.async.ATGraphic;
import com.platforms.async.ATReports;
import com.platforms.handlers.ReportsHandler;
import com.platforms.objects.ColorItem;
import com.platforms.objects.ReportItem;
import com.platforms.utils.Endpoints;
import com.platforms.utils.Utils;

import java.util.ArrayList;
/**
 * Created by AGalkin on 12/12/13.
 */
public class ReportsActivity extends Activity {
    TableLayout frequentSymptomsList;
    TableLayout newSymptomsList;
    TableLayout qualityOfLifeList;
    Spinner reportsInterval;
    Spinner reports;
    ReportsHandler reportsHandler;

    TableLayout frequentSymptoms;
    TableLayout newSymptoms;
    TableLayout qualityOfLife;
    String[] reportNames;
    String[] dataInterval;
    RelativeLayout reportsLayout;
    RelativeLayout graphicsLayout;
    boolean graphicsResult;
    RelativeLayout loading;
    String endPoint;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        reportsHandler = new ReportsHandler();

        reportsLayout = (RelativeLayout)findViewById(R.id.reportsLayout);
        graphicsLayout = (RelativeLayout)findViewById(R.id.graphicsLayout);

        Bundle extras = getIntent().getExtras();
        userId = extras.getString("userId");

        frequentSymptomsList = (TableLayout)findViewById(R.id.frequentSymptoms);
        newSymptomsList = (TableLayout)findViewById(R.id.newSymptoms);
        qualityOfLifeList = (TableLayout)findViewById(R.id.qualityOfLife);
        reportsInterval = (Spinner)findViewById(R.id.reportsInterval);
        reports = (Spinner)findViewById(R.id.reports);

        frequentSymptoms = (TableLayout)findViewById(R.id.frequentSymptoms);
        newSymptoms = (TableLayout)findViewById(R.id.newSymptoms);
        qualityOfLife = (TableLayout)findViewById(R.id.qualityOfLife);

        dataInterval = new String[]{"7", "14", "21", "60"};
        reportNames = new String[0];
        initSpinner(reportsInterval, dataInterval);
        endPoint = Endpoints.reports+"/"+userId;
        loading = (RelativeLayout)findViewById(R.id.loadingAnimationContent);
        ATReports ATR = new ATReports(endPoint, this, loading, reportsHandler);
        ATR.execute();

        reports.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                graphicsResult = false;
                if(position==0){
                    reportsLayout.setVisibility(View.VISIBLE);
                    graphicsLayout.setVisibility(View.GONE);
                    fetchDatas(Integer.valueOf(reportsInterval.getSelectedItem().toString()));
                }else{
                    reportsLayout.setVisibility(View.GONE);
                    graphicsLayout.setVisibility(View.VISIBLE);
                    endPoint = Endpoints.reports+"/"+userId+"/"+position;
                    ATGraphic ATG = new ATGraphic(endPoint, ReportsActivity.this, loading, reportsHandler);
                    ATG.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        reportsInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (reportNames.length == 9 && reports.getSelectedItem().toString().equals("Reports"))
                    fetchDatas(Integer.valueOf(dataInterval[position]));
                else if (graphicsResult){
                    showGraphicAt(Integer.valueOf(dataInterval[position]));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void initSpinner(Spinner spinner, String[] data){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void initListView(TableLayout table, ArrayList<ReportItem> items){
        ReportsCustomAdapter adapter = new ReportsCustomAdapter(this, table, items);
        adapter.addReportsItems();
    }

    public void fetchDatas(int selectedInterval){

        ArrayList<ReportItem> reports = reportsHandler.getFrequentSymptoms().get(selectedInterval);
        initListView(frequentSymptoms, reports);
        reports = reportsHandler.getNewSymptoms().get(selectedInterval);
        initListView(newSymptoms, reports);
        reports = reportsHandler.getQualityOfLife().get(selectedInterval);
        initListView(qualityOfLife, reports);
    }

    public void dataReceived(String[] reportNames, boolean result){
        if (result){
            this.reportNames = reportNames;
            for(String a : reportNames)
                Log.v(Utils.TAG, "title " + a );
            initSpinner(reports, reportNames);
            String selectedInterval = reportsInterval.getSelectedItem().toString();
            fetchDatas(Integer.valueOf(selectedInterval));
        }
    }

    public void graphicsReceived(boolean graphicsResult){
        this.graphicsResult = graphicsResult;
        if (graphicsResult){
            showGraphicAt(Integer.parseInt(reportsInterval.getSelectedItem().toString()));
            Log.v("CL", "ashas "+Integer.parseInt(reportsInterval.getSelectedItem().toString()));
        }
    }

    public void showGraphicAt(int selectedInterval){
        Log.v("CL", "repo "+reportsHandler.getGraphic().get(selectedInterval));
        ImageView graphic = (ImageView)findViewById(R.id.graphic);
        graphic.setImageDrawable(reportsHandler.getGraphic().get(selectedInterval));
        if(reportsHandler.getListItems() != null){
            LinearLayout layoutMaster = (LinearLayout)findViewById(R.id.layoutMaster);
            layoutMaster.removeAllViews();
            ArrayList<ColorItem> items = reportsHandler.getListItems().get(selectedInterval);
            Log.v("CL", "items "+ items.get(0).text);
            for(ColorItem item : items){
                LinearLayout LL = new LinearLayout(this);
                LL.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                LL.setLayoutParams(LLParams);

                LinearLayout.LayoutParams forColor = new LinearLayout.LayoutParams(35,35);
                forColor.setMargins(0,0,5,0);

                ImageView color = new ImageView(this);
                color.setLayoutParams(forColor);
                color.setBackgroundColor(Color.parseColor(item.color));
                LL.addView(color);

                TextView text = new TextView(this);
                text.setLayoutParams(LLParams);
                text.setText(item.text);
                LL.addView(text);

                layoutMaster.addView(LL);
            }
        }
    }
}
