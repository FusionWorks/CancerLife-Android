package com.platforms.handlers;

import android.graphics.drawable.Drawable;

import com.platforms.objects.ColorItem;
import com.platforms.objects.ReportItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AGalkin on 12/12/13.
 */
public class ReportsHandler {
    HashMap<Integer,ArrayList<ReportItem>> frequentSymptoms;
    HashMap<Integer,ArrayList<ReportItem>> newSymptoms;
    HashMap<Integer,ArrayList<ReportItem>> qualityOfLife;
    HashMap<Integer,Drawable> graphics;
    HashMap<Integer,ArrayList<ColorItem>> listItems;

    public HashMap<Integer,ArrayList<ReportItem>> getFrequentSymptoms(){

        return frequentSymptoms;
    }

    public void setFrequentSymptoms(HashMap<Integer,ArrayList<ReportItem>> frequentSymptoms){
        this.frequentSymptoms=frequentSymptoms;

    }

    public HashMap<Integer,ArrayList<ReportItem>> getNewSymptoms(){

        return newSymptoms;
    }

    public void setNewSymptoms(HashMap<Integer,ArrayList<ReportItem>> newSymptoms){
        this.newSymptoms=newSymptoms;

    }

    public HashMap<Integer,ArrayList<ReportItem>> getQualityOfLife(){

        return qualityOfLife;
    }

    public void setQualityOfLife(HashMap<Integer,ArrayList<ReportItem>> qualityOfLife){
        this.qualityOfLife=qualityOfLife;

    }

    public HashMap<Integer,Drawable> getGraphic(){

        return graphics;
    }

    public void setGraphic(HashMap<Integer,Drawable> graphics){
        this.graphics=graphics;

    }

    public void setListItems (HashMap<Integer, ArrayList<ColorItem>> listItems){
        this.listItems = listItems;
    }

    public HashMap<Integer, ArrayList<ColorItem>> getListItems(){
        return listItems;
    }

}
