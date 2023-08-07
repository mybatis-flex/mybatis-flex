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
package com.mybatisflex.core.update;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;
import com.mybatisflex.core.util.UpdateEntity;
import org.apache.ibatis.javassist.util.proxy.ProxyObject;

import java.io.Serializable;
import java.util.Map;

/**
 * @author michael
 */
public interface UpdateWrapper extends Serializable {

    default Map<String, Object> getUpdates() {
        ModifyAttrsRecordHandler handler = (ModifyAttrsRecordHandler) ((ProxyObject) this).getHandler();
        return handler.getUpdates();
    }

    default UpdateWrapper set(String property, Object value, boolean condition) {
        if (condition) {
            return set(property, value);
        }
        return this;
    }

    default UpdateWrapper set(String property, Object value) {
        if (value instanceof QueryWrapper || value instanceof QueryCondition || value instanceof QueryColumn) {
            setRaw(property, value);
        } else {
            getUpdates().put(property, value);
        }
        return this;
    }

    default <T> UpdateWrapper set(LambdaGetter<T> getter, Object value, boolean condition) {
        if (condition) {
            return set(getter, value);
        }
        return this;
    }

    default <T> UpdateWrapper set(LambdaGetter<T> getter, Object value) {
        if (value instanceof QueryWrapper || value instanceof QueryCondition || value instanceof QueryColumn) {
            setRaw(getter, value);
        } else {
            getUpdates().put(LambdaUtil.getFieldName(getter), value);
        }

        return this;
    }

    default <T> UpdateWrapper set(QueryColumn queryColumn, Object value, boolean condition) {
        if (condition) {
            return set(queryColumn, value);
        }
        return this;
    }

    default <T> UpdateWrapper set(QueryColumn queryColumn, Object value) {
        if (value instanceof QueryWrapper || value instanceof QueryCondition || value instanceof QueryColumn) {
            setRaw(queryColumn, value);
        } else {
            getUpdates().put(queryColumn.getName(), value);
        }
        return this;
    }


    default UpdateWrapper setRaw(String property, Object value, boolean condition) {
        if (condition) {
            return setRaw(property, value);
        }
        return this;
    }


    default UpdateWrapper setRaw(String property, Object value) {
        getUpdates().put(property, new RawValue(value));
        return this;
    }

    default <T> UpdateWrapper setRaw(LambdaGetter<T> getter, Object value, boolean condition) {
        if (condition) {
            return setRaw(getter, value);
        }
        return this;
    }


    default <T> UpdateWrapper setRaw(LambdaGetter<T> getter, Object value) {
        getUpdates().put(LambdaUtil.getFieldName(getter), new RawValue(value));
        return this;
    }


    default <T> UpdateWrapper setRaw(QueryColumn queryColumn, Object value, boolean condition) {
        if (condition) {
            return setRaw(queryColumn, value);
        }
        return this;
    }

    default <T> UpdateWrapper setRaw(QueryColumn queryColumn, Object value) {
        getUpdates().put(queryColumn.getName(), new RawValue(value));
        return this;
    }


    static UpdateWrapper of(Object entity) {
        if (entity instanceof UpdateWrapper) {
            return (UpdateWrapper) entity;
        } else {
            return (UpdateWrapper) UpdateEntity.ofNotNull(entity);
        }
    }

}
