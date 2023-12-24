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

package com.mybatisflex.test.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryCondition;

import java.io.*;

/**
 * 序列化工具类。
 *
 * @author 王帅
 * @since 2023-06-10
 */
public class SerialUtil {

    public static byte[] writeObject(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public static Object readObject(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public static <T> T cloneObject(Object obj) {
        //noinspection unchecked
        return (T) readObject(writeObject(obj));
    }

    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.FieldBased,
            JSONWriter.Feature.WriteClassName,
            JSONWriter.Feature.NotWriteRootClassName,
            JSONWriter.Feature.ReferenceDetection);
    }

    public static <T> T parseObject(String str, Class<T> tClass) {
        return JSON.parseObject(str, tClass, JSONReader.Feature.FieldBased,
            JSONReader.Feature.SupportClassForName);
    }

    public static <T> T cloneObject(Object obj, Class<T> tClass) {
        return parseObject(toJSONString(obj), tClass);
    }


    /**
     * 使用jackson对QueryWrapper进行序列化反序列化操作，需要注意QueryCondition的protected属性以及prev和next的递归问题。
     *
     * @return Jackson序列化映射
     */
    public static ObjectMapper jacksonMapper(){
        ObjectMapper mapper = new ObjectMapper();
        // 为了将QueryWrapper里的protected属性可见
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC);
        // 避免QueryCondition里的prev和next在序列化时出现递归调用错误
        mapper.addMixIn(QueryCondition.class,QueryConditionMixIn.class);
        return mapper;
    }

    /**
     * 因无法修改QueryCondition而添加的映射属性包装
     */
    class QueryConditionMixIn{
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        protected QueryCondition prev;
    }
}
