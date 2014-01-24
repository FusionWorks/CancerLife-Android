package com.platforms.objects;

import android.graphics.drawable.Drawable;

/**
 * Created by AGalkin on 10/26/13.
 */
public class Comment {
    public Drawable photo;
    public String name;
    public String comment;
    public String time;

    public Comment(Drawable photo, String name, String comment,String time){
        super();
        this.photo = photo;
        this.name = name;
        this.comment = comment;
        this.time = time;
    }


}
