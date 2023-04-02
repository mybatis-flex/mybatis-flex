# Mybatis-Flex 和 Spring 整合

最近，看到群里有许多用户，用 MyBatis-Flex 整合 Spring 的时候遇到了一些问题，这个文档再补充说明一下。

在开始使用 Mybatis-Flex 之前，可以先看一下 MyBatis-Flex-Samples 示例，网址为：
[https://gitee.com/mybatis-flex/mybatis-flex-samples](https://gitee.com/mybatis-flex/mybatis-flex-samples)。


## 常见问题 

**问题1：** 使用 Druid 数据源无法启动的问题，主要是因为在数据源的配置中，未添加 `type` 字段的配置：

```yaml 3
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:mysql://127.0.0.1:3306/dbtest
    username: root
    password: 123456
```
第 3 行中的 `type` 字段不能为空，这个并非是 MyBaits-Flex 的问题，而是 Spring 没有内置对 Druid 数据源类型
的主动发现机制。若使用 `hikariCP` 数据源，则可以不配置 `type` 内容。

> 若使用多数据源，或者把数据源配置到 `mybatis-flex.datasource` 下，使用 mybatis-flex 的数据源发现机制，
> 使用 druid 也可以不用配置 type，更多文档参考：[多数据源章节](./multi-datasource.md)。

