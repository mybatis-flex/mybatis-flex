package com.mybatisflex.test.model;

import java.util.Date;
import java.util.List;

/**
 * 复杂查询接收类
 * <a href="https://gitee.com/mybatis-flex/mybatis-flex/issues/IAU28L">演示问题</a>。
 *
 * @author 王帅
 * @since 2024-10-03
 */
@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public class AccountView {

    private Long id;
    private String userName;
    private Date birthday;

    private List<Account> accountList;

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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

}
