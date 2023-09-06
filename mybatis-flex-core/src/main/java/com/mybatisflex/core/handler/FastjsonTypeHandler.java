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
package com.mybatisflex.core.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @author michael
 */
public class FastjsonTypeHandler extends BaseJsonTypeHandler<Object> {

    private final Class<?> propertyType;
    private Class<?> genericType;
    private Type type;

    public FastjsonTypeHandler(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    public FastjsonTypeHandler(Class<?> propertyType, Class<?> genericType) {
        this.propertyType = propertyType;
        this.genericType = genericType;
        this.type = new ParameterizedTypeImpl(propertyType, genericType);
    }

    @Override
    protected Object parseJson(String json) {
        if (genericType != null && Collection.class.isAssignableFrom(propertyType)) {
            return JSON.parseObject(json, type);
        } else {
            return JSON.parseObject(json, propertyType);
        }
    }

    @Override
    protected String toJson(Object object) {
        return JSON.toJSONString(object, SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty);
    }


    public static class ParameterizedTypeImpl implements ParameterizedType {

        private final Type[] actualTypeArguments;
        private final Type ownerType;
        private final Type rawType;

        public ParameterizedTypeImpl(Type rawType, Type... actualTypeArguments) {
            this.rawType = rawType;
            this.actualTypeArguments = actualTypeArguments;
            this.ownerType = null;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return this.actualTypeArguments;
        }

        @Override
        public Type getOwnerType() {
            return this.ownerType;
        }

        @Override
        public Type getRawType() {
            return this.rawType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;
                if (!Arrays.equals(this.actualTypeArguments, that.actualTypeArguments)) {
                    return false;
                } else {
                    if (this.ownerType != null) {
                        if (this.ownerType.equals(that.ownerType)) {
                            return Objects.equals(this.rawType, that.rawType);
                        }
                    } else if (that.ownerType == null) {
                        return Objects.equals(this.rawType, that.rawType);
                    }

                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int result = this.actualTypeArguments != null ? Arrays.hashCode(this.actualTypeArguments) : 0;
            result = 31 * result + (this.ownerType != null ? this.ownerType.hashCode() : 0);
            result = 31 * result + (this.rawType != null ? this.rawType.hashCode() : 0);
            return result;
        }
    }


}
