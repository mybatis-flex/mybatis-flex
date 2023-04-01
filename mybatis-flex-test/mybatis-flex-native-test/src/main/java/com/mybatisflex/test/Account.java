package com.mybatisflex.test;

import com.mybatisflex.annotation.*;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import com.mybatisflex.core.mask.Masks;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Table(value = "tb_account",dataSource = "ds2")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @ColumnMask(Masks.CHINESE_NAME)
    private String userName;

    private int age;

    private Date birthday;

    @Column(typeHandler = Fastjson2TypeHandler.class)
    private Map<String, Object> options;

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public void addOption(String key, Object value) {
        if (options == null) {
            options = new HashMap<>();
        }
        options.put(key, value);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", age=" + age +
                ", birthday=" + birthday +
                ", options=" + options +
                '}';
    }
}
