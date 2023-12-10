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

package com.mybatisflex.test;

import com.mybatisflex.annotation.ColumnMask;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.mask.Masks;

import java.io.Serializable;

@Table(value = "tb_account6", dataSource = "ds2", onSet = AccountOnSetListener.class)
public class Account6 extends BaseEntity implements Serializable, AgeAware {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.None)
    private Long id;

    @ColumnMask(Masks.CHINESE_NAME)
    private String userName;

    private int age;


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

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }



    @Override
    public String toString() {
        return "Account{" +
            "id=" + id +
            ", userName='" + userName + '\'' +
            ", age=" + age +
            '}';
    }

}
