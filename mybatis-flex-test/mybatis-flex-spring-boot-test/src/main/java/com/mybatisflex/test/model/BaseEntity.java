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

import java.util.List;

/**
 * @author 王帅
 * @since 2.0
 */
public class BaseEntity<T, ID, L> extends IdEntity<ID> {

    /**
     * 用户名。
     */
    protected T userName;

    /**
     * 用户角色。
     */
    @Column(ignore = true)
    protected List<L> roles;

    public List<L> getRoles() {
        return roles;
    }

    public void setRoles(List<L> roles) {
        this.roles = roles;
    }


    public T getUserName() {
        return userName;
    }

    public void setUserName(T userName) {
        this.userName = userName;
    }

}
