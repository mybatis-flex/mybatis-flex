# 读写分离

MyBatis-Flex 的读写分离功能是基于 【多数据源】 功能来实现的。

读写分离的功能，要求当前的环境必须是多个数据库（也可理解为多个数据源），其原理是：
让主数据库（master）处理事务性操作，比如：增、删、改（INSERT、DELETE、UPDATE），而从数据库（slave）处理 SELECT 查询操作。

在 MyBatis 框架中，我们知道： 所有关于数据库的的操作都是通过 Mapper 来实现的，Mapper 里的一个方法，往往是和一个 SQL 一一对应。

因此，在 MyBatis-Flex 中，提供了一种基于 Mapper 方法的读写分离策略。

## 分片策略

自定义 `DataSourceShardingStrategy` 例如：

```java
public class MyStrategy implements DataSourceShardingStrategy {

    public String doSharding(String currentDataSourceKey
        , Object mapper, Method mapperMethod, Object[] methodArgs){

        //返回新的数据源 key
        return "newDataSourceKey";
    }
}
```

doSharding 的参数分别为：

- currentDataSourceKey：当前由用户端已配置的 key
- mapper：当前的 mapper 对象
- mapperMethod: 当前的 mapper 方法
- methodArgs：当前的 mapper 方法的参数内容

自定义好 数据源分片策略后，在项目启动时，需要通过 `DataSourceManager` 配置自己的自定义分片策略：

```java
DataSourceManager.setDataSourceShardingStrategy(new MyStrategy());
```

## 示例代码

假设数据源配置如下：


```yaml
mybatis-flex:
  datasource:
    master:
      type: druid
      url: jdbc:mysql://127.0.0.1:3306/master-db
      username: root
      password: 123456
    slave1:
      type: com.your.datasource.type2
      url: jdbc:mysql://127.0.0.1:3306/slave1
      username: root
      password: 123456
    slave2:
      type: com.your.datasource.type2
      url: jdbc:mysql://127.0.0.1:3306/slave2
      username: root
      password: 123456
    other:
      type: com.your.datasource.type2
      url: jdbc:mysql://127.0.0.1:3306/other
      username: root
      password: 123456
```
以上配置中，一共有 4 个数据源，分别为 `master`、`slave1`、`slave2`、`other`。
假设我们的需求是：在 增删改 时，走 master，而在查询时，自动使用 `slave1`、`slave2` 进行负载均衡。


那么，我们的分片策略代码如下：

```java
public class MyStrategy implements DataSourceShardingStrategy {

    public String doSharding(String currentDataSourceKey
        , Object mapper, Method mapperMethod, Object[] methodArgs){

        // 不管 other 数据源的情况
        if ("other".equals(currentDataSourceKey)){
            return currentDataSourceKey;
        }

        // 如果 mapper 的方法属于 增删改，使用 master 数据源
        if (StringUtil.startWithAny(mapperMethod.getName(),
            "insert", "delete", "update")){
            return "master";
        }

        //其他场景，使用 slave1 或者 slave2 进行负载均衡
        return "slave*";
    }
}
```
