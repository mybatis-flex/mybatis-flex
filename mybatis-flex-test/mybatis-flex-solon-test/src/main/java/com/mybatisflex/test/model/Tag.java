package com.mybatisflex.test.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;

/**
 * 标签
 *
 * @author Ice 2023/09/26
 * @version 1.0
 */
@Table(value = "tb_tag")
public class Tag implements Serializable {
    private static final long serialVersionUID = 5600670055904157386L;

    /**
     * ID
     */
    @Id
    private Integer tagId;

    /**
     * 标签名称
     */
    private String name;

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
