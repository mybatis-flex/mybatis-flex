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

import java.util.Objects;

/**
 * @author 王帅
 * @since 2.0
 */

public class UserVO3 {

    private String userId;
    private String userName;
    private Integer roleId;
    private String roleKey;
    private String roleName;

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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserVO3 userVO3 = (UserVO3) o;

        if (!Objects.equals(userId, userVO3.userId)) return false;
        if (!Objects.equals(userName, userVO3.userName)) return false;
        if (!Objects.equals(roleId, userVO3.roleId)) return false;
        if (!Objects.equals(roleKey, userVO3.roleKey)) return false;
        return Objects.equals(roleName, userVO3.roleName);
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (roleId != null ? roleId.hashCode() : 0);
        result = 31 * result + (roleKey != null ? roleKey.hashCode() : 0);
        result = 31 * result + (roleName != null ? roleName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserVO3{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", roleId=" + roleId +
                ", roleKey='" + roleKey + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}