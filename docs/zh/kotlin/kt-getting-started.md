# 基于 Kotlin 扩展 Mybatis-Flex

## 快速开始

在开始之前，我们假定您已经：

- 熟悉 Kotlin 环境配置及其开发
- 熟悉 关系型 数据库，比如 MySQL
- 熟悉 Kotlin 构建工具，比如 Gradle、Maven

> 当前章节涉及到的 [演示源码](https://gitee.com/mybatis-flex/mybatis-flex/tree/main/mybatis-flex-test/mybatis-flex-spring-kotlin-test) 已经全部上传
>
> 在开始之前，您也可以先下载到本地，导入到 idea 开发工具后，在继续看文档。


## 特点

- 本模块基于 Mybatis-Flex 核心库 ，只做扩展不做改变
- 结合 Kotlin 特性、DSL让数据库操作更简单

## Hello World 文档

**第 1 步：创建 Kotlin 项目，并添加 Kotlin 的扩展依赖**

>如何创建 Kotlin 项目可参考 [官方文档](https://www.kotlincn.net/docs/tutorials/jvm-get-started.html)

需要添加的主要依赖：

**【Kotlin】**

```kotlin
dependencies {
    implementation("com.mybatis-flex:mybatis-flex-kotlin:1.5.7")
    compileOnly("com.mybatis-flex:mybatis-flex-processor:1.5.7")
}
```

**【Maven】**

```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-kotlin</artifactId>
    <version>${mybatis-flex.version}</version>
</dependency>
```

**第 2 步：创建数据库表与配置数据源**

> 请参考 [快速开始](../intro/getting-started.md) 创建数据库表与配置数据源，
> 或者使用演示源码中的内嵌数据库快速体验

**第 3 步：编写实体类**

```kotlin
@Table("tb_account")
class Account {

     @Id
     var id: Long
     var userName: String
     var age: Integer
     var birthday: Date

}
```

- 使用 `@Table("tb_account")` 设置实体类与表名的映射关系
- 使用 `@Id` 标识主键

**第 4 步：开始使用**

添加测试类，进行功能测试：

```kotlin
fun main() {
        //加载数据源(为了方便演示这里使用了演示源码中的内嵌数据源)
        val dataSource: DataSource = EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("data-kt.sql")
                .build()
        //启动并配入数据源
        buildBootstrap { +dataSource }.start()
        //条件过滤查询并打印
        filter<Account> {
            ACCOUNT.ID `=` 1 and
                  (ACCOUNT.AGE `in` listOf(18,19) or (ACCOUNT.BIRTHDAY between ("2020-01-10" to "2020-01-12")) )
        }.forEach(::println)
        //查询全部数据并打印
        //ACCOUNT.all<Account>().forEach(::println)
}
```
执行的SQL：
```sql
SELECT * FROM `tb_account` WHERE`id` = 1 AND (`age` IN (18, 19) OR `birthday`BETWEEN  '2020-01-10' AND '2020-01-12' )
```
控制台输出：

```txt
Account(id=1, userName=张三, age=18, birthday=Sat Jan 11 00:00:00 CST 2020)
```

> 以上的示例中， `ACCOUNT` 为 MyBatis-Flex 通过 APT
> 自动生成，只需通过静态导入即可，无需手动编码。更多查看 [在Kotlin中使用注解处理器](../others/kapt.md)
>
> 若觉得 APT 使用不习惯，也可以使用代码生成器来生成。点击 [代码生成器文档](../others/codegen.md) 了解。

[comment]: <> (## 更多使用)

[comment]: <> (- 功能 1：[Bootstrap简化配置]&#40;&#41;)

[comment]: <> (- 功能 2：[简单查询]&#40;&#41;)

[comment]: <> (- 功能 3：[表实体扩展]&#40;&#41;)

[comment]: <> (- 功能 4：[SQL扩展/中缀]&#40;&#41;)

[comment]: <> (- 功能 5：[Mapper扩展]&#40;&#41;)

[comment]: <> (###### TODO ...)
