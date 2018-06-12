package com.gordeev.dbserver.entity;

public class Result {
    private ResultType resultType;
    private String message;
    //DOM instead of columnNames, columnValues, conditions ???

    public Result(ResultType resultType, String message) {
        this.resultType = resultType;
        this.message = message;
    }

    public Result() {
        this(ResultType.ERROR, "Who knows what happened there");
    }

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
