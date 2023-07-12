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

import com.mybatisflex.annotation.RelationManyToMany;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 订单信息。
 *
 * @author 王帅
 * @since 2023-06-07
 */
public class OrderInfo {

    private Integer orderId;
    private LocalDateTime createTime;

    @RelationManyToMany(
        selfField = "orderId",
        targetField = "goodId",
        joinTable = "tb_order_good",
        joinSelfColumn = "order_id",
        joinTargetColumn = "good_id"
    )
    private List<Good> goodList;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public List<Good> getGoodList() {
        return goodList;
    }

    public void setGoodList(List<Good> goodList) {
        this.goodList = goodList;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
            "orderId=" + orderId +
            ", createTime=" + createTime +
            ", goodList=" + goodList +
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

        OrderInfo orderInfo = (OrderInfo) o;

        if (!Objects.equals(orderId, orderInfo.orderId)) {
            return false;
        }
        if (!Objects.equals(createTime, orderInfo.createTime)) {
            return false;
        }
        return Objects.equals(goodList, orderInfo.goodList);
    }

    @Override
    public int hashCode() {
        int result = orderId != null ? orderId.hashCode() : 0;
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (goodList != null ? goodList.hashCode() : 0);
        return result;
    }

}
