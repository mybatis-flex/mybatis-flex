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

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.RelationManyToMany;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.util.List;

@Table(value = "tb_role")
public class Role implements Serializable {

    @Id
    private Long id;
    private String name;


    @RelationManyToMany(
        joinTable = "tb_role_mapping",
        selfField = "id", joinSelfColumn = "role_id",
        targetField = "id", joinTargetColumn = "account_id"
    )
    private List<RelationAccount> accounts;

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

    public List<RelationAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<RelationAccount> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "Role{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", accounts=" + accounts +
            '}';
    }

}
