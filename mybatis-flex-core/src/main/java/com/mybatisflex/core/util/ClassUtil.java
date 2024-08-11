/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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
package com.mybatisflex.core.util;


import org.apache.ibatis.javassist.util.proxy.ProxyObject;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * 类实例创建者创建者
 *
 * @author michael
 * @date 17/3/21
 */
@SuppressWarnings("unchecked")
public class ClassUtil {

    private ClassUtil() {
    }

    private static final String[] OBJECT_METHODS = new String[]{
        "toString",
        "getClass",
        "equals",
        "hashCode",
        "wait",
        "notify",
        "notifyAll",
        "clone",
        "finalize"
    };

    //proxy frameworks
    private static final List<String> PROXY_CLASS_NAMES = Arrays.asList("net.sf.cglib.proxy.Factory"
        // cglib
        , "org.springframework.cglib.proxy.Factory"

        // javassist
        , "javassist.util.proxy.ProxyObject"
        , "org.apache.ibatis.javassist.util.proxy.ProxyObject");
    private static final String ENHANCER_BY = "$$EnhancerBy";
    private static final String JAVASSIST_BY = "_$$_";

    public static boolean isProxy(Class<?> clazz) {
        for (Class<?> cls : clazz.getInterfaces()) {
            if (PROXY_CLASS_NAMES.contains(cls.getName())) {
                return true;
            }
        }
        //java proxy
        return Proxy.isProxyClass(clazz);
    }

    public static <T> Class<T> getUsefulClass(Class<T> clazz) {

        if (ProxyObject.class.isAssignableFrom(clazz)) {
            return (Class<T>) clazz.getSuperclass();
        }

        if (isProxy(clazz)) {
            return getJdkProxySuperClass(clazz);
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


    public static Class<?> getWrapType(Class<?> clazz) {
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

    public static boolean canInstance(int mod) {
        return !Modifier.isAbstract(mod) || !Modifier.isInterface(mod);
    }


    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<?> defaultConstructor = null;
            Constructor<?> otherConstructor = null;

            Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
            for (Constructor<?> constructor : declaredConstructors) {
                if (constructor.getParameterCount() == 0 && Modifier.isPublic(constructor.getModifiers())) {
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
            // 没有任何构造函数的情况下，去查找 static 工厂方法，满足 lombok 注解的需求
            else {
                Method factoryMethod = ClassUtil.getFirstMethod(clazz, m -> m.getParameterCount() == 0
                    && m.getReturnType().isAssignableFrom(clazz)
                    && Modifier.isPublic(m.getModifiers())
                    && Modifier.isStatic(m.getModifiers()));

                if (factoryMethod != null) {
                    return (T) factoryMethod.invoke(null);
                }
            }
            throw new IllegalArgumentException("the class \"" + clazz.getName() + "\" has no constructor.");
        } catch (Exception e) {
            throw new RuntimeException("Can not newInstance class: " + clazz.getName(), e);
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
            throw new RuntimeException(e.toString(), e);
        }
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


    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        doGetFields(clazz, fields, null, false);
        return fields;
    }

    public static List<Field> getAllFields(Class<?> clazz, Predicate<Field> predicate) {
        List<Field> fields = new ArrayList<>();
        doGetFields(clazz, fields, predicate, false);
        return fields;
    }

    public static Field getFirstField(Class<?> clazz, Predicate<Field> predicate) {
        List<Field> fields = new ArrayList<>();
        doGetFields(clazz, fields, predicate, true);
        return fields.isEmpty() ? null : fields.get(0);
    }

    /**
     * 应用类及其除Object外的所有父类
     *
     * @param clazz  需要应用的类
     * @param checkToContinue 应用当前类并检测是否继续应用, 返回false则停止应用, 返回true继续向上取父类
     * @author KAMOsama
     */
    public static void applyAllClass(Class<?> clazz,  Predicate<Class<?>> checkToContinue) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class && checkToContinue.test(currentClass)) {
            currentClass = currentClass.getSuperclass();
        }
    }

    private static void doGetFields(Class<?> clazz, List<Field> fields, Predicate<Field> predicate, boolean firstOnly) {
        applyAllClass(clazz, currentClass -> {
            Field[] declaredFields = currentClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (predicate == null || predicate.test(declaredField)) {
                    fields.add(declaredField);
                    if (firstOnly) {
                        break;
                    }
                }
            }
            // 不止要获取第一个或集合为空就继续获取遍历父类
            return !firstOnly || fields.isEmpty();
        });
    }

    public static List<Method> getAllMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        doGetMethods(clazz, methods, null, false);
        return methods;
    }

    public static List<Method> getAllMethods(Class<?> clazz, Predicate<Method> predicate) {
        List<Method> methods = new ArrayList<>();
        doGetMethods(clazz, methods, predicate, false);
        return methods;
    }

    public static Method getAnyMethod(Class<?> clazz, String... methodNames) {
        return getFirstMethod(clazz, method -> ArrayUtil.contains(methodNames, method.getName()));
    }

    public static Method getFirstMethod(Class<?> clazz, Predicate<Method> predicate) {
        List<Method> methods = new ArrayList<>();
        doGetMethods(clazz, methods, predicate, true);
        return methods.isEmpty() ? null : methods.get(0);
    }


    private static void doGetMethods(Class<?> clazz, List<Method> methods, Predicate<Method> predicate, boolean firstOnly) {
        applyAllClass(clazz, currentClass -> {
            Method[] declaredMethods = currentClass.getDeclaredMethods();
            if (currentClass.isInterface()) {
                for (Method method : declaredMethods) {
                    // 接口类只需要获取 default 方法
                    if (method.isDefault() && (predicate == null || predicate.test(method))) {
                        methods.add(method);
                        if (firstOnly) {
                            break;
                        }
                    }
                }
            } else {
                for (Method method : declaredMethods) {
                    if (predicate == null || predicate.test(method)) {
                        methods.add(method);
                        if (firstOnly) {
                            break;
                        }
                    }
                }
            }
            // 只获取第一个并且集合不为空就结束遍历
            if (firstOnly && !methods.isEmpty()) {
                return false;
            }
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                doGetMethods(anInterface, methods, predicate, firstOnly);
                // 只获取第一个并且集合不为空就结束遍历
                if (firstOnly && !methods.isEmpty()){
                    return false;
                }
            }
            return true;
        });
    }

    private static <T> Class<T> getJdkProxySuperClass(Class<T> clazz) {
        final Class<?> proxyClass = Proxy.getProxyClass(clazz.getClassLoader(), clazz.getInterfaces());
        return (Class<T>) proxyClass.getInterfaces()[0];
    }


    public static boolean isGetterMethod(Method method, String property) {
        String methodName = method.getName();
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return StringUtil.firstCharToUpperCase(property).equals(methodName.substring(3));
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            return StringUtil.firstCharToUpperCase(property).equals(methodName.substring(2));
        } else {
            return false;
        }
    }

    public static boolean isObjectMethod(String methodName) {
        return ArrayUtil.contains(OBJECT_METHODS, methodName);
    }

}
