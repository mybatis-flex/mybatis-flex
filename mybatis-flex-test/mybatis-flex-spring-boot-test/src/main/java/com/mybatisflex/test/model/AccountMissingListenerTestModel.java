package com.mybatisflex.test.model;

import com.mybatisflex.annotation.*;
import com.mybatisflex.test.listener.missionListenerFix.*;

/**
 * 缺失的监听器测试
 *
 * @author Ice 2023/10/23
 * @version 1.0
 */
@Table(value = "tb_account", onInsert = AccountTableAnnoInsertListener.class)
public class AccountMissingListenerTestModel extends BaseLogicDelete implements AccountAgeInsertListenerFlag {

    /**
     * 主键。
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    private Integer age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

}












