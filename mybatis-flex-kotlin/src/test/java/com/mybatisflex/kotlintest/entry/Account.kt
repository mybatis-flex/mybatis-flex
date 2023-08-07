package com.mybatisflex.kotlintest.entry

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.NoneListener
import com.mybatisflex.annotation.Table
import com.mybatisflex.kotlin.entry.Entry
import java.util.*

/**
 * 测试用数据类（最好不要写成data class，否则需要与数据库字段数据顺序一致）
 * @author 卡莫sama(yuanjiashuai)
 * @date 2023/8/7
 */
@Table(value = "tb_account", onUpdate = [NoneListener::class])
data class Account(
    @Id var id: Int?,
    @Column("u_name") var userName: String?,
    var age: Int?,
    var birthday: Date?,
    ) : Entry(){
    override fun toString(): String {
        return "Account(id=$id, userName=$userName, birthday=$birthday, age=$age)"
    }
}
