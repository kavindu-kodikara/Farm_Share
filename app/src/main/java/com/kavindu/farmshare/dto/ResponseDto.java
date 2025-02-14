package com.kavindu.farmshare.dto;

import java.io.Serializable;

public class ResponseDto<T> implements Serializable {
    private boolean success;
    private String message;
    private T data;

    public ResponseDto() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
