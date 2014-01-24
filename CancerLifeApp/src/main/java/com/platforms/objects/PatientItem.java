package com.platforms.objects;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by AGalkin on 11/30/13.
 */
public class PatientItem {
    public int id;
    public String jid;
    public String name;
    public String cellPhone;
    public Drawable image;
    public boolean pending;
    public int severity;
    public ArrayList<String> cancerTypes;
    public ArrayList<SideEffect> sideEffects;
    public int time;
    public String message;

    public PatientItem(int id,
                       String jid,
                       String name,
                       String cellPhone,
                       Drawable image,
                       boolean pending,
                       int severity,
                       ArrayList<String> cancerTypes,
                       ArrayList<SideEffect> sideEffects,
                       int time,
                       String message){
        super();
        this.id = id;
        this.jid = jid;
        this.name = name;
        this.cellPhone = cellPhone;
        this.image = image;
        this.pending = pending;
        this.severity = severity;
        this.cancerTypes = cancerTypes;
        this.sideEffects = sideEffects;
        this.time = time;
        this.message = message;

    }
}
