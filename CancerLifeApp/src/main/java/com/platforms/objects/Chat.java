package com.platforms.objects;

import android.graphics.drawable.Drawable;

/**
 * Created by AGalkin on 12/18/13.
 */
public class Chat {
    public String jid;
    public String name;
    public Drawable picture;
    public String text;
    public boolean self;

    public Chat(String jid, String name, Drawable picture, String text, boolean self){
        super();
        this.jid = jid;
        this.picture = picture;
        this.text = text;
        this.name = name;
        this.self = self;
    }
}
