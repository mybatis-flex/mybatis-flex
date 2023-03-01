package com.mybatisflex.test;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.enums.KeyType;

import java.util.Date;

@Table("tb_article")
public class Article {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long accountId;

    private String title;

    @Column(isLarge = true)
    private String content;

    @Column(insert = "now()")
    private Date created;

    @Column(update = "now()")
    private Date modified;

}
