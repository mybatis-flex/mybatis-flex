package com.mybatisflex.test.listener.missingListenerFix;

import com.mybatisflex.annotation.Column;

public class BaseLogicDelete implements LogicDeleteInsertListenerFlag {

    @Column(isLogicDelete = true)
    private Boolean isDelete;

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

}
