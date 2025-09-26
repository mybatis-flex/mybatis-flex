/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.logicdelete.AbstractLogicDeleteProcessor;
import com.mybatisflex.core.table.TableInfo;

import static com.mybatisflex.core.constant.SqlConsts.EQUALS;
import static com.mybatisflex.core.constant.SqlConsts.SINGLE_QUOTE;

/**
 * 默认逻辑删除处理器。
 * @author michael
 */
public class DefaultLogicDeleteProcessor extends AbstractLogicDeleteProcessor {

    @Override
    public String buildLogicNormalCondition(String logicColumn, TableInfo tableInfo, IDialect dialect) {
        return dialect.wrap(logicColumn) + EQUALS + prepareValue(getLogicNormalValue());
    }

    @Override
    public String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo, IDialect dialect) {
        String sql = dialect.wrap(logicColumn) + EQUALS + prepareValue(getLogicDeletedValue());
        return invokeOnLogicDeleteListener(sql, tableInfo, dialect);
    }

    @Override
    public Object getLogicNormalValue() {
        return FlexGlobalConfig.getDefaultConfig().getNormalValueOfLogicDelete();
    }

    @Override
    public Object getLogicDeletedValue() {
        return FlexGlobalConfig.getDefaultConfig().getDeletedValueOfLogicDelete();
    }

}


