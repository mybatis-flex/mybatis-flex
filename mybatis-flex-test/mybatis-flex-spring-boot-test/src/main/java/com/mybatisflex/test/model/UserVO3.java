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

import com.mybatisflex.annotation.Id;

import java.util.List;

/**
 * @author 王帅
 * @since 2023-06-07
 */

public class UserVO3 {

    @Id
    private String userId;
    private String userName;
    private List<RoleVO3> roleVO3;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<RoleVO3> getRoleVO3() {
        return roleVO3;
    }

    public void setRoleVO3(List<RoleVO3> roleVO3) {
        this.roleVO3 = roleVO3;
    }

    @Override
    public String toString() {
        return "UserVO3{" +
            "userId='" + userId + '\'' +
            ", userName='" + userName + '\'' +
            ", roleVO3=" + roleVO3 +
            '}';
    }

}
