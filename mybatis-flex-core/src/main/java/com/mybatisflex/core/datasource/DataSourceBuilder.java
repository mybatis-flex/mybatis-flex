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

import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.exception.locale.LocalizedFormats;
import com.mybatisflex.core.util.ConvertUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.invoker.Invoker;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class DataSourceBuilder {

    private static final Map<String, String> dataSourceAlias = new HashMap<>();

    static {
        dataSourceAlias.put("druid", "com.alibaba.druid.pool.DruidDataSource");
        dataSourceAlias.put("hikari", "com.zaxxer.hikari.HikariDataSource");
        dataSourceAlias.put("hikaricp", "com.zaxxer.hikari.HikariDataSource");
        dataSourceAlias.put("bee", "cn.beecp.BeeDataSource");
        dataSourceAlias.put("beecp", "cn.beecp.BeeDataSource");
        dataSourceAlias.put("dbcp", "org.apache.commons.dbcp2.BasicDataSource");
        dataSourceAlias.put("dbcp2", "org.apache.commons.dbcp2.BasicDataSource");
    }

    private final Map<String, String> dataSourceProperties;

    public DataSourceBuilder(Map<String, String> dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    public DataSource build() {
        String dataSourceClassName = null;
        String type = dataSourceProperties.get("type");
        if (StringUtil.isNotBlank(type)) {
            dataSourceClassName = dataSourceAlias.getOrDefault(type, type);
        } else {
            dataSourceClassName = detectDataSourceClass();
        }


        if (StringUtil.isBlank(dataSourceClassName)) {
            if (StringUtil.isBlank(type)) {
                throw FlexExceptions.wrap(LocalizedFormats.DATASOURCE_TYPE_BLANK);
            } else {
                throw FlexExceptions.wrap(LocalizedFormats.DATASOURCE_TYPE_NOT_FIND, type);
            }
        }

        try {
            Class<?> dataSourceClass = Class.forName(dataSourceClassName);
            Object dataSourceObject = dataSourceClass.newInstance();
            setDataSourceProperties(dataSourceObject);
            return (DataSource) dataSourceObject;
        } catch (Exception e) {
            throw FlexExceptions.wrap(e, LocalizedFormats.DATASOURCE_CAN_NOT_INSTANCE, dataSourceClassName);
        }
    }

    private void setDataSourceProperties(Object dataSourceObject) throws Exception {
        Reflector reflector = new Reflector(dataSourceObject.getClass());
        for (String attr : dataSourceProperties.keySet()) {
            String value = dataSourceProperties.get(attr);
            String camelAttr = attrToCamel(attr);
            if ("url".equals(camelAttr) || "jdbcUrl".equals(camelAttr)) {
                if (reflector.hasSetter("url")) {
                    reflector.getSetInvoker("url").invoke(dataSourceObject, new Object[]{value});
                } else if (reflector.hasSetter("jdbcUrl")) {
                    reflector.getSetInvoker("jdbcUrl").invoke(dataSourceObject, new Object[]{value});
                }
            } else {
                if (reflector.hasSetter(camelAttr)) {
                    Invoker setInvoker = reflector.getSetInvoker(camelAttr);
                    setInvoker.invoke(dataSourceObject, new Object[]{ConvertUtil.convert(value, setInvoker.getType())});
                }
            }
        }
    }


    public static String attrToCamel(String string) {
        int strLen = string.length();
        StringBuilder sb = new StringBuilder(strLen);
        for (int i = 0; i < strLen; i++) {
            char c = string.charAt(i);
            if (c == '-') {
                if (++i < strLen) {
                    sb.append(Character.toUpperCase(string.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    private String detectDataSourceClass() {
        String[] detectClassNames = new String[]{
            "com.alibaba.druid.pool.DruidDataSource",
            "com.zaxxer.hikari.HikariDataSource",
            "cn.beecp.BeeDataSource",
            "org.apache.commons.dbcp2.BasicDataSource",
        };

        for (String detectClassName : detectClassNames) {
            String result = doDetectDataSourceClass(detectClassName);
            if (result != null) {
                return result;
            }
        }

        return null;
    }


    private String doDetectDataSourceClass(String className) {
        try {
            Class.forName(className);
            return className;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
