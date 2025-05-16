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
package com.mybatisflex.core.update;

import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.MapUtil;
import org.apache.ibatis.javassist.util.proxy.Proxy;
import org.apache.ibatis.javassist.util.proxy.ProxyFactory;
import org.apache.ibatis.javassist.util.proxy.ProxyObject;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author michael
 */
public class ModifyAttrsRecordProxyFactory {

    protected static final Map<Class<?>, Class<?>> CACHE = new ConcurrentHashMap<>();

    private static final ModifyAttrsRecordProxyFactory INSTANCE = new ModifyAttrsRecordProxyFactory();

    public static ModifyAttrsRecordProxyFactory getInstance() {
        return INSTANCE;
    }

    private ModifyAttrsRecordProxyFactory() {
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> target) {
        Class<?> proxyClass = MapUtil.computeIfAbsent(CACHE, target, aClass -> {
            Class<?>[] interfaces = Arrays.copyOf(target.getInterfaces(), target.getInterfaces().length + 1);
            interfaces[interfaces.length - 1] = UpdateWrapper.class;
            ProxyFactory factory = new ProxyFactory();
            factory.setSuperclass(target);
            factory.setInterfaces(interfaces);
            return factory.createClass();
        });

        T proxyObject;
        try {
            proxyObject = (T) ClassUtil.newInstance(proxyClass);
        } catch (Exception e) {
            throw FlexExceptions.wrap(e, "请为实体类 %s 添加公开的无参构造器！", target.getCanonicalName());
        }
        if (proxyObject instanceof ProxyObject) {
            ((ProxyObject) proxyObject).setHandler(new ModifyAttrsRecordHandler());
        } else if (proxyObject instanceof Proxy) {
            ((Proxy) proxyObject).setHandler(new ModifyAttrsRecordHandler());
        } else {
            throw FlexExceptions.wrap("为实体类 %s 设置字段更新处理器时出错，获取的实体类代理对象既不是 ProxyObject 的实例，也不是 Proxy 的实例", target.getCanonicalName());
        }
        return proxyObject;
    }

}
