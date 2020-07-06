package com.example.safe.form;

public class dangerForm {
    private int dangerId;
    private String riskResource;
    private String dangerType;
    private String dangerStatus;
    private int dangerTimeLimit;

    public dangerForm(int dangerId, String riskResource, String dangerType, String dangerStatus, int dangerTimeLimit) {
        this.dangerId = dangerId;
        this.riskResource = riskResource;
        this.dangerType = dangerType;
        this.dangerStatus = dangerStatus;
        this.dangerTimeLimit = dangerTimeLimit;
    }

    public String getRiskResource() {
        return riskResource;
    }

    public void setRiskResource(String riskResource) {
        this.riskResource = riskResource;
    }

    public String getDangerType() {
        return dangerType;
    }

    public void setDangerType(String dangerType) {
        this.dangerType = dangerType;
    }

    public String getDangerStatus() {
        return dangerStatus;
    }

    public void setDangerStatus(String dangerStatus) {
        this.dangerStatus = dangerStatus;
    }

    public int getDangerTimeLimit() {
        return dangerTimeLimit;
    }

    public void setDangerTimeLimit(int dangerTimeLimit) {
        this.dangerTimeLimit = dangerTimeLimit;
    }

    public int getDangerId() {
        return dangerId;
    }

    public void setDangerId(int dangerId) {
        this.dangerId = dangerId;
    }

}
