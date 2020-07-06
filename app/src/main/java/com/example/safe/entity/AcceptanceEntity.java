package com.example.safe.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Objects;


public class AcceptanceEntity implements Serializable {

    private Integer id;

    private String acceptOption;

    private Integer uid;

    private LocalDateTime createDate;

    private String acceptStatus;

    private Integer rid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAcceptOption() {
        return acceptOption;
    }

    public void setAcceptOption(String acceptOption) {
        this.acceptOption = acceptOption;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(String acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcceptanceEntity that = (AcceptanceEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(acceptOption, that.acceptOption) &&
                Objects.equals(uid, that.uid) &&
                Objects.equals(createDate, that.createDate) &&
                Objects.equals(acceptStatus, that.acceptStatus) &&
                Objects.equals(rid, that.rid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, acceptOption, uid, createDate, acceptStatus, rid);
    }
}
