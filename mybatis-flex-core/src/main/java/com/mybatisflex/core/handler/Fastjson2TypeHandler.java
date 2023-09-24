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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author michael
 */
public class Fastjson2TypeHandler extends BaseJsonTypeHandler<Object> {

    private final Class<?> propertyType;
    private Class<?> genericType;
    private Type type;

    private boolean supportAutoType = false;

    public Fastjson2TypeHandler(Class<?> propertyType) {
        this.propertyType = propertyType;
        this.supportAutoType = propertyType.isInterface() || Modifier.isAbstract(propertyType.getModifiers());
    }


    public Fastjson2TypeHandler(Class<?> propertyType, Class<?> genericType) {
        this.propertyType = propertyType;
        this.genericType = genericType;
        this.type = TypeReference.collectionType((Class<? extends Collection>) propertyType, genericType);

        Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        if (actualTypeArgument instanceof Class) {
            this.supportAutoType = ((Class<?>) actualTypeArgument).isInterface()
                || Modifier.isAbstract(((Class<?>) actualTypeArgument).getModifiers());
        }
    }

    @Override
    protected Object parseJson(String json) {
        if (genericType != null && Collection.class.isAssignableFrom(propertyType)) {
            if (supportAutoType) {
                return JSON.parseArray(json, Object.class, JSONReader.Feature.SupportAutoType);
            } else {
                return JSON.parseObject(json, type);
            }

        } else {
            if (supportAutoType) {
                return JSON.parseObject(json, Object.class, JSONReader.Feature.SupportAutoType);
            } else {
                return JSON.parseObject(json, propertyType);
            }
        }
    }

    @Override
    protected String toJson(Object object) {
        if (supportAutoType) {
            return JSON.toJSONString(object
                , JSONWriter.Feature.WriteMapNullValue
                , JSONWriter.Feature.WriteNullListAsEmpty
                , JSONWriter.Feature.WriteNullStringAsEmpty, JSONWriter.Feature.WriteClassName
            );
        } else {
            return JSON.toJSONString(object
                , JSONWriter.Feature.WriteMapNullValue
                , JSONWriter.Feature.WriteNullListAsEmpty
                , JSONWriter.Feature.WriteNullStringAsEmpty
            );
        }
    }
}
