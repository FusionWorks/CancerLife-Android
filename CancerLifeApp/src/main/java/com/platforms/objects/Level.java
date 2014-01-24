package com.platforms.objects;
;import java.util.ArrayList;

/**
 * Created by AGalkin on 11/9/13.
 */
public class Level {
    public String question;
    public int min;
    public int max;
    public int step;
    public int defaultNum;
    public ArrayList<String> options;

    public Level(String question, int min, int max, int step, int defaultNum, ArrayList<String> options ){
        super();
        this.question = question;
        this.min = min;
        this.max = max;
        this.step = step;
        this.defaultNum = defaultNum;
        this.options = options;
    }
}
