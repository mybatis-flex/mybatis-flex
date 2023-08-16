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
public interface UpdateWrapper extends PropertySetter<UpdateWrapper>, Serializable {

    default Map<String, Object> getUpdates() {
        ModifyAttrsRecordHandler handler = (ModifyAttrsRecordHandler) ((ProxyObject) this).getHandler();
        return handler.getUpdates();
    }

    @Override
    default UpdateWrapper set(String property, Object value, boolean isEffective) {
        if (isEffective) {
            if (value instanceof QueryWrapper
                || value instanceof QueryColumn
                || value instanceof QueryCondition) {
                getUpdates().put(property, new RawValue(value));
            } else {
                getUpdates().put(property, value);
            }
        }
        return this;
    }

    @Override
    default UpdateWrapper set(QueryColumn property, Object value, boolean isEffective) {
        if (isEffective) {
            if (value instanceof QueryWrapper
                || value instanceof QueryColumn
                || value instanceof QueryCondition) {
                getUpdates().put(property.getName(), new RawValue(value));
            } else {
                getUpdates().put(property.getName(), value);
            }
        }
        return this;
    }

    @Override
    default <T> UpdateWrapper set(LambdaGetter<T> property, Object value, boolean isEffective) {
        if (isEffective) {
            if (value instanceof QueryWrapper
                || value instanceof QueryColumn
                || value instanceof QueryCondition) {
                getUpdates().put(LambdaUtil.getFieldName(property), new RawValue(value));
            } else {
                getUpdates().put(LambdaUtil.getFieldName(property), value);
            }
        }
        return this;
    }

    @Override
    default UpdateWrapper setRaw(String property, Object value, boolean isEffective) {
        if (isEffective) {
            getUpdates().put(property, new RawValue(value));
        }
        return this;
    }

    @Override
    default UpdateWrapper setRaw(QueryColumn property, Object value, boolean isEffective) {
        if (isEffective) {
            getUpdates().put(property.getName(), new RawValue(value));
        }
        return this;
    }

    @Override
    default <T> UpdateWrapper setRaw(LambdaGetter<T> property, Object value, boolean isEffective) {
        if (isEffective) {
            getUpdates().put(LambdaUtil.getFieldName(property), new RawValue(value));
        }
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
