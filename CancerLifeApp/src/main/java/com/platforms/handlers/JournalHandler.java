package com.platforms.handlers;

import com.platforms.objects.JournalItem;
import java.util.ArrayList;


 public class JournalHandler {
     ArrayList<JournalItem> journal;

     public ArrayList<JournalItem> getJournal(){
        return journal;
     }

     public void setJournal(ArrayList<JournalItem> journal){
        this.journal=journal;

     }


 }
