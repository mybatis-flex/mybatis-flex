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
package com.mybatisflex.kotlin.extensions.model

import com.mybatisflex.core.activerecord.Model
import com.mybatisflex.core.mybatis.Mappers
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.row.Db.*
import com.mybatisflex.core.row.Row
import com.mybatisflex.core.row.RowUtil
import com.mybatisflex.core.table.TableDef
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.core.util.SqlUtil
import com.mybatisflex.kotlin.extensions.db.*
import com.mybatisflex.kotlin.scope.QueryScope
import java.io.Serializable

/*
 * 实体操作扩展
 * @author 卡莫sama(yuanjiashuai)
 */

infix fun <T> Row.to(entryClass: Class<T>): T {
    return RowUtil.toEntity(this, entryClass)
}

inline fun <reified E, T : TableDef> T.filter(
    vararg columns: QueryColumn?,
    init: T.() -> QueryCondition
): List<E> {
    val tableInfo = TableInfoFactory.ofEntityClass(E::class.java)
    return filter<E>(
        columns = columns,
        schema = tableInfo.schema,
        tableName = tableInfo.tableName,
        queryCondition = init()
    )
}

inline fun <reified E> TableDef.query(
    vararg columns: QueryColumn?,
    noinline init: QueryScope.() -> Unit
): List<E> {
    return query<E>(
        columns = columns,
        schema = this.schema,
        tableName = this.tableName,
        init = init
    )
}

inline fun <reified E> TableDef.all(): List<E> = selectAll(schema, tableName).toEntities()

inline fun <reified E> Collection<Row>.toEntities() = map { it to E::class.java }.toList()

inline fun<reified E:Model<E>> List<E>.batchInsert(): Int = Mappers.ofEntityClass(E::class.java).insertBatch(this)

fun< E:Model<E>> List<E>.batchUpdateById(): Boolean = all(Model<E>::updateById)

inline fun<reified E:Model<E>> List<E>. batchDeleteById(): Boolean {
    //拿到集合中所有实体的主键
    val primaryValues = this.map { it.pkValues() }.flatMap(Array<*>::toMutableList).map { it as Serializable }
    return SqlUtil.toBool(Mappers.ofEntityClass(E::class.java).deleteBatchByIds(primaryValues))
}

