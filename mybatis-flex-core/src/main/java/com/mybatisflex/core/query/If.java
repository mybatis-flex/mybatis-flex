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
package com.mybatisflex.core.query;

import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class If {


    /**
     * 判断对象是否为空
     */
   public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * 判断对象是否非空
     */
    public static boolean notNull(Object object) {
        return !isNull(object);
    }

    /**
     * 查看某个对象是否为空，支持数组、集合、map 等
     * @param object
     */
    public static boolean notEmpty(Object object){
        if (object == null){
            return false;
        }

        if (object instanceof Collection){
            return !((Collection<?>) object).isEmpty();
        }

        if (ClassUtil.isArray(object.getClass())){
            return Array.getLength(object) >0;
        }

        if (object instanceof Map){
            return !((Map<?, ?>) object).isEmpty();
        }

        if (object instanceof String){
            return StringUtil.isNotBlank((String) object);
        }
        return true;
    }


    /**
     * 查看某个对象是否为空数据 或者 null
     * @param object
     */
    public static boolean isEmpty(Object object){
        return !notEmpty(object);
    }


    /**
     * 查看某个 string 对象是否有文本内容
     * @param object
     */
    public static boolean hasText(Object object){
        return object != null && StringUtil.isNotBlank((String) object);
    }

}
