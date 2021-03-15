package com.fota.android.socket;

import java.io.Serializable;

public class SocketAdditionEntity<T> implements Serializable {
    public SocketAdditionEntity(int handleType, int code, String message) {
        this.handleType = handleType;
        this.code = code;
        this.message = message;
    }

    //1查询+订阅  2 订阅 3取消 4 查询
    int handleType;
    T param;

    public int getHandleType() {
        return handleType;
    }

    public void setHandleType(int handleType) {
        this.handleType = handleType;
    }

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message = "";

}
