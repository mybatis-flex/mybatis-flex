package com.mybatisflex.test.issue.vo;

public class UserVO {
    /**
     * id
     */
    private Integer id;

    /**
     * name
     */
    private String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserVO{" +
            "id=" + id +
            ", name=" + name +
            '}';
    }
}
