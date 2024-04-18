/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mybatisflex.test.relation.onetoone;

import com.mybatisflex.annotation.RelationManyToOne;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;

@Table(value = "tb_book")
public class Book implements Serializable {

    private Long id;

    private Long accountId;

    private String title;

    private String content;

    @RelationManyToOne(selfField = "accountId", targetField = "id")
    private RelationAccount account;

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

    public RelationAccount getAccount() {
        return account;
    }

    public void setAccount(RelationAccount account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Book{" +
            "id=" + id +
            ", accountId=" + accountId +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            ", account=" + account +
            '}';
    }

}
