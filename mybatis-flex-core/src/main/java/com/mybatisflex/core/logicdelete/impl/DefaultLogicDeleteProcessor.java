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

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.logicdelete.AbstractLogicDeleteProcessor;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;

import static com.mybatisflex.core.constant.SqlConsts.EQUALS;
import static com.mybatisflex.core.constant.SqlConsts.SINGLE_QUOTE;

/**
 * 默认逻辑删除处理器。
 */
public class DefaultLogicDeleteProcessor extends AbstractLogicDeleteProcessor {

    @Override
    public void buildQueryCondition(QueryWrapper queryWrapper, TableInfo tableInfo) {
        queryWrapper.where(QueryCondition.create(tableInfo.getSchema(), tableInfo.getTableName(), tableInfo.getLogicDeleteColumn()
                , EQUALS, FlexGlobalConfig.getDefaultConfig().getNormalValueOfLogicDelete()));
    }


    @Override
    protected Object getLogicNormalValue() {
        Object normalValueOfLogicDelete = FlexGlobalConfig.getDefaultConfig().getNormalValueOfLogicDelete();
        if (normalValueOfLogicDelete instanceof Number
                || normalValueOfLogicDelete instanceof Boolean) {
            return normalValueOfLogicDelete;
        }
        return SINGLE_QUOTE + normalValueOfLogicDelete + SINGLE_QUOTE;
    }


    @Override
    protected Object getLogicDeletedValue() {
        Object deletedValueOfLogicDelete = FlexGlobalConfig.getDefaultConfig().getDeletedValueOfLogicDelete();
        if (deletedValueOfLogicDelete instanceof Number
                || deletedValueOfLogicDelete instanceof Boolean) {
            return deletedValueOfLogicDelete;
        }
        return SINGLE_QUOTE + deletedValueOfLogicDelete + SINGLE_QUOTE;
    }

}


