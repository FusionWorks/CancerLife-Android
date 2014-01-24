package com.platforms.objects;

/**
 * Created by AGalkin on 12/12/13.
 */
public class ReportItem {
    public String name;
    public float avgScore;
    public float percentage;

    public float current;
    public float previous;
    public String change;
    public ReportItem(String name, float avgScore, float percentage, float current, float previous, String change){
        super();
        this.name =  name;
        this.avgScore = avgScore;
        this.percentage = percentage;

        this.current = current;
        this.previous = previous;
        this.change = change;
    }
}
