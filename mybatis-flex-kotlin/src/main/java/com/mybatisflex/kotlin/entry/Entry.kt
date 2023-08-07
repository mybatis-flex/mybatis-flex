package com.mybatisflex.kotlin.entry

import com.mybatisflex.core.dialect.DialectFactory
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.core.util.ArrayUtil
import com.mybatisflex.kotlin.extend.db.DB
import java.io.Serializable
/**
 * 实体类父类，继承该类后将赋予实体简单增删改操作
 * @author 卡莫sama(yuanjiashuai)
 * @date 2023/8/7
 */
open class Entry :Serializable{

    fun save(ignoreNulls: Boolean = true): Boolean {
        val tableInfo = TableInfoFactory.ofEntityClass(this.javaClass)
        //设置乐观锁版本字段的初始化数据
        tableInfo.initVersionValueIfNecessary(this)
        //设置租户ID
        tableInfo.initTenantIdIfNecessary(this)
        //设置逻辑删除字段的出初始化数据
        tableInfo.initLogicDeleteValueIfNecessary(this)
        //执行 onInsert 监听器
        tableInfo.invokeOnInsertListener(this)
        val values = tableInfo.buildInsertSqlArgs(this, ignoreNulls)
        val sql = DialectFactory.getDialect().forInsertEntity(tableInfo, this, ignoreNulls)
        return DB.insertBySql(sql, *values) == 1
    }

    fun update(ignoreNulls: Boolean = true): Boolean {
        val tableInfo = TableInfoFactory.ofEntityClass(this.javaClass)
        //执行 onUpdate 监听器
        tableInfo.invokeOnUpdateListener(this)
        val updateValues = tableInfo.buildUpdateSqlArgs(this, ignoreNulls, false)
        val primaryValues = tableInfo.buildPkSqlArgs(this)
        val tenantIdArgs = tableInfo.buildTenantIdArgs()
        val sql = DialectFactory.getDialect().forUpdateEntity(tableInfo, this, ignoreNulls)
        return DB.updateBySql(sql, *ArrayUtil.concat(updateValues, primaryValues, tenantIdArgs)) == 1
    }

    fun deleteById(): Boolean {
        val tableInfo = TableInfoFactory.ofEntityClass(this.javaClass)
        val primaryValues = tableInfo.buildPkSqlArgs(this)
        val allValues = ArrayUtil.concat(primaryValues, tableInfo.buildTenantIdArgs())
        val sql = DialectFactory.getDialect().forDeleteEntityById(tableInfo)
        return DB.deleteBySql(sql, *allValues) == 1
    }

}
