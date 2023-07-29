package com.mybatisflex.test;

import com.mybatisflex.annotation.Table;

import java.io.Serializable;

@Table(value = "tb_book")
public class Book extends BaseEntity implements Serializable{

    private Long id;

    private Long accountId;

    private String title;

    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        return "Book{" +
            "id=" + id +
            ", accountId=" + accountId +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            "} " + super.toString();
    }
}
