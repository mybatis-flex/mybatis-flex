# MyBatis-Flex 的增删改功能

MyBatis-Flex 内置了一个名为 `BaseMapper` 的接口，它实现了基本的增删改查功能以及分页查询功能。

> MyBatis-Flex 的 **代码生成器** 生成的所有 Mapper 辅助类，都是继承 BaseMapper。

## 新增数据

`BaseMapper` 的接口提供了 insert 和 insertBatch 方法，用于新增数据；

<!--@include: ./parts/base-mapper-insert-methods.md-->

### 用 UpdateWrapper 新增数据 <Badge type="tip" text="^ v1.5.8" />

在某些场景下，我们希望在新增数据时，新增数据字段内容是数据库的某个 `函数` 或者 `SQL片段` 生成的内容，而非我们手动设置的内容。
例如，我们希望执行的 SQL 如下：

```sql
INSERT INTO `tb_account`(`user_name`,  `birthday`)
VALUES (?, now())
```

> 以上 SQL 中，`birthday` 是由 `now()` 函数生成的内容。

那么，java 代码如下：

```java 9
@Test
public void testInsertWithRaw() {
    Account account = new Account();
    account.setUserName("michael");

    Account newAccount = UpdateWrapper.of(account)
//       .setRaw("birthday", "now()")
//       .setRaw(ACCOUNT.BIRTHDAY, "now()")
        .setRaw(Account::getBirthday, "now()")
        .toEntity();

    accountMapper.insert(newAccount);
}
```

或者复杂一点的：

```java 7
@Test
public void testInsertWithRaw() {
    Account account = new Account();
    account.setUserName("michael");

    Account newAccount = UpdateWrapper.of(account)
        .setRaw(Account::getBirthday, "(select xxx from ...)")
        .toEntity();

    accountMapper.insert(newAccount);
}
```
其生成的 SQL 如下：

```sql
INSERT INTO `tb_account`(`user_name`,  `birthday`)
VALUES (?, (select xxx from ...))
```

> 注意，通过 `UpdateWrapper.setRaw()` 的设置，会覆盖注解 `@Column.onUpdateValue` 配置的内容。



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

## UpdateChain

UpdateChain 是一个对 `UpdateEntity`、`UpdateWrapper` 等进行封装的一个工具类，方便用户用于进行链式操作。

假设我们要更新 `Account` 的 `userName` 为 "`张三`"，更新年龄在之前的基础上加 1，更新代码如下：

```java
@Test
public void testUpdateChain() {
    UpdateChain.of(Account.class)
        .set(Account::getUserName, "张三")
        .setRaw(Account::getAge, "age + 1")
        .where(Account::getId).eq(1)
        .update();
}
```
以上方法调用时，MyBatis-Flex 内部执行的 SQL 如下：

```sql
UPDATE `tb_account` SET `user_name` = '张三' , `age` = age + 1
WHERE `id` = 1
```

更多关于 **链式操作**，请点击这个 [这里](./chain.html#updatechain-示例)。

## `set()` 和 `setRaw()` 的区别

在 `Row`、`UpdateWrapper`、`UpdateChain` 中，都提供了 `set()` 和 `setRaw()` 两个方法用于设置数据。
那么，他们有什么区别呢？

- `set()` 方法用于设置参数数据。
- `setRaw()` 用于设置 SQL 拼接数据。

例如：

```java
UpdateChain.of(Account.class)
    .set(Account::getUserName, "张三")
    .where(Account::getId).eq(1)
    .update();
```

其执行的 SQL 如下：

```sql
UPDATE `tb_account` SET `user_name` = ? WHERE `id` = 1
```

如果是使用 `setRaw()` 方法：

```java
UpdateChain.of(Account.class)
    .setRaw(Account::getUserName, "张三")
    .where(Account::getId).eq(1)
    .update();
```

以上代码执行时，参数 "`张三`" 会直接参与 SQL 拼接，可能会造成 SQL 错误，其 SQL 如下：

```sql
UPDATE `tb_account` SET `user_name` = 张三 WHERE `id` = 1
```

因此，需要用户 **【特别注意!!!】**，`setRaw()` 传入不恰当的参数时，可能会造成 SQL 注入的危险。
因此，调用 `setRaw()` 方法时，需要开发者自行对其参数进行 SQL 注入过滤。


**`setRaw()` 经常使用的场景：**

- **场景1： 用户充值，更新用户金额：**

```java
UpdateChain.of(Account.class)
    .setRaw(Account::getMoney, "money + 100")
    .where(Account::getId).eq(1)
    .update();
```

其执行的 SQL 如下：

```sql
UPDATE `tb_account` SET  `money` = money + 100
WHERE `id` = 1
```

- **场景2：执行某些特殊函数：**

```java
UpdateChain.of(Account.class)
    .setRaw(Account::getUserName, "UPPER(user_name)")
    .where(Account::getId).eq(1)
    .update();
```

其执行的 SQL 如下：

```sql
UPDATE tb_account SET  user_name = UPPER(user_name)
WHERE id = 1
```

或者

```java
UpdateChain.of(Account.class)
    .setRaw(Account::getUserName, "utl_raw.cast_to_raw('some magic here')")
    .where(Account::getId).eq(1)
    .update();
```

其执行的 SQL 如下：

```sql
UPDATE tb_account SET  user_name = utl_raw.cast_to_raw('some magic here')
WHERE id = 1
```
