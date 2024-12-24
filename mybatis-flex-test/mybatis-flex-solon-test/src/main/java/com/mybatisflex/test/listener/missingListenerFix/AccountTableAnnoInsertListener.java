package com.mybatisflex.test.listener.missingListenerFix;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.test.model.AccountMissingListenerTestModel;

public class AccountTableAnnoInsertListener implements InsertListener {

    @Override
    public void onInsert(Object entity) {
        AccountMissingListenerTestModel model = (AccountMissingListenerTestModel) entity;
        model.setUserName("测试缺失的监听器-userName");
    }
}
