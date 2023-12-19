package com.mybatisflex.coretest.auth;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.NoneListener;
import com.mybatisflex.annotation.Table;

/**
 * 权限测试类
 *
 * @author zhang
 * @since 2023-12-17
 */
@Table(value = "tb_project", onUpdate = NoneListener.class)
public class Project {

    @Id
    private Long id;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 创建人
     */
    private String insertUserId;

    @Column(isLogicDelete = true)
    private Boolean isDelete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInsertUserId() {
        return insertUserId;
    }

    public void setInsertUserId(String insertUserId) {
        this.insertUserId = insertUserId;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }
}
