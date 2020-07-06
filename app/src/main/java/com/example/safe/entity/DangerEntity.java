package com.example.safe.entity;

import java.io.Serializable;
import java.util.Date;

public class DangerEntity implements Serializable {
    private Integer id;

    private Integer type;


    private String riskSource;

    private Integer level;

    private String description;

    private String measure;

    private Integer timeLimit;

    private Integer uid;

    private Date createDate;

    private Integer pid1;

    private Integer pid2;

    private Integer pid3;

    private Integer dangerStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRiskSource() {
        return riskSource;
    }

    public void setRiskSource(String riskSource) {
        this.riskSource = riskSource;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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

    @Override
    public String toString() {
        return "DangerEntity{" +
                "id=" + id +
                ", type=" + type +
                ", riskSource='" + riskSource + '\'' +
                ", level=" + level +
                ", description='" + description + '\'' +
                ", measure='" + measure + '\'' +
                ", timeLimit=" + timeLimit +
                ", uid=" + uid +
                ", createDate=" + createDate +
                ", pid1=" + pid1 +
                ", pid2=" + pid2 +
                ", pid3=" + pid3 +
                ", dangerStatus=" + dangerStatus +
                '}';
    }

    public Integer getDangerStatus() {
        return dangerStatus;
    }

    public void setDangerStatus(Integer dangerStatus) {
        this.dangerStatus = dangerStatus;
    }


}
