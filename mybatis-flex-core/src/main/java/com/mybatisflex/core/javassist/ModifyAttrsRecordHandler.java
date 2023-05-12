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


import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;


public class ModifyAttrsRecordHandler implements MethodHandler {

    private final Set<String>  modifyAttrs = new LinkedHashSet<>();

    public Set<String> getModifyAttrs() {
        return modifyAttrs;
    }


    @Override
    public Object invoke(Object self, Method originalMethod, Method proxyMethod, Object[] args) throws Throwable {

        if (originalMethod.getName().startsWith("set")){
            String property = StringUtil.firstCharToLowerCase(originalMethod.getName().substring(3));
            modifyAttrs.add(property);
//            ((ModifyAttrsRecord) self).addModifyAttr(property);
        }

        return proxyMethod.invoke(self, args);
    }



}


