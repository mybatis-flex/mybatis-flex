package com.mybatisflex.core.query;

import java.io.Serializable;

public class RawValue implements Serializable {

    private String content;

    public RawValue() {
    }

    public RawValue(String context) {
        this.content = context;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
