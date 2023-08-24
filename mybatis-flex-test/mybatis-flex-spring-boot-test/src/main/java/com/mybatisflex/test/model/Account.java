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
package com.mybatisflex.test.model;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;

import java.util.Date;

/**
 * 账户信息。
 */
@Table(value = "tb_account", onSet = AccountOnSetListener.class)
public class Account extends BaseEntity<String, Long, String> {

    /*@Id(keyType = KeyType.Auto)
    private Long id;*/
    //private String userName;
    /**
     * 年龄。
     */
    private Integer age;

    /**
     * 生日。
     */
    private Date birthday;

    /**
     * 逻辑删除。
     */
    @Column(isLogicDelete = true)
    private Boolean isDelete;

    @Column(ignore = true)
    private String anotherColumn;

//    private Gender gender;
//
//    public Gender getGender() {
//        return gender;
//    }
//
//    public void setGender(Gender gender) {
//        this.gender = gender;
//    }

    /*public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }*/

    /*public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }*/

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public String getAnotherColumn() {
        return anotherColumn;
    }

    public void setAnotherColumn(String anotherColumn) {
        this.anotherColumn = anotherColumn;
    }

    @Override
    public String toString() {
        return "Account{" +
            "id=" + id +
            ", userName='" + userName + '\'' +
            ", age=" + age +
            ", birthday=" + birthday +
            ", isDelete=" + isDelete +
            ", anotherColumn=" + anotherColumn +
//                ", roles=" + roles +
            '}';
    }

}
