# 动态表名

动态表名指的是用户在对数据进行 增删改查 的时候，传入表名能够根据上下文信息（比如用户信息、应用信息）等，动态修改当前的表。

## 使用场景

- 1、多租户，不同的租户拥有不同的表
- 2、分库分表，减轻数据压力

## 如何使用

在应用启动时，通过调用 `TableManager.setDynamicTableProcessor()` 配置动态表名处理器 `DynamicTableProcessor` 即可，如下代码所示：

```java
TableManager.setDynamicTableProcessor(new DynamicTableProcessor() {
    @Override
    public String process(String tableName) {
        return tableName + "_01";
    }
});
```

通过以上配置后，我们对数据库进行增删改查，MyBatis-Flex 都会调用 `DynamicTableProcessor.process` 方法，获得最新的表名进行 SQL 构建操作。因此，我们应该在 `process` 方法中，
判断当前的上下文（用户信息、应用信息）等，动态的返回对应的表名。

在某些情况下，我们临时修改映射关系，而非通过 `DynamicTableProcessor.process` 方法获取，可以通过如下配置：

```java
try{
    TableManager.setHintTableMapping("tb_account", "tb_account_01");

    //这里写您的业务逻辑

} finally {
    TableManager.clear();
}

```
那么此时，当前线程不再通过 `DynamicTableProcessor` 去获取。

## 动态 Schema

动态 Schema 和动态表名类似，通过 `TableManager.setDynamicSchemaProcessor()` 配置动态 Schema 处理器 `DynamicSchemaProcessor` 即可，如下代码所示：

```java
TableManager.setDynamicSchemaProcessor(new DynamicSchemaProcessor() {
    @Override
    public String process(String schema, String table) {
        return schema + "_01";
    }
});
```

动态 Schema 的配置，只对使用了注解 `@Table(schema="xxx")` 的 Entity 有效。

## SpringBoot 支持
在 SpringBoot 项目下，直接通过 `@Configuration` 即可使用：

```java
@Configuration
public class MyConfiguration {

    @Bean
    public DynamicTableProcessor dynamicTableProcessor(){
        DynamicTableProcessor processor = new ....;
        return processor;
    }


    @Bean
    public DynamicSchemaProcessor dynamicSchemaProcessor(){
        DynamicSchemaProcessor processor = new ....;
        return processor;
    }

}
```
