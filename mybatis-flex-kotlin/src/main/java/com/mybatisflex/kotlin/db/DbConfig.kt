package com.mybatisflex.kotlin.db

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
        if (_value == null) {
            synchronized(this) {
                if (_value == null) {
                    _value = init!!()
                    init = null
                }
            }
        }
        return this._value!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        _value = value
    }
}
