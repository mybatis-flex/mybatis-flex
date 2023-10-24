package com.mybatisflex.test.listener.missionListenerFix;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.test.model.AccountMissingListenerTestModel;

public class AccountAgeInsertListener implements InsertListener {

    @Override
    public void onInsert(Object entity) {
        AccountMissingListenerTestModel model = (AccountMissingListenerTestModel) entity;
        model.setAge(18);
    }
}
