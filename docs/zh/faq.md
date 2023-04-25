# 常见问题

## 示例中的 AccountMapper 和 "ACCOUNT" 在哪里，报错了。

Mybatis-Flex 使用了 APT 技术，这两个类是自动生成的。
参考：[Mybatis-Flex APT 配置 - Mybatis-Flex 官方网站](./others/02-apt.md)



## SpringBoot 项目，启动报错 Property 'sqlSessionFactory' or 'sqlSessionTempalte' are required

如果当前依赖没有连接池相关依赖，则建议添加 HikariCP 依赖。
如果已经添加了如 druid 依赖，则配置数据源类型 `spring.datasource.type=com.alibaba.druid.pool.DruidDataSource`。

SpringBoot v2.x 添加 hikariCP 的内容如下：

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>4.0.3</version>
</dependency>
```

SpringBoot v3.x 添加 hikariCP 的内容如下：

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.0.1</version>
</dependency>
```

## 整合 Springboot 3 出现 ClassNotFoundException： NestedIOException 的错误

需要排除 flex 中的 mybatis-spring 的依赖，主动添加最新版本的 mybatis-spring 依赖。


```xml 6,7,8,9
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-spring-boot-starter</artifactId>
    <version>${mybatis-flex.version}</version>
    <exclusions>
        <exclusion>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- 添加已适配 springboot 3 的 mybatis-spring 依赖-->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>3.0.1</version>
</dependency>
```



## Spring 下使用 Druid 数据源无法启动

原因是在数据源的配置中，未添加 `type` 字段的配置：

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
> 使用 druid 也可以不用配置 type，更多文档参考：[多数据源章节](./core/11-multi-datasource.md)。

