/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

/**
 * UnMapped
 *
 * @author wy
 * @version 1.0
 * @date 2024/9/12 11:28
 **/
@Getter
@Setter
@Table("tb_unmapped_user")
public class UnmappedUser extends UnmappedBaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer age;

    private String name;

    @Override
    public String toString() {
        return "UnmappedUser{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", age=" + age +
            (CollectionUtils.isEmpty(unmappedMap) ? "" : ", unmappedMap=" + unmappedMap.toString())
            + '}';
    }
}
