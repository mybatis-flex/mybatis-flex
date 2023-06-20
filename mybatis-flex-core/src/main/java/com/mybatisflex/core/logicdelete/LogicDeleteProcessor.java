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
package com.mybatisflex.core.logicdelete;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;

public interface LogicDeleteProcessor {

    /**
     * 用户构建查询正常数据的条件
     * @param logicColumn
     * @param dialect
     */
    String buildLogicNormalCondition(String logicColumn, IDialect dialect);

    /**
     * 用户与构建删除数据时的内容
     * @param logicColumn
     * @param dialect
     */
    String buildLogicDeletedSet(String logicColumn, IDialect dialect);

    /**
     * 用于构建通过 QueryWrapper 查询数据时的内容
     * @param queryWrapper
     * @param tableInfo
     */
    void buildQueryCondition(QueryWrapper queryWrapper, TableInfo tableInfo);
}


