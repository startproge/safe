package com.example.safe.form;

import java.util.List;

public class RectificationForm {
    String status;
    String measure;
    Integer uid;
    Integer did;
    List<String> position;

    public RectificationForm(String status, String measure, Integer uid, Integer did, List<String> position) {
        this.status = status;
        this.measure = measure;
        this.uid = uid;
        this.did = did;
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getDid() {
        return did;
    }

    public void setDid(Integer did) {
        this.did = did;
    }

    public List<String> getPosition() {
        return position;
    }

    public void setPosition(List<String> position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "RectificationForm{" +
                "status='" + status + '\'' +
                ", measure='" + measure + '\'' +
                ", uid=" + uid +
                ", did=" + did +
                ", position=" + position +
                '}';
    }
}
