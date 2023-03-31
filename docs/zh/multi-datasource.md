# 多数据源

MyBaits-Flex 内置了功能完善的多数据源支持<Badge type="tip" text="^1.0.6" />，不需要借助第三方插件或者依赖，开箱即用，
支持包括 `druid`、`hikaricp`、`dbcp2`、`beecp` 在内的任何数据源，MyBatis-Flex 多数据源配置如下：

```yaml
mybatis-flex:
  datasource:
    ds1:
      url: jdbc:mysql://127.0.0.1:3306/db
      username: root
      password: 123456
    ds2:
      url: jdbc:mysql://127.0.0.1:3306/db2
      username: root
      password: 123456
```

在以上配置中，`ds1` 和 `ds2` 是由用户自定义的，我们可以理解为数据源的名称，或者数据源的 `key`，这个在动态切换数据库中非常有用。

在无 Spring 框架的场景下，代码如下：

```java
DataSource dataSource1 = new HikariDataSource();
dataSource1.setJdbcUrl(....)

DataSource dataSource2 = new HikariDataSource();
dataSource2.setJdbcUrl(....)

DataSource dataSource3 = new HikariDataSource();
dataSource3.setJdbcUrl(....)
        
MybatisFlexBootstrap.getInstance()
        .setDataSource("ds1", dataSource1)
        .addDataSource("ds2", dataSource2)
        .addDataSource("ds3", dataSource3)
        .start();
```
## 开始使用

默认使用第一个配置的数据源：

```java
List<Row> rows = Db.selectAll("tb_account");
System.out.println(rows);
```

通过编码的方式，切换到数据源 `ds2`：

```java
try{
    DataSourceKey.use("ds2")
    List<Row> rows = Db.selectAll("tb_account");
    System.out.println(rows); 
}finally{
    DataSourceKey.clear(); 
}
```

或者

```java
List<Row> rows =  DataSourceKey.use("ds2"
    , () -> Db.selectAll("tb_account"));
```

## 数据源切换（设置）

MyBatis-Flex 提供了 3 种方式来配置数据源：
- 1、编码，使用` DataSourceKey.use` 方法。
- 2、`@UseDataSource("dataSourceName")` 在 Mapper 方法上，添加注解，用于指定使用哪个数据源。
- 3、`@Table(dataSource="dataSourceName")` 在 Entity 类上添加注解，该 Entity 的增删改查请求默认使用该数据源。

::: tip 数据源配置的优先级
`DataSourceKey.use()` > `@UseDataSource()` > `@Table(dataSource="...")`
:::
