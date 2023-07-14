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

import com.mybatisflex.annotation.*;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import com.mybatisflex.core.mask.Masks;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Table(value = "entity04")
public class Entity04 extends BaseAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "uuid")
    private String id;

    @ColumnMask(Masks.CHINESE_NAME)
    private String userName;

    private int age;

    @NotBlank
    private Date birthday;

    @Column(typeHandler = Fastjson2TypeHandler.class, isLarge = true)
    private Map<String, Object> options;

    @Column(isLogicDelete = true)
    private Boolean isDelete;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public void addOption(String key, Object value) {
        if (options == null) {
            options = new HashMap<>();
        }
        options.put(key, value);
    }


    @Override
    public String toString() {
        return "Account{" +
            "id=" + id +
            ", userName='" + userName + '\'' +
            ", age=" + age +
            ", birthday=" + birthday +
            ", options=" + options +
            ", isDelete=" + isDelete +
            '}';
    }

}
