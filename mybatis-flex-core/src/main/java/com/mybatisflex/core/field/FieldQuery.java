/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.field;

import com.mybatisflex.core.query.QueryWrapper;

import java.io.Serializable;

public class FieldQuery implements Serializable {

    private String field;
//    private Class<?> mappingType;
    private QueryWrapper queryWrapper;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

//    public Class<?> getMappingType() {
//        return mappingType;
//    }
//
//    public void setMappingType(Class<?> mappingType) {
//        this.mappingType = mappingType;
//    }

    public QueryWrapper getQueryWrapper() {
        return queryWrapper;
    }

    public void setQueryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }
}
