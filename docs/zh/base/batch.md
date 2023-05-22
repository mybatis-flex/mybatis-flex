# 批量操作

在 MyBatis-Flex 中，提供了许多批量操作（批量插入、批量更新等）的方法，当有多个方法的时候，会经常导致误用的情况。



## `BaseMapper.insertBatch` 方法

这个方法的示例代码如下：

```java
List<Account> accounts = ....
mapper.insertBatch(accounts);
```
通过 `BaseMapper.insertBatch` 执行时，会通过 `accounts` 去组装一个如下的 SQL：

```sql
insert into tb_account(id,nickname, .....) values
(100,"miachel100", ....),
(101,"miachel101", ....),
(102,"miachel102", ....),
(103,"miachel103", ....),
(104,"miachel104", ....),
(105,"miachel105", ....);
```
这种有一个特点：在小批量数据执行插入的时候，效率是非常高；但是当数据列表过多时，其生成的 SQL 可能会非常大， 这个大的 SQL 
在传输和执行的时候就会变得很慢了。

因此，`BaseMapper.insertBatch` 方法只适用于在小批量数据插入的场景，比如 100 条数据以内。

## `Db.executeBatch` 方法

`Db.executeBatch` 可以用于进行批量的插入、修改和删除，以下是使用 `Db.executeBatch` 进行批量插入的示例：

```java
List<Account> accounts = ....
Db.executeBatch(accounts.size(), 1000, AccountMapper.class, (mapper, index) -> {
        Account account = accounts.get(index);
        mapper.insert(account);
    });
```
`Db.executeBatch` 是通过 JDBC 的 `Statement.executeBatch()` 进行批量执行；这个在大批量数据执行的时候，效率要比 `BaseMapper.insertBatch` 高出许多；

::: tip 提示
我看到有一些同学担心 `BaseMapper.insertBatch` 被误用，在 `IService` 中通过使用 `Db.executeBatch`  重写了 Service 的 `insertBatch` 方法，这也是没问题的。但还是需要明白，
`BaseMapper.insertBatch` 和 `Db.executeBatch` 的底层实现差异，以及不同的使用场景。
:::

## `Db.updateBatch` 方法 

这个方法的示例代码如下：

```java
List<Account> accounts = ....
String sql = "insert into tb_account(user_name,age,birthday) values (?,?,?)";
Db.updateBatch(sql, new BatchArgsSetter() {
        @Override
        public int getBatchSize() {
            return accounts.size();
        }

        @Override
        public Object[] getSqlArgs(int index) {
            Account account = accounts = accounts.get(index);
            Object[] args = new Object[3];
            args[0] = account.getUserName;
            args[1] = account.getAge();
            args[2] = new Date();
            return args;
        }
    });
```

虽然这个方法叫 `updateBatch`，但一样可以执行 `insert`、`delete`、`update` 等任何 SQL； 这个方法类似 Spring 的 `jdbcTemplate.batchUpdate()` 方法。


## `Db.updateEntitiesBatch` 方法  
这个方法用于批量根据 id 更新 entity，其是对 `Db.executeBatch` 的封装，使用代码如下：

```java
List<Account> accounts = ....
Db.updateEntitiesBatch(accounts, 1000);
```