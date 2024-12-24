package com.mybatisflex.test.model;

import com.mybatisflex.annotation.*;
import com.mybatisflex.test.listener.missingListenerFix.AccountAgeInsertListenerFlag;
import com.mybatisflex.test.listener.missingListenerFix.AccountTableAnnoInsertListener;
import com.mybatisflex.test.listener.missingListenerFix.BaseLogicDelete;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountMissingListenerTestModel that = (AccountMissingListenerTestModel) o;
        return Objects.equals(id, that.id) && Objects.equals(userName, that.userName) && Objects.equals(age, that.age) && Objects.equals(getDelete(), that.getDelete());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, age, getDelete());
    }

    @Override
    public String toString() {
        return "AccountMissingListenerTestModel{" +
            "id=" + id +
            ", userName='" + userName + '\'' +
            ", age=" + age +
            ", isDelete=" + getDelete() +
            '}';
    }
}












