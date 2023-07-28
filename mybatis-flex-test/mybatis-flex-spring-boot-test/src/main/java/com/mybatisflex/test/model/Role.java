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
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;

import java.util.List;
import java.util.Objects;

/**
 * 角色。
 *
 * @author 王帅
 * @since 2023-06-07
 */
@Table("tb_role")
public class Role implements Comparable<Role> {

    @Id
    private Integer roleId;
    private String roleKey;
    private String roleName;

    @Column(ignore = true)
    private List<UserVO> userVOS;

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

    public List<UserVO> getUserVOS() {
        return userVOS;
    }

    public void setUserVOS(List<UserVO> userVOS) {
        this.userVOS = userVOS;
    }

    @Override
    public String toString() {
        return "Role{" +
            "roleId=" + roleId +
            ", roleKey='" + roleKey + '\'' +
            ", roleName='" + roleName + '\'' +
            '}';
    }

    @Override
    public int compareTo(Role o) {
        return Integer.compare(this.roleId, o.roleId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Role role = (Role) o;

        if (!Objects.equals(roleId, role.roleId)) {
            return false;
        }
        if (!Objects.equals(roleKey, role.roleKey)) {
            return false;
        }
        if (!Objects.equals(roleName, role.roleName)) {
            return false;
        }
        return Objects.equals(userVOS, role.userVOS);
    }

    @Override
    public int hashCode() {
        int result = roleId != null ? roleId.hashCode() : 0;
        result = 31 * result + (roleKey != null ? roleKey.hashCode() : 0);
        result = 31 * result + (roleName != null ? roleName.hashCode() : 0);
        result = 31 * result + (userVOS != null ? userVOS.hashCode() : 0);
        return result;
    }

}
