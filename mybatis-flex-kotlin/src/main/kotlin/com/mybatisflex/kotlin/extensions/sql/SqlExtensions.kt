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
package com.mybatisflex.kotlin.extensions.sql

import com.mybatisflex.core.query.*
import java.util.function.Consumer

/*
 * sql操作扩展
 * @author 卡莫sama(yuanjiashuai)
 * @date 2023/8/7
 */
infix fun QueryColumn.eq(value: Any?): QueryCondition {
    return this.eq(value)
}

inline fun QueryCondition.andIf(test: Boolean, block: () -> QueryCondition): QueryCondition =
    if (test) this.and(block()) else this

inline fun QueryCondition.orIf(test: Boolean, block: () -> QueryCondition): QueryCondition =
    if (test) this.or(block()) else this

inline fun `if`(test: Boolean, block: () -> QueryCondition): QueryCondition =
    if (test) block() else QueryCondition.createEmpty()

infix fun QueryColumn.like(value: String): QueryCondition = this.like(value)

infix fun QueryCondition.and(other: QueryCondition): QueryCondition = this.and(other)

infix fun QueryCondition.or(other: QueryCondition): QueryCondition = this.or(other)

infix fun QueryColumn.`=`(value: Any?): QueryCondition = this.eq(value)

infix fun QueryColumn.`!=`(value: Any?): QueryCondition = this.ne(value)

infix fun QueryColumn.gt(value: Any?): QueryCondition = this.gt(value)

infix fun QueryColumn.ge(value: Any?): QueryCondition = this.ge(value)

infix fun QueryColumn.le(value: Any?): QueryCondition = this.le(value)

infix fun QueryColumn.lt(value: Any?): QueryCondition = this.lt(value)

infix fun QueryColumn.between(pair: Pair<Any?, Any?>): QueryCondition = this.between(pair.first, pair.second)

infix fun QueryColumn.notBetween(pair: Pair<Any?, Any?>): QueryCondition = this.notBetween(pair.first, pair.second)

infix fun QueryColumn.notIn(value: Collection<Any?>): QueryCondition = this.notIn(value)

infix fun QueryColumn.notIn(values: Array<Any?>): QueryCondition = this.notIn(values)

infix fun QueryColumn.`in`(value: Collection<Any?>): QueryCondition = this.`in`(value)

infix fun QueryColumn.`in`(values: Array<Any?>): QueryCondition = this.`in`(values)

infix fun QueryWrapper.`as`(alias: String?) = this.`as`(alias)

infix fun <M> Joiner<M>.`as`(alias: String?): Joiner<M> = this.`as`(alias)

infix fun <M> Joiner<M>.on(on: String?): M = this.on(on)

infix fun <M> Joiner<M>.on(on: QueryCondition?): M = this.on(on)

infix fun <M> Joiner<M>.on(consumer: Consumer<QueryWrapper?>): M = this.on(consumer)

infix fun QueryWrapper.orderBy(orderBys: Collection<QueryOrderBy?>): QueryWrapper = this.orderBy(*orderBys.toTypedArray())

infix fun QueryWrapper.limit(rows: Number): QueryWrapper = this.limit(rows)

infix fun QueryWrapper.limit(pair: Pair<Number?, Number?>): QueryWrapper = this.limit(pair.first,pair.second)
