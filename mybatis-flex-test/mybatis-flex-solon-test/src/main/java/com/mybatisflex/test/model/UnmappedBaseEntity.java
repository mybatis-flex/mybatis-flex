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

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * UnMappedBaseEntity
 *
 * @author wy
 * @version 1.0
 * @date 2024/9/12 11:36
 **/
@Getter
@Setter
public class UnmappedBaseEntity {

    protected Map<String, Object> unmappedMap;
}
