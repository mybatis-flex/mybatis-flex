package com.mybatisflex.test.model;


/**
 * 性别枚举
 * @author luozhan
 */

public enum Sex implements BaseEnum {
    //
    MALE(1),
    FEMALE(2),
    UNKNOWN(0);

    private final int value;

    Sex(Integer value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
