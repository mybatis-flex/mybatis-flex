# MyBatis-Flex 的增删改功能

MyBatis-Flex 内置了一个名为 `BaseMapper` 的接口，它实现了基本的增删改查功能以及分页查询功能。

> MyBatis-Flex 的 **代码生成器** 生成的所有 Mapper 辅助类，都是继承 BaseMapper。

## 新增数据

`BaseMapper` 的接口提供了 insert 和 insertBatch 方法，用于新增数据；

<!--@include: ./parts/base-mapper-insert-methods.md-->




## 删除数据

`BaseMapper` 的接口提供了 deleteById、deleteBatchByIds、deleteByMap、deleteByQuery 方法，用于删除数据；

<!--@include: ./parts/base-mapper-delete-methods.md-->


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

>tip: QueryWrapper 非常灵活，也是 MyBatis-Flex 的特色之一，更多关于 QueryWrapper 的
> 请看 [QueryWrapper 章节](./querywrapper)。

## 更新数据

`BaseMapper` 的接口提供了 update、updateByMap、updateByQuery 方法，用于更新数据；

<!--@include: ./parts/base-mapper-update-methods.md-->


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

## 部分字段更新（增强）

在以上的部分字段更新中，只能更新为用户传入的数据，但是有些时候我们想更新为数据库计算的数据，比如 SQL：

```sql
update tb_account
set user_name = ?, age = age + 1 where id = ?
```
此时，我们可以直接把 `Account` 强转为 `UpdateWrapper` 然后进行更新，例如：

```java
Account account = UpdateEntity.of(Account.class, 100);

account.setUserName(null);

// 通过 UpdateWrapper 操作 account 数据
UpdateWrapper wrapper = UpdateWrapper.of(account);
wrapper.setRaw("age", "age + 1")

accountMapper.update(account);
```

其执行的 SQL 为：

```sql
update tb_account
set user_name = null, age = age + 1 where id = 100
```

**更高级的用法**

示例1：


```java
Account account = UpdateEntity.of(Account.class, 100);

account.setUserName("Michael");

// 通过 UpdateWrapper 操作 account 数据
UpdateWrapper wrapper = UpdateWrapper.of(account);
wrapper.set(ACCOUNT.AGE, ACCOUNT.AGE.add(1))

accountMapper.update(account);
```

其执行的 SQL 为：

```sql
update tb_account
set user_name = "michael", age = age + 1 where id = 100
```


示例2：

```java
Account account = UpdateEntity.of(Account.class, 100);

account.setUserName("Michael");

// 通过 UpdateWrapper 操作 account 数据
UpdateWrapper wrapper = UpdateWrapper.of(account);
wrapper.set(ACCOUNT.AGE, select().from(...))

accountMapper.update(account);
```

其执行的 SQL 为：

```sql
update tb_account
set user_name = "michael", age = (select ... from ... )
where id = 100
```
