package com.mybatisflex.kotlin.scope

import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.core.table.TableDef
/**
 * 查询作用域
 * @author 卡莫sama(yuanjiashuai)
 * @date 2023/8/7
 */
class QueryScope :QueryWrapper() {
    companion object CurrentQueryScope : ThreadLocal<QueryScope>()

    fun from(init: (QueryScope.() -> Unit)? = null): QueryWrapper = this.from(queryScope(init = init))

    fun <T : TableDef> where(tableDef: T, build: T.() -> QueryCondition): QueryWrapper = this.where(build(tableDef))

    fun where(build: QueryScope.() -> QueryCondition): QueryWrapper = this.where(build(this))

    operator fun String.get(name: String): QueryColumn = QueryColumn(this, name)

    operator fun String.unaryMinus(): QueryColumn = QueryColumn(this)

}


fun queryScope(vararg columns: QueryColumn?, init: (QueryScope.() -> Unit)? = null): QueryWrapper {
    val builder = QueryScope()

    if (columns.isNotEmpty()) {
        builder.select(columns)
    }
    //用于嵌套查询拿到上层查询包装对象
    init?.also {
        val prentQueryScope = QueryScope.get()
        QueryScope.set(builder)
        it(builder)
        QueryScope.set(prentQueryScope)
    }

    return builder
}


