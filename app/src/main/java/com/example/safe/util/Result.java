package com.example.safe.util;

import java.util.Map;


public class Result {
    private Integer status;
    private String msg;
    private Object data;

    private Result(Integer status, String msg, Object data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private Result(Object data){
        this.status = 200;
        this.msg = "success";
        this.data = data;
    }

    public static Result build(Integer status, String msg, Object data){
        return new Result(status, msg, data);
    }

    public static Result errorMsg(String msg){
        return new Result(500, msg, null);
    }

    public static Result success(Object data){
        return new Result(data);
    }

    public static Result errorMap(Map<String, String> errorMap){
        return new Result(502, "参数格式错误", errorMap);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
