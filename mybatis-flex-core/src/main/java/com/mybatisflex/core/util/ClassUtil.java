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


import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 类实例创建者创建者
 * Created by michael on 17/3/21.
 */
public class ClassUtil {


    //proxy frameworks
    private static final List<String> PROXY_CLASS_NAMES = Arrays.asList("net.sf.cglib.proxy.Factory"
            // cglib
            , "org.springframework.cglib.proxy.Factory"
            , "javassist.util.proxy.ProxyObject"
            // javassist
            , "org.apache.ibatis.javassist.util.proxy.ProxyObject");

    public static boolean isProxy(Class<?> clazz) {
        for (Class<?> cls : clazz.getInterfaces()) {
            if (PROXY_CLASS_NAMES.contains(cls.getName())) {
                return true;
            }
        }
        //java proxy
        return Proxy.isProxyClass(clazz);
    }

    private static final String ENHANCER_BY = "$$EnhancerBy";
    private static final String JAVASSIST_BY = "_$$_";

    public static <T> Class<T> getUsefulClass(Class<T> clazz) {
        if (isProxy(clazz)) {
            return (Class<T>) clazz.getSuperclass();
        }

        //ControllerTest$ServiceTest$$EnhancerByGuice$$40471411#hello   -------> Guice
        //com.demo.blog.Blog$$EnhancerByCGLIB$$69a17158  ----> CGLIB
        //io.jboot.test.app.TestAppListener_$$_jvstb9f_0 ------> javassist

        final String name = clazz.getName();
        if (name.contains(ENHANCER_BY) || name.contains(JAVASSIST_BY)) {
            return (Class<T>) clazz.getSuperclass();
        }

        return clazz;
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            return (T) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static <T> T newInstance(Class<T> clazz, Object... paras) {
        try {
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                if (isMatchedParas(constructor, paras)) {
                    Object ret = constructor.newInstance(paras);
                    return (T) ret;
                }
            }

            throw new IllegalArgumentException("Can not matched constructor by paras" + Arrays.toString(paras) + " for class: " + clazz.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private static boolean isMatchedParas(Constructor<?> constructor, Object[] paras) {
        if (constructor.getParameterCount() == 0) {
            return paras == null || paras.length == 0;
        }

        if (constructor.getParameterCount() > 0
                && (paras == null || paras.length != constructor.getParameterCount())) {
            return false;
        }

        Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Object paraObject = paras[i];
            if (paraObject != null && !parameterType.isAssignableFrom(paraObject.getClass())) {
                return false;
            }
        }

        return true;
    }


    public static String buildMethodString(Method method) {
        StringBuilder sb = new StringBuilder()
                .append(method.getDeclaringClass().getName())
                .append(".")
                .append(method.getName())
                .append("(");

        Class<?>[] params = method.getParameterTypes();
        int in = 0;
        for (Class<?> clazz : params) {
            sb.append(clazz.getName());
            if (++in < params.length) {
                sb.append(",");
            }
        }

        return sb.append(")").toString();
    }


    public static boolean hasClass(String className) {
        try {
            Class.forName(className, false, getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    public static ClassLoader getClassLoader() {
        ClassLoader ret = Thread.currentThread().getContextClassLoader();
        return ret != null ? ret : ClassUtil.class.getClassLoader();
    }

    public static List<Field> getAllFields(Class<?> cl) {
        List<Field> fields = new ArrayList<>();
        doGetFields(cl, fields);
        return fields;
    }

    private static void doGetFields(Class<?> cl, List<Field> fields) {
        if (cl == null || cl == Object.class) {
            return;
        }

        Field[] declaredFields = cl.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            fields.add(declaredField);
        }

        doGetFields(cl.getSuperclass(), fields);
    }

}
