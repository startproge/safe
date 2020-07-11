package com.example.safe.vo;

import java.time.LocalDateTime;
import java.util.Date;

public class DangerVo {
    private Integer id;
    private String riskSource;
    private String status;
    private String type;
    private Integer timeLimit;
    private Date createDate;

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

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "DangerVo{" +
                "id=" + id +
                ", riskSource='" + riskSource + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", timeLimit=" + timeLimit +
                ", createDate=" + createDate +
                '}';
    }
}
