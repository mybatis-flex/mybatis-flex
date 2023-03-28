# Row + Db 工具的使用

Db + Row 工具类，提供了在 Entity 实体类之外的数据库操作能力。使用 Db + Row 时，无需对数据库表进行映射， Row 是一个 HashMap 的子类，相当于一个通用的 Entity。以下为 Db + Row 的一些示例：


```java
//使用原生 SQL 插入数据
String sql = "insert into tb_account(id,name) value (?, ?)";
Db.insertBySql(sql,1,"michael");

//使用 Row 插入数据
Row account = new Row();
account.set("id",100);
account.set(ACCOUNT.USER_NAME,"Michael");
Db.insert("tb_account",account);


//根据主键查询数据
Row row = Db.selectOneById("tb_account","id",1);

//Row 可以直接转换为 Entity 实体类，且性能极高
Account account = row.toEntity(Account.class);


//查询所有大于 18 岁的用户
String listsql = "select * from tb_account where age > ?"
List<Row> rows = Db.selectListBySql(sql,18);


//分页查询：每页 10 条数据，查询第 3 页的年龄大于 18 的用户
QueryWrapper query = QueryWrapper.create()
        .where(ACCOUNT.AGE.ge(18));
Page<Row> rowPage = Db.paginate("tb_account",3,10,query);
```

> Db 工具类还提供了更多 增、删、改、查和分页查询等方法。
>
> 具体参考： [Db.java](./mybatis-flex-core/src/main/java/com/mybatisflex/core/row/Db.java) 。

## Row 转换为 Entity

```java
Row row = Db.selectOneById("tb_account","id",1);
Account entity = row.toEntity(Account.class);
```

## Row 字段转化为驼峰风格

```java
Row row = Db.selectOneById("tb_account","id",1);
Map result = row.toCamelKeysMap();
```

## Row 字段转换为下划线风格

```java
Row row = Db.selectOneById("tb_account","id",1);
Map result = row.toUnderlineKeysMap();
```

## Row 插入时，设置主键生成方式

**ID 自增**

```java
// ID 自增
Row row = Row.ofKey(RowKey.ID_AUTO);
row.set(ACCOUNT.USER_NAME,"Michael");

Db.insert("tb_account",row);
```

**ID 为 UUID**

```java
// ID 为 uuid
Row row = Row.ofKey(RowKey.ID_UUID);
row.set(ACCOUNT.USER_NAME,"Michael");

Db.insert("tb_account",row);
```
**自定义 Row 主键生成方式**

```java
// 自定义 Row Key，整个应用定义一个常量即可
RowKey myRowKey = RowKey.of("id", KeyType.Generator, "uuid", true);

// 使用自定义的 RowKey
Row row = Row.ofKey(myRowKey);
row.set(ACCOUNT.USER_NAME,"Michael");

Db.insert("tb_account",row);
```

## Db 中的 RowMapperInvoker

在 Db.java 工具类中，内置了一个静态的 RowMapperInvoker 对象，用来真正的与 Mybatis 交互执行
SQL，在一个应用中，若有多个数据源，没有让每个数据源持有一个 RowMapperInvoker 对象。

```java
//environmentId 为 mybatis 中的 environment 的 id
RowMapperInvoker myDb = Db.invoker("environmentId");
Row row = myDb.selectOneById("tb_account","id",1);
```

## Db 的事务管理
在 Db 的 RowMapperInvoker 中，有一个对象为 rowSessionManager，我们可以通过设置 rowSessionManager
来管理自己的事务。

在 Spring 中，我们可以通过一下代码让 Spring 来管理 Db 操作事务：

```java
Db.invoker().setRowSessionManager(new SpringRowSessionManager());

// 或者可以自定义自己的 RowSessionManager 来管理事务
Db.invoker().setRowSessionManager(new RowSessionManager() {
    @Override
    public SqlSession getSqlSession(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        return SqlSessionUtils.getSqlSession(sqlSessionFactory, executorType, null);
    }

    @Override
    public void releaseSqlSession(SqlSession sqlSession, SqlSessionFactory sqlSessionFactory) {
        SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory);
    }
});
```
