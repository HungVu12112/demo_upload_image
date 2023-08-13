package com.example.labretrofit.model;

public class ResComic {
    private String msg;
    private Boolean isCheack;

    public ResComic(String msg, Boolean isCheack) {
        this.msg = msg;
        this.isCheack = isCheack;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getCheack() {
        return isCheack;
    }

    public void setCheack(Boolean cheack) {
        isCheack = cheack;
    }
}
