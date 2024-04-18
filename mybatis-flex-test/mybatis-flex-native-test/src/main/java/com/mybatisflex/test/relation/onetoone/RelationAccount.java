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
import java.util.Map;

@Table(value = "tb_relation_account")
public class RelationAccount implements Serializable {

    @Id
    private Long id;

    private String userName;

    private int age;

    //    @RelationOneToOne(selfField = "id", targetField = "accountId")
//    @RelationOneToOne(targetField = "accountId")
//    @RelationManyToOne(joinTable = "tb_idcard_mapping",joinSelfColumn = "account_id",joinTargetColumn = "idcard_id"
//    ,selfField = "id",targetField = "accountId")
    private IDCard idCard;

    //    @RelationOneToMany(selfField = "id", targetField = "accountId")
//    @RelationOneToMany(targetField = "accountId")
    private List<Book> books;

    //    @RelationManyToMany(
//            joinTable = "tb_role_mapping",
//            selfField = "id", joinSelfColumn = "account_id",
//            targetField = "id", joinTargetColumn = "role_id"
//    )
//    @RelationManyToMany(
//        joinTable = "tb_role_mapping",
//        joinSelfColumn = "account_id",
//        joinTargetColumn = "role_id",
//        extraCondition = "(name like '%2%' or id > 1)"
//    )
    private List<Role> roles;


    @RelationManyToMany(
        joinTable = "tb_role_mapping",
        joinSelfColumn = "account_id",
        joinTargetColumn = "role_id",
        extraCondition = "(name like '%2%' or id > 1)",
        mapKeyField = "id"
    )
    private Map<Long, Role> rolesMap;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public IDCard getIdCard() {
        return idCard;
    }

    public void setIdCard(IDCard idCard) {
        this.idCard = idCard;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Map<Long, Role> getRolesMap() {
        return rolesMap;
    }

    public void setRolesMap(Map<Long, Role> rolesMap) {
        this.rolesMap = rolesMap;
    }

    @Override
    public String toString() {
        return "Account{" +
            "id=" + id +
            ", userName='" + userName + '\'' +
            ", age=" + age +
            ", idCard=" + idCard +
            ", books=" + books +
            ", roles=" + roles +
            ", rolesMap=" + rolesMap +
            '}';
    }
}
