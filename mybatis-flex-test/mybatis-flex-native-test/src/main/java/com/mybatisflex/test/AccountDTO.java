package com.mybatisflex.test;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.ColumnMask;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import com.mybatisflex.core.mask.Masks;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountDTO  {


    private Long id;

    @ColumnMask(Masks.CHINESE_NAME)
    private String userName;

    private int age;

    @NotBlank
    private Date birthday;

    @Column(typeHandler = Fastjson2TypeHandler.class, isLarge = true)
    private Map<String, Object> options;

    @Column(isLogicDelete = true)
    private Boolean isDelete;

    private List<Article> articles;


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

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "AccountDTO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", age=" + age +
                ", birthday=" + birthday +
                ", options=" + options +
                ", isDelete=" + isDelete +
                ", articles=" + articles +
                '}';
    }
}
