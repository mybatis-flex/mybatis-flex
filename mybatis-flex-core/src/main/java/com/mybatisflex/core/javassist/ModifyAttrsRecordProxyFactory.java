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
package com.mybatisflex.core.javassist;

import org.apache.ibatis.javassist.util.proxy.ProxyFactory;
import org.apache.ibatis.javassist.util.proxy.ProxyObject;
import org.apache.ibatis.logging.LogFactory;

import java.util.Arrays;


public class ModifyAttrsRecordProxyFactory {

    private static final ModifyAttrsRecordProxyFactory instance = new ModifyAttrsRecordProxyFactory();

    public static ModifyAttrsRecordProxyFactory getInstance(){
        return instance;
    }

    private ModifyAttrsRecordProxyFactory(){}

    public <T> T get(Class<T> target) {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(target);

        Class<?>[] interfaces = Arrays.copyOf(target.getInterfaces(), target.getInterfaces().length + 1);
        interfaces[interfaces.length - 1] = ModifyAttrsRecord.class;
        factory.setInterfaces(interfaces);


        final Class<?> proxyClass = factory.createClass();

        T proxyObject = null;
        try {
            proxyObject = (T) proxyClass.newInstance();
            ((ProxyObject) proxyObject).setHandler(new ModifyAttrsRecordHandler());
        } catch (Throwable e) {
            LogFactory.getLog(ModifyAttrsRecordProxyFactory.class).error(e.toString(),e);
        }

        return proxyObject;
    }




}



