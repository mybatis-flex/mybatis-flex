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

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.exception.FlexAssert;
import com.mybatisflex.core.logicdelete.NullableColumnLogicDeleteProcessor;
import com.mybatisflex.core.table.IdInfo;
import com.mybatisflex.core.table.TableInfo;

import java.util.List;

import static com.mybatisflex.core.constant.SqlConsts.EQUALS;

/**
 * 主键逻辑删除处理器。
 *
 * @author 王帅
 * @see <a href="https://gitee.com/mybatis-flex/mybatis-flex/issues/I7O1VV">I7O1VV</a>
 * @since 2023-07-26
 */
public class PrimaryKeyLogicDeleteProcessor extends NullableColumnLogicDeleteProcessor {

    @Override
    public String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo, IDialect dialect) {
        List<IdInfo> primaryKeys = tableInfo.getPrimaryKeyList();
        FlexAssert.notEmpty(primaryKeys, "primaryKeys");
        String column = primaryKeys.get(0).getColumn();
        return dialect.wrap(logicColumn) + EQUALS + dialect.wrap(column);
    }

    /**
     * 逻辑删除后，则更新逻辑删除字段值为主键的值。
     */
    @Override
    public Object getLogicDeletedValue() {
        return null;
    }

}
