package com.gordeev.dbserver.entity;

import java.util.ArrayList;
import java.util.List;

public class Result {
    private ResultType resultType;
    private String message;
    private List<String> columns = new ArrayList<>();
    private List<List<Object>> rows = new ArrayList<>();

    public Result(ResultType resultType, String message) {
        this.resultType = resultType;
        this.message = message;
    }

    public Result() {
        this(ResultType.ERROR, "Something happened on server");
    }

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<Object>> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultType=" + resultType +
                ", message='" + message + '\'' +
                ", columns=" + columns +
                ", rows=" + rows +
                '}';
    }
}
