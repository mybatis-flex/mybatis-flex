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

package com.mybatisflex.core.logicdelete.impl;

import com.mybatisflex.core.logicdelete.AbstractLogicDeleteProcessor;

/**
 * 时间戳类型的属性对应的逻辑删除处理器。
 *
 * @author 王帅
 * @since 2023-06-21
 */
public class TimeStampLogicDeleteProcessor extends AbstractLogicDeleteProcessor {

    /**
     * 逻辑删除字段值为 {@code 0} 表示数据未删除。
     */
    @Override
    public Object getLogicNormalValue() {
        return 0;
    }

    /**
     * 逻辑删除字段值为 {@code NOW()} 表示数据删除，并记录删除时时间戳。
     */
    @Override
    public Object getLogicDeletedValue() {
        return System.currentTimeMillis();
    }

}
