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
package com.mybatisflex.core.util;

import com.mybatisflex.core.javassist.ModifyAttrsRecordProxyFactory;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;

public class UpdateEntity {

    private static ReflectorFactory reflectorFactory = new DefaultReflectorFactory();



    public static <T> T wrap(Class<T> clazz) {
        return ModifyAttrsRecordProxyFactory.getInstance().get(clazz);
    }



    public static <T> T ofNotNull(T entity) {
        Class<?> usefulClass = ClassUtil.getUsefulClass(entity.getClass());

        T newEntity = (T) wrap(usefulClass);

        Reflector reflector = reflectorFactory.findForClass(usefulClass);
        String[] propertyNames = reflector.getGetablePropertyNames();

        for (String propertyName : propertyNames) {
            try {
                Object value = reflector.getGetInvoker(propertyName)
                        .invoke(entity, null);
                if (value != null) {
                    reflector.getSetInvoker(propertyName).invoke(newEntity, new Object[]{value});
                }
            } catch (Exception e) {
                //ignore();
            }
        }

        return newEntity;
    }


}
