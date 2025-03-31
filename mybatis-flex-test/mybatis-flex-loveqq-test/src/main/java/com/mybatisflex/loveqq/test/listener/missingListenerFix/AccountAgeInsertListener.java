package com.mybatisflex.loveqq.test.listener.missingListenerFix;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.loveqq.test.model.AccountMissingListenerTestModel;

public class AccountAgeInsertListener implements InsertListener {

    @Override
    public void onInsert(Object entity) {
        AccountMissingListenerTestModel model = (AccountMissingListenerTestModel) entity;
        model.setAge(18);
    }
}
