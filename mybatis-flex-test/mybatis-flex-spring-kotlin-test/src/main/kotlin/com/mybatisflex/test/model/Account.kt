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
package com.mybatisflex.test.model

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.NoneListener
import com.mybatisflex.annotation.Table
import com.mybatisflex.core.activerecord.Model
import com.mybatisflex.test.model.table.AccountTableDef
import java.util.*
/**
 * 测试用数据类（最好不要写成data class，否则没有无参构造需要与数据库字段数据顺序一致）
 * @author 卡莫sama(yuanjiashuai)
 * @date 2023/8/7
 */
@Table(value = "tb_account", onUpdate = [NoneListener::class], onSet = [AccountOnSetListener::class])
data class Account(
    @Id var id: Int,
    var userName: String?,
    var age: Int?,
    var birthday: Date?,
) : Model<Account>(){
   companion object : AccountTableDef()

    override fun toString(): String {
        return "Account(id=$id, userName=$userName, birthday=$birthday, age=$age)"
    }
}

