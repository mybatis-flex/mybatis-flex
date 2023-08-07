package com.mybatisflex.kotlin.extend.mapper

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.queryScope
/*
 * 映射器操作扩展
 * @author 卡莫sama(yuanjiashuai)
 * @date 2023/8/7
 */
fun <T> BaseMapper<*>.queryList(init: (QueryScope.() -> Unit)? = null): List<T> =
    this.selectListByQuery(queryScope(init = init)) as List<T>

fun <T> BaseMapper<T>.update(entity: T, init: () -> QueryCondition): Int =
    this.updateByCondition(entity, init())

fun <T> BaseMapper<T>.delete(init: (QueryScope.() -> Unit)? = null): Int =
    this.deleteByQuery(queryScope(init = init))

fun <T> BaseMapper<T>.delete1(init: () -> QueryCondition): Int =
    this.deleteByCondition(init())
