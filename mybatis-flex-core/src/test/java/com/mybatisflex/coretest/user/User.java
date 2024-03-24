package com.mybatisflex.coretest.user;

import com.mybatisflex.annotation.*;

import java.sql.Timestamp;

@Table(value = "t_user", onUpdate = NoneListener.class)
public class User {
    @Id
    private Long id;

    private String name;

    @Column(nullStrategy = NullStrategy.KEEP_INSERT)
    private Timestamp createdDate;

    @Column(nullStrategy = NullStrategy.KEEP_UPDATE)
    private Timestamp modifiedDate;

    @Column(nullStrategy = NullStrategy.KEEP_INSERT_UPDATE)
    private String neverIgnore;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }
}
