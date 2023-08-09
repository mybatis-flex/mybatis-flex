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
package com.mybatisflex.kotlin.db

import java.util.NoSuchElementException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 数据库配置对象，暂时未启用
 * @author 卡莫sama(yuanjiashuai)
 * @date 2023/8/7
 */
object DbConfig {

    var url: String by IfNullVar { "" }
    var username: String by IfNullVar { "" }
    var password: String by IfNullVar { "" }


}

class IfNullVar<T : Any>(private var init: (() -> T)?) : ReadWriteProperty<Any?, T> {
    private var _value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (_value == null && init != null) {
            synchronized(this) {
                if (_value == null) {
                    _value = init?.invoke()
                    //释放引用
                    init = null
                }
            }
        }
        return this._value?:throw NoSuchElementException()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        _value = value
    }
}
