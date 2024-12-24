package com.mybatisflex.test.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;

@Table(value = "tb_class")
public class TbClass {
    @Id
    private Long id;

    private Long user_id;

    private String className;

    public Long getId() {
        return id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public String getClassName() {
        return className;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "TbClass{" +
            "id=" + id +
            ", user_id=" + user_id +
            ", className='" + className + '\'' +
            '}';
    }
}
