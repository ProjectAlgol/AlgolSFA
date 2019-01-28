
package com.algol.project.algolsfa.pojos.response;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TestModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("MyTeam")
    @Expose
    private List<MyTeam> myTeam = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<MyTeam> getMyTeam() {
        return myTeam;
    }

    public void setMyTeam(List<MyTeam> myTeam) {
        this.myTeam = myTeam;
    }

}
