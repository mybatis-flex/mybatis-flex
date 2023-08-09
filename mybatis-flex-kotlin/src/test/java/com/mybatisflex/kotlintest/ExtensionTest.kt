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
package com.mybatisflex.kotlintest

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.FlexConsts
import com.mybatisflex.core.audit.AuditManager
import com.mybatisflex.core.audit.ConsoleMessageCollector
import com.mybatisflex.kotlin.entry.Entry
import com.mybatisflex.kotlin.extensions.db.*
import com.mybatisflex.kotlin.extensions.entry.*
import com.mybatisflex.kotlin.extensions.mapper.queryList
import com.mybatisflex.kotlin.extensions.sql.*
import com.mybatisflex.kotlin.scope.buildBootstrap
import com.mybatisflex.kotlintest.entry.Account
import com.mybatisflex.kotlintest.entry.table.AccountTableDef.ACCOUNT
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource
import kotlin.streams.toList


fun main() {
    val dataSource: DataSource = EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .addScript("schema.sql")
        .addScript("data.sql")
        .build()

    AuditManager.setAuditEnable(true)
    AuditManager.setMessageCollector(ConsoleMessageCollector())

    buildBootstrap {
        + AccountMapper::class.java
        dataSources {
//            dataSource(FlexConsts.NAME,dataSource)
            FlexConsts.NAME of dataSource
//            "dataSource1" of dataSource
//            "dataSource2" of dataSource
        }
//        + dataSource
    }.start()

    filter<Account> {
        ACCOUNT.AGE `=` 12 or
            `if`(true) { ACCOUNT.ID `in` listOf(1, 2) }
    }.stream().peek(::println).peek { it.id = it.id.plus(2) }.forEach(Entry::save)
    //使用表对象filter或者DB对象有两个泛型的filter方法时方法体内this为表对象无需XXX.AA调用，直接AA
//    ACCOUNT.filter<Account,AccountTableDef> {
//        AGE `=` 12 or
//            `if`(true) { ID `in` listOf(1, 2) }
//    }.stream().peek(::println).peek { it.id = it.id.plus(6) }.forEach(Entry::save)

    println("保存后————————")
    mapper<AccountMapper>().findByAge(18,1,2).stream().peek { println(it) }.forEach(Entry::deleteById)

    println("删除后————————")
    ACCOUNT.all<Account>().stream().peek { println(it) }.map { it.userName = "sa"
        it }.forEach(Entry::update)
    println("更新后————————")

    ACCOUNT.all<Account>().stream().peek { println(it) }.map {
        it.id = it.id.plus(5)
        it.userName = "423423"
        it }.toList().batchInsert()

    println("批量插入后————————")
    ACCOUNT.all<Account>().stream().peek { println(it) }.toList().filter { it.id.rem(2) == 0 }.batchDeleteById()

    println("批量删除后————————")
    //直接使用函数查询时需指定from表
    query<Account> {from(ACCOUNT)}.stream().peek { println(it) }.toList().filter { it.id.rem(3) == 0 }.map { it.userName = "哈哈"
        it }.batchUpdate()

    println("批量更新后————————")
    //使用表对象查询时无需指定from表
    ACCOUNT.query<Account> {}.forEach(::println)
}



//接口里面写方法时打这个注解才能生成Default方法否则会单独生成一个类导致报错
@JvmDefaultWithCompatibility
interface AccountMapper : BaseMapper<Account> {

    fun findByAge(age: Int, vararg ids: Int): List<Account> = queryList {
        select(ACCOUNT.ALL_COLUMNS)
        from(ACCOUNT)
        where(ACCOUNT) {
            (AGE `=` age) and  `if`(true) {
                ID `in` ids.asList()
            }
        }
    }

}

