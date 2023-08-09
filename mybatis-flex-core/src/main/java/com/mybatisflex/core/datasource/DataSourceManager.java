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

import com.mybatisflex.core.util.ClassUtil;
import org.apache.ibatis.logging.LogFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;

/**
 * @author michael
 */
public class DataSourceManager {

    private static DataSourceDecipher decipher;

    public static DataSourceDecipher getDecipher() {
        return decipher;
    }

    public static void setDecipher(DataSourceDecipher decipher) {
        DataSourceManager.decipher = decipher;
    }


    public static void decryptDataSource(DataSource dataSource) {
        if (decipher == null) {
            return;
        }

        for (DataSourceProperty property : DataSourceProperty.values()) {
            Method getterMethod = ClassUtil.getAnyMethod(dataSource.getClass(), property.getGetterMethods());
            if (getterMethod != null) {
                String value = invokeMethod(getterMethod, dataSource);
                if (value != null) {
                    value = decipher.decrypt(property, value);
                    Method setter = ClassUtil.getAnyMethod(dataSource.getClass(), property.getSetterMethods());
                    if (setter != null && value != null) {
                        invokeMethod(setter, dataSource, value);
                    }
                }
            }
        }
    }


    static String invokeMethod(Method method, Object object, Object... params) {
        try {
            return (String) method.invoke(object, params);
        } catch (Exception e) {
            LogFactory.getLog(DataSourceManager.class).error("Can not invoke method: " + method.getName(), e);
        }
        return null;
    }


}
