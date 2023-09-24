# 读写分离

MyBatis-Flex 的读写分离功能是基于 【[多数据源](./multi-datasource.md)】 功能来实现的。

读写分离的功能，要求当前环境必须是多个数据库（也可理解为多个数据源），其原理是：
让主数据库（master）处理事务性操作，比如：增、删、改（INSERT、DELETE、UPDATE），而从数据库（slave）处理查询（SELECT）操作。


## 实现原理

在 MyBatis 框架中，我们知道： 所有关于数据库的的操作都是通过 Mapper 来进行的，Mapper 里的一个方法，往往是和一个执行 SQL 一一对应。

因此，在 MyBatis-Flex 中，提供了一种基于 Mapper 方法的读写分离策略。

## 数据源分片策略

在 MyBatis-Flex 框架中，我们需要通过实现 `DataSourceShardingStrategy` 接口来自定义自己的数据源读写分离策略（分片策略）例如：

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

- currentDataSourceKey：当前使用的数据源 key
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
我们的需求是：在 增删改 时，走 master 数据源，而在查询时，随机自动使用 `slave1`、`slave2` 数据源进行负载均衡。


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

## 注意事项

> MyBatis-Flex 的读写分离组件，只进行数据查询和数据操作时的读写分离，并不涉及主从数据库之间的数据同步，主从数据库同步需要用户自己在数据库服务器，通过第三方组件去实现。
