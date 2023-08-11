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
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.activerecord.Model;

import java.util.Objects;

/**
 * 商品。
 *
 * @author 王帅
 * @since 2023-06-07
 */
@Table(value = "tb_good", onSet = GoodOnSetListener.class)
public class Good extends Model<Good> {

    @Id(keyType = KeyType.Auto)
    private Integer goodId;
    private String name;
    private Double price;

    public static Good create() {
        return new Good();
    }

    public Integer getGoodId() {
        return goodId;
    }

    public Good setGoodId(Integer goodId) {
        this.goodId = goodId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Good setName(String name) {
        this.name = name;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public Good setPrice(Double price) {
        this.price = price;
        return this;
    }

    @Override
    public String toString() {
        return "Good{" +
            "goodId=" + goodId +
            ", name='" + name + '\'' +
            ", price=" + price +
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

        Good good = (Good) o;

        if (!Objects.equals(goodId, good.goodId)) {
            return false;
        }
        if (!Objects.equals(name, good.name)) {
            return false;
        }
        return Objects.equals(price, good.price);
    }

    @Override
    public int hashCode() {
        int result = goodId != null ? goodId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        return result;
    }

}
