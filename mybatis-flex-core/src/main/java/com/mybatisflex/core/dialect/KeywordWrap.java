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
package com.mybatisflex.core.dialect;

import com.mybatisflex.core.util.StringUtil;

/**
 * 用于对数据库的关键字包装
 */
public class KeywordWrap {

    /**
     * 无反义处理, 适用于 db2, informix, clickhouse 等
     */
    public final static KeywordWrap NONE = new KeywordWrap("", "") {
        @Override
        public String wrap(String keyword) {
            return keyword;
        }
    };

    /**
     * 反引号反义处理, 适用于 mysql, h2 等
     */
    public final static KeywordWrap BACKQUOTE = new KeywordWrap("`", "`");

    /**
     * 双引号反义处理, 适用于 postgresql, sqlite, derby, oracle 等
     */
    public final static KeywordWrap DOUBLE_QUOTATION = new KeywordWrap("\"", "\"");

    /**
     * 方括号反义处理, 适用于 sqlserver
     */
    public final static KeywordWrap SQUARE_BRACKETS = new KeywordWrap("[", "]");

    /**
     * 前缀
     */
    private final String prefix;

    /**
     * 后缀
     */
    private final String suffix;


    public KeywordWrap(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String wrap(String keyword) {
        return StringUtil.isBlank(keyword) ? "" : prefix + keyword + suffix;
    }

}
