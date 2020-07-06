package com.example.safe.form;

import java.util.List;

public class DangerForm {
    private String type;
    private String riskSource;
    private String level;
    private String description;
    private String measure;
    private int timeLimit;
    private int uid;
    private List<String> position;

    public DangerForm(String type, String riskSource, String level, String description, String measure, int timeLimit, int uid, List<String> position) {
        this.type = type;
        this.riskSource = riskSource;
        this.level = level;
        this.description = description;
        this.measure = measure;
        this.timeLimit = timeLimit;
        this.uid = uid;
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRiskSource() {
        return riskSource;
    }

    public void setRiskSource(String riskSource) {
        this.riskSource = riskSource;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public List<String> getPosition() {
        return position;
    }

    public void setPosition(List<String> position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "DangerForm{" +
                "type='" + type + '\'' +
                ", riskSource='" + riskSource + '\'' +
                ", level='" + level + '\'' +
                ", description='" + description + '\'' +
                ", measure='" + measure + '\'' +
                ", timeLimit=" + timeLimit +
                ", uid=" + uid +
                ", position=" + position +
                '}';
    }
}
