package com.mybatisflex.test;

import com.mybatisflex.annotation.EnumValue;

public enum TypeEnum {
    TYPE1(1,"类型1"),
    TYPE2(2,"类型2"),
    TYPE3(3,"类型3"),

    ;

    @EnumValue
    private int code;

    private String desc;

    TypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
