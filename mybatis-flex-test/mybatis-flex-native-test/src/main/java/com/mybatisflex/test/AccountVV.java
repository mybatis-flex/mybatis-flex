package com.mybatisflex.test;

import com.mybatisflex.annotation.*;

import java.io.Serializable;

@Table(value = "tb_account_vv", dataSource = "ds2", onSet = AccountOnSetListener.class)
public class AccountVV extends Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    private int age;
    private int ageVV;
}