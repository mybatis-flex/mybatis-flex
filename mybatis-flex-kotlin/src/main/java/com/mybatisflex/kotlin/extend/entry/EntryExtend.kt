package com.mybatisflex.kotlin.extend.entry

import com.mybatisflex.core.FlexConsts
import com.mybatisflex.core.dialect.DialectFactory
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.row.Row
import com.mybatisflex.core.row.RowUtil
import com.mybatisflex.core.table.TableDef
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.core.util.ArrayUtil
import com.mybatisflex.kotlin.entry.Entry
import com.mybatisflex.kotlin.extend.db.DB
import com.mybatisflex.kotlin.scope.QueryScope
import java.util.Arrays

/*
 * 实体操作扩展
 * @author 卡莫sama(yuanjiashuai)
 * @date 2023/8/7
 */

infix fun <T> Row.to(entryClass: Class<T>): T {
    return RowUtil.toEntity(this, entryClass)
}

inline fun <reified E, T : TableDef> T.filter(
    vararg columns: QueryColumn?,
    init: T.() -> QueryCondition
): List<E> {
    val tableInfo = TableInfoFactory.ofEntityClass(E::class.java)
    return DB.filter<E>(
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
    return DB.query<E>(
        columns = columns,
        schema = this.schema,
        tableName = this.tableName,
        init = init
    )
}

inline fun <reified E> TableDef.all(
    vararg columns: QueryColumn?
): List<E> = DB.selectAll(schema, tableName).toEntities()

inline fun <reified E> Collection<Row>.toEntities() = map { it to E::class.java }.toList()

inline fun<reified E:Entry> List<E>.batchInsert(): Boolean {
    val entities = this
    val tableInfo = TableInfoFactory.ofEntityClass(E::class.java)
    for (entity in entities) {
        tableInfo.initVersionValueIfNecessary(entity)
        tableInfo.initTenantIdIfNecessary(entity)
        tableInfo.initLogicDeleteValueIfNecessary(entity)
        //执行 onInsert 监听器
        tableInfo.invokeOnInsertListener(entity)
    }
    var allValues = FlexConsts.EMPTY_ARRAY
    for (entity in entities) {
        allValues = ArrayUtil.concat(allValues, tableInfo.buildInsertSqlArgs(entity, false))
    }
    val sql = DialectFactory.getDialect().forInsertEntityBatch(tableInfo, entities)
    return DB.insertBySql(sql,*allValues) > 1
}


fun< E:Entry> List<E>.batchUpdate(): Boolean = all(Entry::update)

inline fun<reified E:Entry> List<E>. batchDeleteById(): Boolean {
    val tableInfo = TableInfoFactory.ofEntityClass(E::class.java)
    val primaryValues = this.map { tableInfo.buildPkSqlArgs(it) }.stream().flatMap(Arrays::stream).toArray()
    val tenantIdArgs = tableInfo.buildTenantIdArgs()
    val sql =  DialectFactory.getDialect().forDeleteEntityBatchByIds(tableInfo, primaryValues)
    return DB.deleteBySql(sql,*ArrayUtil.concat(primaryValues, tenantIdArgs)) > 1
}
