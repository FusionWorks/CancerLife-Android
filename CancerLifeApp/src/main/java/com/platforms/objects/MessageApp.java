package com.platforms.objects;

/**
 * Created by AGalkin on 12/18/13.
 */
public class MessageApp {
    public String text;
    public String time;
    public boolean self;

    public MessageApp(String text, String time, boolean self){
        super();
        this.text = text;
        this.time = time;
        this.self = self;
    }
}
