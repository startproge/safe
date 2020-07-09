package com.example.safe.entity;

import java.io.Serializable;
import java.util.Date;

public class RectificationEntity implements Serializable {

    private static final long serialVersionUID=1L;


    private Integer id;

    private String status;

    private String measure;

    private String document;

    private Integer uid;

    private Date createDate;

    private Integer pid1;

    private Integer pid2;

    private Integer pid3;

    private Integer did;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
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


    public Integer getDid() {
        return did;
    }

    public void setDid(Integer did) {
        this.did = did;
    }


    @Override
    public String toString() {
        return "RectificationEntity{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", measure='" + measure + '\'' +
                ", document='" + document + '\'' +
                ", uid=" + uid +
                ", createDate=" + createDate +
                ", pid1=" + pid1 +
                ", pid2=" + pid2 +
                ", pid3=" + pid3 +
                ", did=" + did +
                '}';
    }
}
