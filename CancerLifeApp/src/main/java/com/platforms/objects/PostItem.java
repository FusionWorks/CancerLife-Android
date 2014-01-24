package com.platforms.objects;

import java.util.ArrayList;

/**
 * Created by AGalkin on 11/9/13.
 */
public class PostItem {
    public String name;
    public int order;
    public Level level1;
    public Level level2;
    public String question1Type;
    public String question1Text;
    public String question2Type;
    public String question2Text;
    public ArrayList<String> question2options;
    public PostItem(String name,
                    int order,
                    Level level1,
                    Level level2,
                    String question1Type,
                    String question1Text,
                    String question2Type,
                    String question2Text,
                    ArrayList<String> question2options){
        super();
        this.name = name;
        this.order = order;
        this.level1 = level1;
        this.level2 = level2;
        this.question1Type = question1Type;
        this.question1Text = question1Text;
        this.question2Type = question2Type;
        this.question2Text = question2Text;
        this.question2options = question2options;
    }
}
