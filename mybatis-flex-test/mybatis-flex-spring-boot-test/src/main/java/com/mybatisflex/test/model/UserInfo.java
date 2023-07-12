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
import com.mybatisflex.annotation.RelationManyToMany;

import java.util.List;
import java.util.Objects;

/**
 * 用户信息。
 *
 * @author 王帅
 * @since 2023-06-07
 */
public class UserInfo {

    private Integer userId;
    private String userName;
    private String password;
    @Column(ignore = true)
    private String idNumber;

    @RelationManyToMany(
        selfField = "userId",
        targetField = "roleId",
        joinTable = "tb_user_role",
        joinSelfColumn = "user_id",
        joinTargetColumn = "role_id"
    )
    private List<Role> roleList;

    @RelationManyToMany(
        selfField = "userId",
        targetField = "orderId",
        targetTable = "tb_order",
        joinTable = "tb_user_order",
        joinSelfColumn = "user_id",
        joinTargetColumn = "order_id"
    )
    private List<OrderInfo> orderInfoList;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public List<OrderInfo> getOrderInfoList() {
        return orderInfoList;
    }

    public void setOrderInfoList(List<OrderInfo> orderInfoList) {
        this.orderInfoList = orderInfoList;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
            "userId=" + userId +
            ", userName='" + userName + '\'' +
            ", password='" + password + '\'' +
            ", idNumber='" + idNumber + '\'' +
            ", roleList=" + roleList +
            ", orderInfoList=" + orderInfoList +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserInfo userInfo = (UserInfo) o;

        if (!Objects.equals(userId, userInfo.userId)) {
            return false;
        }
        if (!Objects.equals(userName, userInfo.userName)) {
            return false;
        }
        if (!Objects.equals(password, userInfo.password)) {
            return false;
        }
        if (!Objects.equals(idNumber, userInfo.idNumber)) {
            return false;
        }
        if (!Objects.equals(roleList, userInfo.roleList)) {
            return false;
        }
        return Objects.equals(orderInfoList, userInfo.orderInfoList);
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (idNumber != null ? idNumber.hashCode() : 0);
        result = 31 * result + (roleList != null ? roleList.hashCode() : 0);
        result = 31 * result + (orderInfoList != null ? orderInfoList.hashCode() : 0);
        return result;
    }

}
