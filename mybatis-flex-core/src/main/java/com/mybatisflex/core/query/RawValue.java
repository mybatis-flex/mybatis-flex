package com.mybatisflex.core.query;

import java.io.Serializable;

public class RawValue implements Serializable {

    private String context;

    public RawValue() {
    }

    public RawValue(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return context;
    }
}
