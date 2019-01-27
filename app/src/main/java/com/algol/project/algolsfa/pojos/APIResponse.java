package com.algol.project.algolsfa.pojos;

/**
 * Created by Lykos on 27-Jan-19.
 */

public class APIResponse {
    private String api;
    private int httpStatus;
    private Object response;

    public APIResponse(String api, int httpStatus, Object response) {
        this.httpStatus = httpStatus;
        this.response = response;
        this.api= api;
    }

    public String getApi() {
        return api;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public Object getResponse() {
        return response;
    }
}
