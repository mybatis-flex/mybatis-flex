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

import java.io.Serializable;
import java.util.Locale;

/**
 * 可本地化字符串接口。
 *
 * @author 王帅
 * @since 2023-07-26
 */
public interface Localizable extends Serializable {

    /**
     * 获取源（非本地化）字符串。
     *
     * @return 源字符串
     */
    String getSourceString();

    /**
     * 获取本地化字符串。
     *
     * @param locale 要获取字符串的区域
     * @return 本地化字符串或源字符串（如果没有可用的本地化版本）
     */
    String getLocalizedString(Locale locale);

}
