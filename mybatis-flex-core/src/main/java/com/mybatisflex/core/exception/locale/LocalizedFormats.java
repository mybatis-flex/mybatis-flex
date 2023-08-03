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

package com.mybatisflex.core.exception.locale;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 异常消息中使用的本地化消息格式的枚举。
 *
 * @author 王帅
 * @author michael
 *
 * @since 2023-07-26
 */
public enum LocalizedFormats implements Localizable {

    /**
     * object can not be null
     */
    OBJECT_NULL("{0} can not be null."),
    OBJECT_NULL_OR_BLANK("{0} can not be null or blank."),
    MAP_NULL_OR_EMPTY("{0} can not be null or empty."),
    ARRAY_NULL_OR_EMPTY("{0} array can not be null or empty."),


    DATASOURCE_TYPE_BLANK("The dataSource type can not be null or blank."),
    DATASOURCE_TYPE_NOT_FIND("Can not find the dataSource type: {0}"),
    DATASOURCE_CAN_NOT_INSTANCE("Can not new instance dataSource object by class: {0}"),
    DATASOURCE_JDBC_URL("Can not get the dataSource jdbcUrl."),


    UPDATE_ONLY_SUPPORT_1_TABLE("\"UpdateByQuery\" only support 1 table."),
    UPDATE_OR_DELETE_NOT_ALLOW("Not allowed \"UPDATE\" or \"DELETE\" a table without where condition."),


    ENTITY_VERSION_NULL("The version value of entity \"{0}\" must not be null."),

    KEY_GENERATOR_BLANK("The name of key generator must not be null or blank."),
    ;

    private final String sourceFormat;

    LocalizedFormats(String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

    @Override
    public String getSourceString() {
        return this.sourceFormat;
    }

    @Override
    public String getLocalizedString(final Locale locale) {
        try {
            // 本地化消息路径
            String path = LocalizedFormats.class.getName().replace(".", "/");
            // 获取多语言本地化消息信息文件
            ResourceBundle bundle = ResourceBundle.getBundle("assets/" + path, locale);
            // 获取当前语言的消息格式
            if (bundle.getLocale().getLanguage().equals(locale.getLanguage())) {
                return bundle.getString(name());
            }
        } catch (MissingResourceException mre) {
            // do nothing here.
        }
        // 如果没有该语言的本地化消息，则返回源消息字符串
        return sourceFormat;
    }

}
