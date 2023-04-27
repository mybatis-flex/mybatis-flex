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
import java.util.function.Predicate;

/**
 * 类实例创建者创建者
 * Created by michael on 17/3/21.
 */
public class ClassUtil {

    //proxy frameworks
    private static final List<String> PROXY_CLASS_NAMES = Arrays.asList("net.sf.cglib.proxy.Factory"
            // cglib
            , "org.springframework.cglib.proxy.Factory"

            // javassist
            , "javassist.util.proxy.ProxyObject"
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


    public static Class<?> wrap(Class<?> clazz) {
        if (clazz == null || !clazz.isPrimitive()) {
            return clazz;
        }
        if (clazz == Integer.TYPE) {
            return Integer.class;
        } else if (clazz == Long.TYPE) {
            return Long.class;
        } else if (clazz == Boolean.TYPE) {
            return Boolean.class;
        } else if (clazz == Float.TYPE) {
            return Float.class;
        } else if (clazz == Double.TYPE) {
            return Double.class;
        } else if (clazz == Short.TYPE) {
            return Short.class;
        } else if (clazz == Character.TYPE) {
            return Character.class;
        } else if (clazz == Byte.TYPE) {
            return Byte.class;
        } else if (clazz == Void.TYPE) {
            return Void.class;
        }
        return clazz;
    }


    public static boolean isArray(Class<?> clazz) {
        return clazz.isArray()
                || clazz == int[].class
                || clazz == long[].class
                || clazz == short[].class
                || clazz == float[].class
                || clazz == double[].class;
    }


    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<?> defaultConstructor = null;
            Constructor<?> otherConstructor = null;

            Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
            for (Constructor<?> constructor : declaredConstructors) {
                if (constructor.getParameterCount() == 0) {
                    defaultConstructor = constructor;
                } else if (Modifier.isPublic(constructor.getModifiers())) {
                    otherConstructor = constructor;
                }
            }
            if (defaultConstructor != null) {
                return (T) defaultConstructor.newInstance();
            } else if (otherConstructor != null) {
                Class<?>[] parameterTypes = otherConstructor.getParameterTypes();
                Object[] parameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (parameterTypes[i].isPrimitive()) {
                        parameters[i] = ConvertUtil.getPrimitiveDefaultValue(parameterTypes[i]);
                    } else {
                        parameters[i] = null;
                    }
                }
                return (T) otherConstructor.newInstance(parameters);
            }
            throw new IllegalArgumentException("the class \"" + clazz.getName() + "\" has no constructor.");
        } catch (Exception e) {
            throw new RuntimeException("Can not newInstance class: " + clazz.getName());
        }
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
            throw new IllegalArgumentException("Can not find constructor by paras: \"" + Arrays.toString(paras) + "\" in class[" + clazz.getName() + "]");
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


    public static List<Field> getAllFields(Class<?> cl) {
        List<Field> fields = new ArrayList<>();
        doGetFields(cl, fields, null);
        return fields;
    }

    public static List<Field> getAllFields(Class<?> cl, Predicate<Field> predicate) {
        List<Field> fields = new ArrayList<>();
        doGetFields(cl, fields, predicate);
        return fields;
    }

    private static void doGetFields(Class<?> cl, List<Field> fields, Predicate<Field> predicate) {
        if (cl == null || cl == Object.class) {
            return;
        }

        Field[] declaredFields = cl.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (predicate == null || predicate.test(declaredField)) {
                fields.add(declaredField);
            }
        }

        doGetFields(cl.getSuperclass(), fields, predicate);
    }

    public static List<Method> getAllMethods(Class<?> cl) {
        List<Method> methods = new ArrayList<>();
        doGetMethods(cl, methods, null);
        return methods;
    }

    public static List<Method> getAllMethods(Class<?> cl, Predicate<Method> predicate) {
        List<Method> methods = new ArrayList<>();
        doGetMethods(cl, methods, predicate);
        return methods;
    }


    private static void doGetMethods(Class<?> cl, List<Method> methods, Predicate<Method> predicate) {
        if (cl == null || cl == Object.class) {
            return;
        }

        Method[] declaredMethods = cl.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (predicate == null || predicate.test(method)) {
                methods.add(method);
            }
        }

        doGetMethods(cl.getSuperclass(), methods, predicate);
    }

}
