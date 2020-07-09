package com.example.safe.form;

public class AcceptanceForm {
    String acceptOption;
    Integer uid;
    String acceptStatus;
    int rid;

    public AcceptanceForm(String acceptOption, Integer uid, String acceptStatus, int rid) {
        this.acceptOption = acceptOption;
        this.uid = uid;
        this.acceptStatus = acceptStatus;
        this.rid = rid;
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

    public String getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(String acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    @Override
    public String toString() {
        return "AcceptanceForm{" +
                "acceptOption='" + acceptOption + '\'' +
                ", uid=" + uid +
                ", acceptStatus='" + acceptStatus + '\'' +
                ", rid=" + rid +
                '}';
    }


}
