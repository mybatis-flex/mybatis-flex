package com.mybatisflex.test;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.activerecord.Model;
import com.mybatisflex.core.keygen.KeyGenerators;

import java.math.BigDecimal;

@Table("demo")
public class Demo extends Model<Demo> {
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private String id;

    public static Demo create() {
        return new Demo();
    }

    private BigDecimal firstField;
    private String userId;
    private BigDecimal secondField;
    private String foreignId;

    public String getId() {
        return id;
    }

    public Demo setId(String id) {
        this.id = id;
        return this;
    }

    public BigDecimal getFirstField() {
        return firstField;
    }

    public Demo setFirstField(BigDecimal firstField) {
        this.firstField = firstField;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Demo setUserId(String userId) {
        this.userId = userId;
        return this;

    }

    public BigDecimal getSecondField() {
        return secondField;
    }

    public Demo setSecondField(BigDecimal secondField) {
        this.secondField = secondField;
        return this;
    }

    public String getForeignId() {
        return foreignId;
    }

    public Demo setForeignId(String foreignId) {
        this.foreignId = foreignId;
        return this;
    }
}

