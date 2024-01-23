package com.gallery.gallery.dao;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private final Object data;
    private final Boolean success;
    private final String authToken;

    public ApiResponse(Object data, Boolean success,String token) {
        this.data = data;
        this.success = success;
        this.authToken = token;
    }

    public Object getData() {
        return data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getAuthToken() {
        return authToken;
    }
}
