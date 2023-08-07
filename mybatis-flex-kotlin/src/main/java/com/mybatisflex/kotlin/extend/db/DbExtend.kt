package com.mybatisflex.kotlin.extend.db

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.MybatisFlexBootstrap
import com.mybatisflex.core.paginate.Page
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.core.row.*
import com.mybatisflex.core.table.TableDef
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.core.transaction.Propagation
import com.mybatisflex.kotlin.extend.entry.filter
import com.mybatisflex.kotlin.extend.entry.toEntities
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.queryScope
import java.util.function.BiConsumer
import java.util.function.Supplier

/**
 * 数据库操作对象
 * @author 卡莫sama(yuanjiashuai)
 * @date 2023/8/7
 */
object DB {

    fun invoker(): RowMapperInvoker = Db.invoker()

    fun invoker(environmentId: String): RowMapperInvoker = Db.invoker(environmentId)

    inline fun <reified M : BaseMapper<*>> mapper(): M = MybatisFlexBootstrap.getInstance().getMapper(M::class.java)

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
            schema = schema,
            tableName = tableName,
            queryWrapper = queryScope(columns = columns, init = init)
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
        schema = schema, tableName = tableName,
        queryWrapper = queryScope(columns = columns, init = init)
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

    inline fun <reified E> filter(
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


    //    ----------------------
    fun insert(schema: String?, tableName: String?, row: Row?): Int {
        return Db.insert(schema, tableName, row)
    }

    fun insert(tableName: String?, row: Row?): Int {
        return Db.insert(null as String?, tableName, row)
    }

    fun insertBySql(sql: String?, vararg args: Any?): Int {
        return Db.insertBySql(sql, *args)
    }

    fun insertBatch(schema: String?, tableName: String?, rows: Collection<Row?>): IntArray {
        return insertBatch(schema, tableName, rows, rows.size)
    }

    fun insertBatch(tableName: String?, rows: Collection<Row?>): IntArray {
        return insertBatch(null as String?, tableName, rows, rows.size)
    }

    fun insertBatch(schema: String?, tableName: String?, rows: Collection<Row?>, batchSize: Int): IntArray {
        return Db.insertBatch(schema, tableName, rows, batchSize)
    }

    fun insertBatch(tableName: String?, rows: Collection<Row>, batchSize: Int): IntArray {
        return Db.insertBatch(tableName, rows)
    }

    fun insertBatchWithFirstRowColumns(schema: String?, tableName: String?, rows: List<Row?>?): Int {
        return Db.insertBatchWithFirstRowColumns(schema, tableName, rows)
    }

    fun insertBatchWithFirstRowColumns(tableName: String?, rows: List<Row?>?): Int {
        return Db.insertBatchWithFirstRowColumns(null as String?, tableName, rows)
    }

    fun deleteBySql(sql: String?, vararg args: Any?): Int {
        return Db.deleteBySql(sql, *args)
    }

    fun deleteById(schema: String?, tableName: String?, row: Row?): Int {
        return Db.deleteById(schema, tableName, row)
    }

    fun deleteById(tableName: String?, row: Row?): Int {
        return Db.deleteById(null as String?, tableName, row)
    }

    fun deleteById(schema: String?, tableName: String?, primaryKey: String?, id: Any?): Int {
        return Db.deleteById(schema, tableName, primaryKey, id)
    }

    fun deleteById(tableName: String?, primaryKey: String?, id: Any?): Int {
        return Db.deleteById(null as String?, tableName, primaryKey, id)
    }

    fun deleteBatchByIds(schema: String?, tableName: String?, primaryKey: String?, ids: Collection<*>?): Int {
        return Db.deleteBatchByIds(schema, tableName, primaryKey, ids)
    }

    fun deleteBatchByIds(tableName: String?, primaryKey: String?, ids: Collection<*>?): Int {
        return Db.deleteBatchByIds(null as String?, tableName, primaryKey, ids)
    }

    fun deleteByMap(schema: String?, tableName: String?, whereColumns: Map<String?, Any?>?): Int {
        return Db.deleteByQuery(
            schema, tableName, QueryWrapper()
                .where(whereColumns)
        )
    }

    fun deleteByMap(tableName: String?, whereColumns: Map<String?, Any?>?): Int {
        return Db.deleteByQuery(
            null as String?, tableName, QueryWrapper()
                .where(whereColumns)
        )
    }

    fun deleteByCondition(schema: String?, tableName: String?, condition: QueryCondition?): Int {
        return Db.deleteByQuery(
            schema, tableName, QueryWrapper()
                .where(condition)
        )
    }

    fun deleteByCondition(tableName: String?, condition: QueryCondition?): Int {
        return Db.deleteByQuery(
            null as String?, tableName, QueryWrapper()
                .where(condition)
        )
    }

    fun deleteByQuery(schema: String?, tableName: String?, queryWrapper: QueryWrapper?): Int {
        return Db.deleteByQuery(schema, tableName, queryWrapper)
    }

    fun deleteByQuery(tableName: String?, queryWrapper: QueryWrapper?): Int {
        return Db.deleteByQuery(null as String?, tableName, queryWrapper)
    }

    fun updateBySql(sql: String?, vararg args: Any?): Int {
        return Db.updateBySql(sql, *args)
    }

    fun updateBatch(sql: String?, batchArgsSetter: BatchArgsSetter): IntArray {
        return Db.updateBatch(sql, batchArgsSetter)
    }


    fun updateById(schema: String?, tableName: String?, row: Row?): Int {
        return Db.updateById(schema, tableName, row)
    }

    fun updateById(tableName: String?, row: Row?): Int {
        return Db.updateById(null as String?, tableName, row)
    }

    fun updateByMap(schema: String?, tableName: String?, data: Row?, whereColumns: Map<String?, Any?>?): Int {
        return Db.updateByQuery(
            schema, tableName, data, QueryWrapper()
                .where(whereColumns)
        )
    }

    fun updateByMap(tableName: String?, data: Row?, whereColumns: Map<String?, Any?>?): Int {
        return Db.updateByQuery(
            null as String?, tableName, data, QueryWrapper()
                .where(whereColumns)
        )
    }

    fun updateByCondition(schema: String?, tableName: String?, data: Row?, condition: QueryCondition?): Int {
        return Db.updateByQuery(
            schema, tableName, data, QueryWrapper()
                .where(condition)
        )
    }

    fun updateByCondition(tableName: String?, data: Row?, condition: QueryCondition?): Int {
        return Db.updateByQuery(
            null as String?, tableName, data, QueryWrapper()
                .where(condition)
        )
    }

    fun updateByQuery(schema: String?, tableName: String?, data: Row?, queryWrapper: QueryWrapper?): Int {
        return Db.updateByQuery(schema, tableName, data, queryWrapper)
    }

    fun updateByQuery(tableName: String?, data: Row?, queryWrapper: QueryWrapper?): Int {
        return Db.updateByQuery(null as String?, tableName, data, queryWrapper)
    }

    fun updateBatchById(schema: String?, tableName: String?, rows: List<Row?>?): Int {
        return Db.updateBatchById(schema, tableName, rows)
    }

    fun updateBatchById(tableName: String?, rows: List<Row?>?): Int {
        return Db.updateBatchById(null as String?, tableName, rows)
    }

    fun <T> updateEntitiesBatch(entities: Collection<T>?, batchSize: Int): Int {
        return Db.updateEntitiesBatch(entities, batchSize)
    }

    fun <T> updateEntitiesBatch(entities: Collection<T>?): Int {
        return updateEntitiesBatch(entities, 1000)
    }

    fun updateNumberAddByQuery(
        schema: String?,
        tableName: String?,
        fieldName: String?,
        value: Number?,
        queryWrapper: QueryWrapper?
    ): Int {
        return Db.updateNumberAddByQuery(schema, tableName, fieldName, value, queryWrapper)
    }

    fun updateNumberAddByQuery(
        tableName: String?,
        fieldName: String?,
        value: Number?,
        queryWrapper: QueryWrapper?
    ): Int {
        return Db.updateNumberAddByQuery(null as String?, tableName, fieldName, value, queryWrapper)
    }

    fun <M> executeBatch(
        totalSize: Int,
        batchSize: Int,
        mapperClass: Class<M>?,
        consumer: BiConsumer<M, Int?>?
    ): IntArray {
        return Db.executeBatch(totalSize, batchSize, mapperClass, consumer)
    }

    fun selectOneBySql(sql: String?, vararg args: Any?): Row {
        return Db.selectOneBySql(sql, *args)
    }


    fun selectOneById(schema: String?, tableName: String?, row: Row?): Row {
        return Db.selectOneById(schema, tableName, row)
    }

    fun selectOneById(tableName: String?, row: Row?): Row {
        return Db.selectOneById(null as String?, tableName, row)
    }

    fun selectOneById(schema: String?, tableName: String?, primaryKey: String?, id: Any?): Row {
        return Db.selectOneById(schema, tableName, primaryKey, id)
    }

    fun selectOneById(tableName: String?, primaryKey: String?, id: Any?): Row {
        return Db.selectOneById(null as String?, tableName, primaryKey, id)
    }

    fun selectOneByMap(schema: String?, tableName: String?, whereColumns: Map<String, Any>?): Row {
        return Db.selectOneByQuery(
            schema, tableName, QueryWrapper()
                .where(whereColumns).limit(1)
        )
    }

    fun selectOneByMap(tableName: String?, whereColumns: Map<String, Any>?): Row {
        return Db.selectOneByQuery(
            null as String?, tableName, QueryWrapper().where(whereColumns).limit(1)
        )
    }

    fun selectOneByCondition(schema: String?, tableName: String?, condition: QueryCondition?): Row {
        return Db.selectOneByQuery(
            schema, tableName, QueryWrapper()
                .where(condition).limit(1)
        )
    }

    fun selectOneByCondition(tableName: String?, condition: QueryCondition?): Row {
        return Db.selectOneByQuery(
            null as String?, tableName, QueryWrapper()
                .where(condition).limit(1)
        )
    }

    fun selectOneByQuery(schema: String?, tableName: String?, queryWrapper: QueryWrapper?): Row {
        return Db.selectOneByQuery(schema, tableName, queryWrapper)
    }

    fun selectOneByQuery(tableName: String?, queryWrapper: QueryWrapper?): Row {
        return Db.selectOneByQuery(null as String?, tableName, queryWrapper)
    }

    fun selectOneByQuery(queryWrapper: QueryWrapper?): Row {
        return Db.selectOneByQuery(queryWrapper)
    }

    fun selectListBySql(sql: String?, vararg args: Any?): List<Row> {
        return Db.selectListBySql(sql, *args)
    }

    fun selectListByMap(schema: String?, tableName: String?, whereColumns: Map<String?, Any?>?): List<Row> {
        return Db.selectListByQuery(
            schema, tableName, QueryWrapper()
                .where(whereColumns)
        )
    }

    fun selectListByMap(tableName: String?, whereColumns: Map<String?, Any?>?): List<Row> {
        return Db.selectListByMap(tableName, whereColumns)
    }

    fun selectListByMap(schema: String?, tableName: String?, whereColumns: Map<String?, Any?>?, count: Int): List<Row> {
        return Db.selectListByMap(schema, tableName, whereColumns, count)
    }

    fun selectListByMap(tableName: String?, whereColumns: Map<String?, Any?>?, count: Int): List<Row> {
        return Db.selectListByMap(tableName, whereColumns, count)
    }

    fun selectListByCondition(schema: String?, tableName: String?, condition: QueryCondition?): List<Row> {
        return Db.selectListByCondition(schema, tableName, condition)
    }

    fun selectListByCondition(tableName: String?, condition: QueryCondition?): List<Row> {
        return Db.selectListByQuery(
            null as String?, tableName, QueryWrapper()
                .where(condition)
        )
    }

    fun selectListByCondition(schema: String?, tableName: String?, condition: QueryCondition?, count: Int): List<Row> {
        return Db.selectListByQuery(
            schema, tableName, QueryWrapper()
                .where(condition).limit(count)
        )
    }

    fun selectListByCondition(tableName: String?, condition: QueryCondition?, count: Int): List<Row> {
        return Db.selectListByQuery(
            null as String?, tableName, QueryWrapper()
                .where(condition).limit(count)
        )
    }

    fun selectListByQuery(schema: String?, tableName: String?, queryWrapper: QueryWrapper?): List<Row> {
        return Db.selectListByQuery(schema, tableName, queryWrapper)
    }

    fun selectListByQuery(tableName: String?, queryWrapper: QueryWrapper?): List<Row> {
        return Db.selectListByQuery(null as String?, tableName, queryWrapper)
    }

    fun selectListByQuery(queryWrapper: QueryWrapper?): List<Row> {
        return Db.selectListByQuery(queryWrapper)
    }

    fun selectAll(schema: String?, tableName: String?): List<Row> {
        return Db.selectAll(schema, tableName)
    }

    fun selectAll(tableName: String?): List<Row> {
        return Db.selectAll(null as String?, tableName)
    }

    fun selectObject(sql: String?, vararg args: Any?): Any {
        return Db.selectObject(sql, *args)
    }

    fun selectObject(schema: String?, tableName: String?, queryWrapper: QueryWrapper?): Any {
        return Db.selectObject(schema, tableName, queryWrapper)
    }

    fun selectObject(tableName: String?, queryWrapper: QueryWrapper?): Any {
        return Db.selectObject(null as String?, tableName, queryWrapper)
    }

    fun selectObject(queryWrapper: QueryWrapper?): Any {
        return Db.selectObject(queryWrapper)
    }

    fun selectObjectList(sql: String?, vararg args: Any?): List<Any> {
        return Db.selectObjectList(sql, *args)
    }

    fun selectObjectList(schema: String?, tableName: String?, queryWrapper: QueryWrapper?): Any {
        return Db.selectObjectList(schema, tableName, queryWrapper)
    }

    fun selectObjectList(tableName: String?, queryWrapper: QueryWrapper?): Any {
        return Db.selectObjectList(tableName, queryWrapper)
    }

    fun selectObjectList(queryWrapper: QueryWrapper?): Any {
        return Db.selectObjectList(queryWrapper)
    }

    fun selectCount(sql: String?, vararg args: Any?): Long {
        return Db.selectCount(sql, *args)
    }

    fun selectCountByCondition(schema: String?, tableName: String?, condition: QueryCondition?): Long {
        return Db.selectCountByQuery(
            schema, tableName, QueryWrapper()
                .where(condition)
        )
    }

    fun selectCountByCondition(tableName: String?, condition: QueryCondition?): Long {
        return Db.selectCountByCondition(
            tableName, condition
        )
    }

    fun selectCountByQuery(schema: String?, tableName: String?, queryWrapper: QueryWrapper?): Long {
        return Db.selectCountByQuery(schema, tableName, queryWrapper)
    }

    fun selectCountByQuery(tableName: String?, queryWrapper: QueryWrapper?): Long {
        return Db.selectCountByQuery(null as String?, tableName, queryWrapper)
    }

    fun selectCountByQuery(queryWrapper: QueryWrapper?): Long = Db.selectCountByQuery(queryWrapper)


    fun paginate(
        schema: String?,
        tableName: String?,
        pageNumber: Int,
        pageSize: Int,
        condition: QueryCondition?
    ): Page<Row> = Db.paginate(schema, tableName, Page(pageNumber, pageSize), QueryWrapper.create().where(condition))


    fun paginate(tableName: String?, pageNumber: Int, pageSize: Int, condition: QueryCondition?): Page<Row> =
        Db.paginate(tableName, pageNumber, pageSize, condition)


    fun paginate(
        schema: String?,
        tableName: String?,
        pageNumber: Int,
        pageSize: Int,
        totalRow: Int,
        condition: QueryCondition?
    ): Page<Row> {
        return Db.paginate(
            schema, tableName, pageNumber, pageSize, totalRow, condition
        )
    }

    fun paginate(
        tableName: String?,
        pageNumber: Int,
        pageSize: Int,
        totalRow: Int,
        condition: QueryCondition?
    ): Page<Row> = Db.paginate(tableName, pageNumber, pageSize, totalRow, condition)

    fun paginate(
        schema: String?,
        tableName: String?,
        pageNumber: Int,
        pageSize: Int,
        queryWrapper: QueryWrapper?
    ): Page<Row> = Db.paginate(schema, tableName, pageNumber, pageSize, queryWrapper)

    fun paginate(tableName: String?, pageNumber: Int, pageSize: Int, queryWrapper: QueryWrapper?): Page<Row> =
        Db.paginate(tableName, pageNumber, pageSize, queryWrapper)

    fun paginate(
        schema: String?,
        tableName: String?,
        pageNumber: Int,
        pageSize: Int,
        totalRow: Int,
        queryWrapper: QueryWrapper?
    ): Page<Row> {
        return Db
            .paginate(schema, tableName, pageNumber, pageSize, totalRow, queryWrapper)
    }

    fun paginate(
        tableName: String?,
        pageNumber: Int,
        pageSize: Int,
        totalRow: Int,
        queryWrapper: QueryWrapper?
    ): Page<Row> {
        return Db
            .paginate(tableName, pageNumber, pageSize, totalRow, queryWrapper)
    }

    fun paginate(schema: String?, tableName: String?, page: Page<Row?>?, queryWrapper: QueryWrapper?): Page<Row> {
        return Db.paginate(schema, tableName, page, queryWrapper)
    }

    fun paginate(tableName: String?, page: Page<Row?>?, queryWrapper: QueryWrapper?): Page<Row> {
        return Db.paginate(tableName, page, queryWrapper)
    }

    fun tx(supplier: Supplier<Boolean?>?): Boolean {
        return tx(supplier, Propagation.REQUIRED)
    }

    fun tx(supplier: Supplier<Boolean?>?, propagation: Propagation?): Boolean {
        return Db.tx(supplier, propagation)
    }

    fun <T> txWithResult(supplier: Supplier<T>?): T {
        return txWithResult(supplier, Propagation.REQUIRED)
    }

    fun <T> txWithResult(supplier: Supplier<T>?, propagation: Propagation?): T {
        return Db.txWithResult(supplier, propagation)
    }


}




