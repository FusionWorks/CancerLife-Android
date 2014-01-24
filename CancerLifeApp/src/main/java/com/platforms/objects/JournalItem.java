package com.platforms.objects;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by AGalkin on 10/26/13.
 */
public class JournalItem {
    public int id;
    public Drawable image;
    public String ownComment;
    public String time;
    public ArrayList<SideEffect> sideEffects;
    public String providers;
    public ArrayList<Comment> comments;

    public JournalItem(int id, Drawable image, String ownComment, String time,  ArrayList<SideEffect> sideEffects, String providers, ArrayList<Comment> comments){
        super();
        this.id = id;
        this.image = image;
        this.ownComment = ownComment;
        this.time = time;
        this.sideEffects = sideEffects;
        this.providers = providers;
        this.comments = comments;

    }

}
