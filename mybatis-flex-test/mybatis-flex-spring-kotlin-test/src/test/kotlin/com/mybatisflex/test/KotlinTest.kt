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
package com.mybatisflex.test

import com.mybatisflex.core.FlexConsts
import com.mybatisflex.core.activerecord.Model
import com.mybatisflex.core.audit.AuditManager
import com.mybatisflex.core.audit.ConsoleMessageCollector
import com.mybatisflex.kotlin.extensions.db.filter
import com.mybatisflex.kotlin.extensions.db.mapper
import com.mybatisflex.kotlin.extensions.db.query
import com.mybatisflex.kotlin.extensions.model.*
import com.mybatisflex.kotlin.extensions.sql.*
import com.mybatisflex.kotlin.scope.buildBootstrap
import com.mybatisflex.test.mapper.AccountMapper
import com.mybatisflex.test.model.Account
import com.mybatisflex.test.model.table.AccountTableDef.ACCOUNT
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource
import kotlin.streams.toList
import kotlin.test.Test


class KotlinTest {
    @Test
    fun testDb() {
        val dataSource: DataSource = EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("data-kt.sql")
            .build()

        AuditManager.setAuditEnable(true)
        AuditManager.setMessageCollector(ConsoleMessageCollector())

        buildBootstrap {
//            此方法体 it 是 MybatisFlexBootstrap 实例
//            配置Mapper
//            1.通过+（重写自增）的方式
            +AccountMapper::class.java
//            2.通过原始的方式
//            it.addMapper(AccountMapper::class.java)

//            配置单dataSource
//            1.通过+（重写自增）的方式
            +dataSource
//            2.通过原始的方式
//            it.setDataSource(dataSource)

//            配置多dataSource
//            1.通过of（中缀）的方式
            FlexConsts.NAME of dataSource
//            "dataSource1" of dataSource
//            "dataSource2" of dataSource
//            2.通过dsl（中缀）的方式
            dataSources {
//            dataSource(FlexConsts.NAME,dataSource)
//            dataSource("dataSource1",dataSource)
//            dataSource("dataSource2",dataSource)
            }
//            3.通过原始的方式
//            it.addDataSource(FlexConsts.NAME,dataSource)
        }.start()

//        查询表对象对应的所有实体数据
        ACCOUNT.all<Account>().forEach(::println)
//        ACCOUNT.query<Account> {}.forEach(::println)

//        a and (b or c)
//      filter:
        filter<Account> {
            ACCOUNT.ID `=` 1 and
                    (ACCOUNT.AGE `in` (17..19) or (ACCOUNT.BIRTHDAY between ("2020-01-10" to "2020-01-12")) )
        }.forEach(::println)

//       query:
        query <Account> {
            from(Account)
            where(Account) {
                (AGE `in` (17..19) or (BIRTHDAY between ("2020-01-10" .. "2020-01-12")) )
            } orderBy -Account.ID
        }.forEach(::println)

//        查询表对象对应的实体数据并根据条件过滤
        filter<Account> {
            ACCOUNT.AGE `=` 12 or
                    //if的第一个参数为true时则会调用花括号类的方法返回一个条件对象与上面那个条件对象相连接
                    `if`(true) { ACCOUNT.ID between (1 to 2) }
//                  `if`(false) { ACCOUNT.ID `in` listOf(1, 2) }
        }.stream().peek(::println)
//            过滤后修改id再次保存
            .peek { it.id = it.id.plus(2) }.forEach(Model<*>::save)
//      使用表对象filter或者DB对象有两个泛型的filter方法时方法体内this为表对象无需XXX.AA调用，直接AA
//         ACCOUNT.filter<Account,AccountTableDef> {
//           AGE `=` 12 or
//                `if`(true) { ID `in` listOf(1, 2) }
//       }.stream().peek(::println).peek { it.id = it.id.plus(6) }.forEach(Entry::save)

        println("保存后————————")
//      获得mapper实例通过自定义的默认方法查，并将查到的删除
        mapper<AccountMapper>().findByAge(18, 1, 2).stream().peek { println(it) }.forEach{it.removeById()}

        println("删除后————————")
        Account.all<Account>().stream().peek { println(it) }.map {
            it.userName = "kamo"
            it
        }.forEach{it.updateById()}
        println("更新后————————")

        ACCOUNT.all<Account>().stream().peek { println(it) }.map {
            it.id = it.id.plus(5)
            it.userName = "akino"
            it
        }.toList().batchInsert()

        println("批量插入后————————")
        ACCOUNT.all<Account>().stream().peek { println(it) }.toList().filter { it.id.rem(2) == 0 }.batchDeleteById()

        println("批量删除后————————")
        //直接使用函数查询时需指定from表
        query<Account> { from(ACCOUNT) }.stream().peek { println(it) }.toList().filter { it.id.rem(3) == 0 }.map {
            it.userName = "cloud-player"
            it
        }.batchUpdateById()

        println("批量更新后————————")
        //使用表对象查询时无需指定from表
        ACCOUNT.query<Account> {}.forEach(::println)
    }

}
