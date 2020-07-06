package com.example.safe.entity;


import java.io.Serializable;

public class EnterpriseEntity implements Serializable {

//    private static final long serialVersionUID=1L;

    private Integer id;

    private String name;

    private String creditCode;

    private String representative;

    private String telephone;

    private String address;

    private String position;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Enterprise{" +
        "id=" + id +
        ", name=" + name +
        ", creditCode=" + creditCode +
        ", representative=" + representative +
        ", telephone=" + telephone +
        ", address=" + address +
        ", position=" + position +
        "}";
    }
}
