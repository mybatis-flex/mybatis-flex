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
package com.mybatisflex.kotlin.extensions.db

import com.mybatisflex.core.mybatis.Mappers
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.row.Db.selectListByQuery
import com.mybatisflex.core.row.Db.selectOneByQuery
import com.mybatisflex.core.row.Row
import com.mybatisflex.core.table.TableDef
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.kotlin.extensions.model.filter
import com.mybatisflex.kotlin.extensions.model.toEntities
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.queryScope


/*
 * 数据库简单操作扩展
 * @author 卡莫sama(yuanjiashuai)
 */

inline fun <reified M > mapper(): M = Mappers.ofMapperClass(M::class.java)

inline fun <reified T : Any> queryOne(
    vararg columns: QueryColumn,
    schema: String? = null,
    tableName: String? = null,
    noinline init: QueryScope.() -> Unit
): T = queryRow(schema = schema, tableName = tableName, columns = columns, init = init).toEntity(T::class.java)


fun queryRow(
    vararg columns: QueryColumn?,
    schema: String? = null,
    tableName: String? = null,
    init: QueryScope.() -> Unit
): Row =
    selectOneByQuery(
        schema,
        tableName,
        queryScope(columns = columns, init = init)
    )


inline fun <reified T> query(
    vararg columns: QueryColumn?,
    schema: String? = null,
    tableName: String? = null,
    noinline init: QueryScope.() -> Unit
): List<T> =
    queryRows(schema = schema, tableName = tableName, columns = columns, init = init)
        .toEntities()

fun queryRows(
    vararg columns: QueryColumn?,
    schema: String? = null,
    tableName: String? = null,
    init: QueryScope.() -> Unit
): List<Row> = selectListByQuery(
    schema,tableName,queryScope(columns = columns, init = init)
)

//    filter-----------
inline fun <reified E> filter(
    tableName: String,
    schema: String,
    vararg columns: QueryColumn?,
    queryCondition: QueryCondition = QueryCondition.createEmpty()
): List<E> = selectListByQuery(
    schema,
    tableName,
    queryScope(*columns).where(queryCondition)
).toEntities()

inline fun <reified E > filter(
    vararg columns: QueryColumn?,
    init: () -> QueryCondition
): List<E> {
    val tableInfo = TableInfoFactory.ofEntityClass(E::class.java)
    return filter<E>(
        columns = columns,
        schema = tableInfo.schema,
        tableName = tableInfo.tableName,
        queryCondition = init()
    )
}

inline fun <reified E, T : TableDef> filter(
    tableDef: T,
    vararg columns: QueryColumn?,
    init: T.() -> QueryCondition
): List<E> = tableDef.filter(columns = columns, init = init)



