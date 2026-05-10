package com.example.airquality.common;

public class AqiLevelResult {
    private int level;
    private String levelName;
    private String color;
    private String healthImplication;

    public AqiLevelResult() {}

    public AqiLevelResult(int level, String levelName, String color, String healthImplication) {
        this.level = level;
        this.levelName = levelName;
        this.color = color;
        this.healthImplication = healthImplication;
    }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getHealthImplication() { return healthImplication; }
    public void setHealthImplication(String healthImplication) { this.healthImplication = healthImplication; }
}
