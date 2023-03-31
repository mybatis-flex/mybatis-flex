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

## 通过注解指定数据源


## 编码动态切换数据源