package com.mshd.mshd.model;

public class RegionCode {
    private String province;      // 省
    private String city;         // 市
    private String county;       // 县
    private String town;         // 乡镇
    private String village;      // 村
    private String fullCode;     // 完整12位编码

    public RegionCode(String province, String city, String county, String town, String village, String fullCode) {
        this.province = province;
        this.city = city;
        this.county = county;
        this.town = town;
        this.village = village;
        this.fullCode = fullCode;
    }

    // Getters and setters
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getFullCode() {
        return fullCode;
    }

    public void setFullCode(String fullCode) {
        this.fullCode = fullCode;
    }
} 