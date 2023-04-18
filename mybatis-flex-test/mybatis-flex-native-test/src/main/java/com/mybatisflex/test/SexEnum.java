package com.mybatisflex.test;

import com.mybatisflex.annotation.EnumValue;

public enum SexEnum {
    TYPE1(0,"女"),
    TYPE2(1,"男"),
    TYPE3(2,"未知"),

    ;

    @EnumValue
    private int code;

    private String desc;

    SexEnum(int code, String desc) {
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
