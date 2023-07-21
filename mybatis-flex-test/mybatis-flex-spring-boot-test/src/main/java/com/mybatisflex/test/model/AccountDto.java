package com.mybatisflex.test.model;

import java.util.List;
import java.util.Map;

public class AccountDto {

    private Long id;

    private String name;

    private List<Map<String,String>> other;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, String>> getOther() {
        return other;
    }

    public void setOther(List<Map<String, String>> other) {
        this.other = other;
    }
}
