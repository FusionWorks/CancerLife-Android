package com.platforms.objects;

import android.graphics.drawable.Drawable;

/**
 * Created by AGalkin on 11/6/13.
 */
public class Supporter {
    public String name;
    public Drawable photo;
    public int supporterId;
    public String userCircle;
    public String email;
    public String status;

    public Supporter(String name, Drawable photo, int supporterId, String userCircle, String email, String status){
        super();
        this.name =  name;
        this.photo = photo;
        this.supporterId = supporterId;
        this.userCircle = userCircle;
        this.email = email;
        this.status = status;
    }
}
