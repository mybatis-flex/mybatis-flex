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
 * @since 2023-07-26
 */
public enum LocalizedFormats implements Localizable {

    OBJECT_NULL("{0} can not be null.");

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
                return bundle.getString(toString());
            }
        } catch (MissingResourceException mre) {
            mre.printStackTrace();
            // do nothing here.
        }
        // 如果没有该语言的本地化消息，则返回源消息字符串
        return sourceFormat;
    }

}
