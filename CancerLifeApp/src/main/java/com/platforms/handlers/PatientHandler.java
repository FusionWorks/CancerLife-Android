package com.platforms.handlers;

import com.platforms.objects.PatientItem;

import java.util.ArrayList;

/**
 * Created by AGalkin on 11/30/13.
 */
public class PatientHandler {

    ArrayList<PatientItem> patients;

    public ArrayList<PatientItem> getPatients(int severity){
        ArrayList<PatientItem> sorted = new ArrayList<PatientItem>();
        for(PatientItem item : patients){
            if(item.severity == severity){
                sorted.add(item);
            }
        }
        return sorted;
    }

    public void setPatients(ArrayList<PatientItem> patients){
        this.patients=patients;

    }

    public ArrayList<PatientItem> getAllPatients(){
        return patients;
    }
}
