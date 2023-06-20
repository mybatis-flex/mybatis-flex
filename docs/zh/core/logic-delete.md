# 逻辑删除

## 逻辑删除简介

逻辑删除指的是在删除数据的时候，并非真正的去删除，而是将表中列所对应的状态字段（status）做修改操作，
实际上并未删除目标数据。

我们可以进行表的字段设计时，用一个列标识该数据的 "删除状态"，在 mybatis-flex 中，正常状态的值为 0， 已删除
的值为 1（可以通过设置 FlexGlobalConfig 来修改这个值）。

## MyBatis-Flex 逻辑删除示例

假设在 tb_account 表中，存在一个为 is_deleted 的字段，用来标识该数据的逻辑删除，那么 tb_account 表
对应的 "Account.java" 实体类应该配置如下：

```java

@Table("tb_account")
public class Account {

    @Column(isLogicDelete = true)
    private Boolean isDelete;
    
    //Getter Setter...
}
```

此时，当我们执行如下的删除代码是：

```java
accountMapper.deleteById(1);
```
MyBatis 执行的 SQL 如下：

```sql
UPDATE `tb_account` SET `is_delete` = 1 
WHERE `id` = ? AND `is_delete` = 0
```
可以看出，当执行 deleteById 时，MyBatis 只是进行了 update 操作，而非 delete 操作。

## 注意事项

当 "tb_account" 的数据被删除时（ is_delete = 1 时），我们通过 MyBatis-Flex 的 selectOneById 去查找数据时，会查询不到数据。
原因是 `selectOneById` 会自动添加上 `is_delete = 0` 条件，执行的 sql 如下：

```java
SELECT * FROM tb_account where id = ? and is_delete = 0
```

不仅仅是 selectOneById 方法会添加 `is_delete = 0` 条件，BaseMapper 的以下方法也都会添加该条件：

- selectOneBy**
- selectListBy**
- selectCountBy**
- paginate

## 逻辑删除的默认值配置

在某些场景下，我们可能希望数据库存入的逻辑删除中的值并非 0 和 1，比如可能是 true 和 false 等，那么，我们可以通过配置 `FlexGlobalConfig`
来修改这个默认值。

如下代码所示：

```java
FlexGlobalConfig globalConfig = FlexGlobalConfig.getDefaultConfig();

//设置数据库正常时的值
globalConfig.setNormalValueOfLogicDelete("...");

//设置数据已被删除时的值
globalConfig.setDeletedValueOfLogicDelete("...");
```

## 跳过逻辑删除处理

在某些场景下，我们再执行查询、更新或删除数据时，有必要跳过 MyBatis-Flex 自动添加的逻辑删除的相关条件，
此时，我们可以使用 LogicDeleteManager.execWithoutLogicDelete() 方法处理，代码如下：

```java
LogicDeleteManager.execWithoutLogicDelete(() -> 
            accountMapper.deleteById(1)
        );
```
以上代码中，`accountMapper` 会直接对 `Account` 数据进行物理删除，忽略逻辑删除字段配置。

## 自定义逻辑删除处理功能

在社区中，有许多用户提出希望使用时间类型，当删除时，设置删除字段为`当前时间`，当正常时，设置为 `0` 或者 `null`。
那么，我们可以通过 `LogicDeleteManager` 设置一个新的 `LogicDeleteProcessor`：

`LogicDeleteProcessor` 接口的内容如下：

```java
public interface LogicDeleteProcessor {

    /**
     * 用户构建查询正常数据的条件
     * @param logicColumn
     * @param dialect
     */
    String buildLogicNormalCondition(String logicColumn, IDialect dialect);

    /**
     * 用户与构建删除数据时的内容
     * @param logicColumn
     * @param dialect
     */
    String buildLogicDeletedSet(String logicColumn, IDialect dialect);

    /**
     * 用于构建通过 QueryWrapper 查询数据时的内容
     * @param queryWrapper
     * @param tableInfo
     */
    void buildQueryCondition(QueryWrapper queryWrapper, TableInfo tableInfo);
}
```

具体实现可以参考：[DefaultLogicDeleteProcessorImpl](https://gitee.com/mybatis-flex/mybatis-flex/blob/main/mybatis-flex-core/src/main/java/com/mybatisflex/core/logicdelete/DefaultLogicDeleteProcessorImpl.java)