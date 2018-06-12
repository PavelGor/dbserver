package com.gordeev.dbserver.entity;

public enum ResultType {
    RESULTSET("result set"), UPDATED("updated"), ERROR("error");

    private final String type;

    ResultType(String type) {
        this.type = type;
    }

    public static ResultType getByName(String type) {
        for(ResultType resultType : values()){
            if (resultType.type.equalsIgnoreCase(type)){
                return resultType;
            }
        }
        throw new IllegalArgumentException("No result type with type " + type + " found");
    }
}
