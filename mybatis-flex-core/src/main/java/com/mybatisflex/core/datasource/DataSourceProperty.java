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
package com.mybatisflex.core.datasource;

import com.mybatisflex.core.util.StringUtil;

public enum DataSourceProperty {

    URL("url", new String[]{"url", "jdbcUrl"}),
    USERNAME("username", new String[]{"username"}),
    PASSWORD("password", new String[]{"password"}),
    ;

    String property;
    String[] methodFlags;

    DataSourceProperty(String property, String[] methodFlags) {
        this.property = property;
        this.methodFlags = methodFlags;
    }

    String[] getGetterMethods() {
        String[] getterMethods = new String[methodFlags.length];
        for (int i = 0; i < methodFlags.length; i++) {
            getterMethods[i] = "get" + StringUtil.firstCharToUpperCase(methodFlags[i]);
        }
        return getterMethods;
    }

    String[] getSetterMethods() {
        String[] getterMethods = new String[methodFlags.length];
        for (int i = 0; i < methodFlags.length; i++) {
            getterMethods[i] = "set" + StringUtil.firstCharToUpperCase(methodFlags[i]);
        }
        return getterMethods;
    }
}
