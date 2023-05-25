package com.mybatisflex.test;

import java.util.Date;

public class ArticleDTO {

    private Long id;

    private Long accountId;

    private String title;

    private String content;

    private String authorName;

    private int authorAge;

    private Date birthday;

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


    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getAuthorAge() {
        return authorAge;
    }

    public void setAuthorAge(int authorAge) {
        this.authorAge = authorAge;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "ArticleDTO{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorAge=" + authorAge +
                ", birthday=" + birthday +
                '}';
    }
}
