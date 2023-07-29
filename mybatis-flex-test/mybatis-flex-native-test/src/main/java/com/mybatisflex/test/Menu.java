package com.mybatisflex.test;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;

@Table(value = "tb_menu")
public class Menu extends BaseEntity implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long parentId;

    private String name;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Menu{" +
            "id=" + id +
            ", parentId=" + parentId +
            ", name='" + name + '\'' +
            "} " + super.toString();
    }
}
