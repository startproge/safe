package com.example.safe.vo;

public class DangerVo {
    private Integer id;
    private String riskSource;
    private String status;
    private String type;
    private Integer timeLevel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRiskSource() {
        return riskSource;
    }

    public void setRiskSource(String riskSource) {
        this.riskSource = riskSource;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTimeLevel() {
        return timeLevel;
    }

    public void setTimeLevel(int timeLevel) {
        this.timeLevel = timeLevel;
    }
}