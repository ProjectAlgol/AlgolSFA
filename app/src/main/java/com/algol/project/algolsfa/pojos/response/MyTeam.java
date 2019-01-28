
package com.algol.project.algolsfa.pojos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyTeam {

    @SerializedName("UserType")
    @Expose
    private String userType;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("LatLon")
    @Expose
    private String latLon;
    @SerializedName("Mobile")
    @Expose
    private String mobile;
    @SerializedName("DateTime")
    @Expose
    private String dateTime;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatLon() {
        return latLon;
    }

    public void setLatLon(String latLon) {
        this.latLon = latLon;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}
