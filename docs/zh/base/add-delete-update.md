# Mybatis-Flex 的增删改功能

Mybatis-Flex 内置了一个名为 `BaseMapper` 的接口，它实现了基本的增删改查功能以及分页查询功能。

> Mybatis-Flex 的 **代码生成器** 生成的所有 Mapper 辅助类，都是继承 BaseMapper。

## 新增数据

`BaseMapper` 的接口提供了 insert 和 insertBatch 方法，用于新增数据；

- **insert**： 新增 1 条数据
- **insertSelective**： 新增 1 条数据，忽略 null 值的字段
- **insertBatch**： 新增多条数据
- **insertBatch(entities, int size)**： 批量插入 entity 数据，按 size 切分


## 删除数据

`BaseMapper` 的接口提供了 deleteById、deleteBatchByIds、deleteByMap、deleteByQuery 方法，用于删除数据；

- **deleteById(id)** ：根据主键 id 删除数据，复合主键需要传入一个数组，例如 [1,100]
- **deleteBatchByIds(idList)** ：根据主键的 集合，批量删除多条数据
- **deleteByMap(map)** ：根据 `map<字段名，值>` 组成的条件删除数据，字段名和值的关系为相等的关系，同时，防止 "不小心" 全表 
删除数据，map 的值不允许为 null 或者 空数据。
- **deleteByCondition(condition)**：根据 condition 构建的条件来删除数据
- **deleteByQuery(queryWrapper)**：根据 queryWrapper 组成的条件删除数据。


**deleteByQuery(queryWrapper)** 方法示例：

```java
QueryWrapper queryWrapper = QueryWrapper.create();
queryWrapper.where(ACCOUNT.ID.ge(100));

//通过 queryWrapper 删除
accountMapper.deleteByQuery(queryWrapper);
```

**deleteByCondition(condition)** 方法示例：

```java
accountMapper.deleteByCondition(ACCOUNT.ID.ge(100));
```


以上的代码，会删除所有 id >= 100 的数据，其执行的 Sql 如下：

```sql
delete from tb_account where id >= 100;
```

>tip: QueryWrapper 非常灵活，也是 Mybatis-Flex 的特色之一，更多关于 QueryWrapper 的
> 请看 [QueryWrapper 章节](./querywrapper)。

## 更新数据

`BaseMapper` 的接口提供了 update、updateByMap、updateByQuery 方法，用于更新数据；

- **update(entity)**：根据主键更新到 entity 到数据库，要求主键值不能为空，否则会抛出异常。同时，数据为 null 的字段 **不会** 更新到数据库。 
- **update(entity, ignoreNulls)**：根据主键更新到 entity 到数据库，要求主键值不能为空。ignoreNulls 为是否忽略 null 字段，如果为 false，所有 null 字段都会更新到数据库。
- **updateByMap(entity, map)**：根据 `map<字段名，值>` 组成的条件更新到 entity 到数据库，entity 可以没有主键（如果有也会被忽略）, entity 的 null 属性，会自动被忽略。
- **updateByCondition(entity, condition)**：根据 condition 构建的条件更新到 entity 到数据库，entity 可以没有主键（如果有也会被忽略）, entity 的 null 属性，会自动被忽略。
- **updateByCondition(entity, ignoreNulls, condition)**：ignoreNulls 是否忽略 null 值，默认为 true，如果为 false，所有 null 字段都会更新到数据库。
- **updateByQuery(entity, queryWrapper)**：根据 queryWrapper 组成的条件更新到 entity 到数据库，entity 可以没有主键（如果有也会被忽略）, entity 的 null 属性，会自动被忽略。
- **updateByQuery(entity, ignoreNulls, queryWrapper)**：据 queryWrapper 组成的条件更新到 entity 到数据库，entity 可以没有主键（如果有也会被忽略）。 ignoreNulls 用于是否忽略 entity 的 null 属性
， 若 ignoreNulls 为 false，entity 的所有 null 属性都会被更新到数据库。


## 部分字段更新

在很多场景下，我们希望只更新**部分字段**，而更新的字段中，一些为 null，一些非 null。此时需要用到 `UpdateEntity` 工具类，以下是示例代码：

```java
Account account = UpdateEntity.of(Account.class, 100);
//Account account = UpdateEntity.of(Account.class);
//account.setId(100);
        
account.setUserName(null);
account.setAge(10);

accountMapper.update(account);
```




以上的示例中，会把 id (主键)为 100 这条数据中的 user_name 字段更新为 null，age 字段更新为 10，其他字段不会被更新。

也就是说，通过 UpdateEntity 创建的对象，只会更新调用了 setter 方法的字段，若不调用 setter 方法，不管这个对象里的属性的值是什么，都不会更新到数据库。

其执行的 sql 内容如下：

```sql
update tb_account
set user_name = ?, age = ? where id = ? 
#参数: null,10,100
```

注意：

```java
Account account = UpdateEntity.of(Account.class, 100);
```
等同于：

```java
Account account = UpdateEntity.of(Account.class);
account.setId(100);
```

