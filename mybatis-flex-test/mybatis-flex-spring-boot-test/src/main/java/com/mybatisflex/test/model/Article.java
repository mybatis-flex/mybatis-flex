package com.mybatisflex.test.model;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;

@Table(value = "tb_article")
public class Article extends IdEntity<Long> {

    private static final long serialVersionUID = 1L;


    private Long accountId;

    private String title;

    private String content;

    @Column(isLogicDelete = true)
    private Boolean isDelete;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
