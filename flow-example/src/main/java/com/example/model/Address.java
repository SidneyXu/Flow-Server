package com.example.model;

public class Address {

    private String tip;
    private String detail;

    public Address(String tip, String detail) {
        this.tip = tip;
        this.detail = detail;
    }

    public Address() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Address{");
        sb.append("tip='").append(tip).append('\'');
        sb.append(", detail='").append(detail).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
