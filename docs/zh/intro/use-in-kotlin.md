# 在 Kotlin 中使用 Mybatis-Flex

**MyBatis-Flex-Kotlin 基于 Mybatis-Flex 的 Kotlin 扩展模块，方便 Kotlin 开发者使用 MyBatis-Flex 进行开发。**


>它继承了 Mybatis-Flex 轻量的特性，同时拥有 Kotlin 特有的扩展方法、中缀表达式与DSL等语法支持，
>使其拥有了更高的灵活性。让我们可以更加轻松的在 Kotlin 中使用 Mybaits-Flex 所带来的开发效率和开发体验。

* [Gitee](https://gitee.com/mybatis-flex/mybatis-flex-kotlin)
* [Github](https://github.com/KAMO030/MyBatis-Flex-Kotlin)

## 特征

- 轻量：只基于 Mybatis-Flex 核心库 ，只做扩展不做改变
- 简明：使用 DSL 让查询语句更加简单明了
- 快捷：结合 Kotlin 特性快速对数据库进行操作

## 亮点

- 快速构建启动：通过DSL➕重载运算符，快速配置 MybatisFlexBootstrap 实例并启动：
    ```kotlin
    runFlex {
        // 配置数据源 相当于 setDataSource(dataSource)
        +dataSource
        // 配置Mapper 相当于 addMapper(AccountMapper::class.java)
        +AccountMapper::class
        // 配置日志输出 相当于 setLogImpl(StdOutImpl::class.java)
        logImpl = StdOutImpl::class
      }
    ```
- 快速查询数据：通过DSL➕泛型快速编写查询语句并查询:  (快速查询提供三个函数：all, filter 和 query )
  >- `all<实体类>()` 查泛型对应的表的所有数据
  >- `filter<实体类>(vararg KProperty<*>, ()->QueryCondition)` 按条件查泛型对应的表的数据
  >- `query<实体类>(QueryScope.()->Unit)` 较复杂查泛型对应的表的数据 (如: 分组,排序等)
  >- `paginateWith(pageNumber: Number, pageSize: Number, totalRow: Number? = null, queryConditionGet: () -> QueryCondition): Page<实体类>`
     与 `paginate(pageNumber: Number, pageSize: Number, totalRow: Number? = null, init: QueryScope.() -> Unit): Page<实体类>` 使用分页的条件查询与较复杂查询
- 简明地构建查询：通过中缀表达式➕扩展方法能更加简单明了的构建条件:

    * **【对比原生】**
        * **原生**
          ```kotlin
            val queryWrapper = QueryWrapper.create()
                .select(QueryColumn("id"), QueryColumn("user_name"))
                .where(QueryColumn("age").isNotNull()).and(QueryColumn("age").ge(17))
                .orderBy(QueryColumn("id").desc())
            mapper<AccountMapper>().selectListByQuery(queryWrapper)
          ```

        * **扩展后**
          ```kotlin
            // 无需注册Mapper与APT/KSP即可查询操作
            val accountList: List<Account> = query {
                select(Account::id, Account::userName)
                whereWith {
                    Account::age.isNotNull and (Account::age ge 17)
                }
                orderBy(-Account::id)
            }
          ```
      执行的SQL:
      ```sql
       SELECT `id`, `user_name` FROM `tb_account` WHERE `age` IS NOT NULL  AND `age` >= 17 ORDER BY `id` DESC
      ```

    * **【条件优化】**
        - 例如: 查询属性是否在一个连续的区间时，会自动将 IN 转为 BETWEEN 调用
          ```kotlin
           filter<Account> { Account::age `in` (17..19) }
          ```
          执行的SQL:
          ```sql
          SELECT * FROM `tb_account` WHERE `age` BETWEEN  17 AND 19
          ```
        - 例如: 构建多属性组合 IN (最多支持三个属性)
          ```kotlin
          filter<Account> {
            (Account::id to Account::userName to Account::age).inTriple(
                1 to "张三" to 18,
                2 to "李四" to 19,
            )
          }
          ```
          执行的SQL:
          ```sql
          SELECT * FROM `tb_account`
          WHERE (`id` = 1 AND `user_name` = '张三' AND `age` = 18)
             OR (`id` = 2 AND `user_name` = '李四' AND `age` = 19)
          ```
- 摆脱APT: 使用扩展方法摆脱对 APT(注解处理器) 的使用,直接使用属性引用让代码更加灵活优雅:
  >  使用APT: `ACCOUNT.ID eq 1` ,使用属性引用: `Account::id eq 1`
  >
  >  (少依赖一个模块且不用开启注解处理器功能)
- 属性类型约束：使用泛型➕扩展方法对操作的属性进行类型约束:
  > 如: Account 中 age 属性为 Int 类型
  >
  > 那么使用between时后续参数也必须是Int： `Account::age between (17 to 19)`
  >
  > 而如果写成String：`Account::age between ("17" to "19")`则会报错提醒

## 总结
引入 Mybatis-Flex-Kotlin 扩展模块在 Kotlin 中使用 Mybaits-Flex 能够基于 Kotlin 强大的语法特性可以让我们更加轻松方便地操作数据库，极大提高了开发效率和开发体验。

## 快速开始

点击链接进入详情：
- https://github.com/KAMO030/MyBatis-Flex-Kotlin#快速开始
- https://gitee.com/mybatis-flex/mybatis-flex-kotlin#快速开始

## 更多使用

- 功能 1：[Bootstrap简化配置](https://gitee.com/mybatis-flex/mybatis-flex-kotlin/blob/main/docs/bootstrapExt.md)
- 功能 2：[简单查询与扩展](https://gitee.com/mybatis-flex/mybatis-flex-kotlin/blob/main/docs/extensions.md)
- 功能 3：[向量查询](https://gitee.com/mybatis-flex/mybatis-flex-kotlin/blob/main/docs/vec.md) (实验性)
- 功能 4：[KSP](https://gitee.com/mybatis-flex/mybatis-flex-kotlin/blob/main/docs/ksp.md)
