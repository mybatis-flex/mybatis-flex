package com.mybatisflex.test.listener.missionListenerFix;

import com.mybatisflex.annotation.InsertListener;

public class LogicDeleteInsertListener implements InsertListener {

    @Override
    public void onInsert(Object entity) {
        BaseLogicDelete logicDeleteEntity = (BaseLogicDelete) entity;
        logicDeleteEntity.setDelete(true);
    }
}
