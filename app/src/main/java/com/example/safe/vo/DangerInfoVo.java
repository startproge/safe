package com.example.safe.vo;

import java.util.Date;

public class DangerInfoVo {
    Integer id;
    String type;
    String riskSource;
    String level;
    String description;
    String measure;
    Integer timeLimit;
    Integer uid;
    Date createDate;
    Integer pid1;
    Integer pid2;
    Integer pid3;
    String dangerStatus;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getPid1() {
        return pid1;
    }

    public void setPid1(Integer pid1) {
        this.pid1 = pid1;
    }

    public Integer getPid2() {
        return pid2;
    }

    public void setPid2(Integer pid2) {
        this.pid2 = pid2;
    }

    public Integer getPid3() {
        return pid3;
    }

    public void setPid3(Integer pid3) {
        this.pid3 = pid3;
    }

    public String getDangerStatus() {
        return dangerStatus;
    }

    public void setDangerStatus(String dangerStatus) {
        this.dangerStatus = dangerStatus;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "DangerInfoVo{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", riskSource='" + riskSource + '\'' +
                ", level='" + level + '\'' +
                ", description='" + description + '\'' +
                ", measure='" + measure + '\'' +
                ", timeLimit=" + timeLimit +
                ", uid=" + uid +
                ", createDate=" + createDate +
                ", pid1=" + pid1 +
                ", pid2=" + pid2 +
                ", pid3=" + pid3 +
                ", dangerStatus='" + dangerStatus + '\'' +
                '}';
    }
}
