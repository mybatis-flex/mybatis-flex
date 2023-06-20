# 常见问题

## 示例中的 AccountMapper 和 "ACCOUNT" 在哪里，报错了。

MyBatis-Flex 使用了 APT 技术，这两个类是自动生成的。
参考：[MyBatis-Flex APT 配置 - MyBatis-Flex 官方网站](./others/apt.md)

## 阿里镜像找不到依赖？

```txt
Could not find artifact com.mybatis-flex:mybatis-flex-spring-boot-starter:pom:1.4.0 
in alimaven (http://maven.aliyun.com/nexus/content/groups/public/)
```

这个是因为目前阿里云镜像正在维护，可以替换为`腾讯云`或者`华为云`的镜像源，更改 Maven 安装目录下的 `settings.xml` 文件，
添加如下配置：

腾讯云：

```xml
<mirror>
    <id>tencent-cloud</id>
    <mirrorOf>*</mirrorOf>
    <name>tencent-cloud</name>
    <url>https://mirrors.cloud.tencent.com/nexus/repository/maven-public/</url>
</mirror>
```

华为云：

```xml
<mirror>
    <id>huawei-cloud</id>
    <mirrorOf>*</mirrorOf>
    <name>huawei-cloud</name>
    <url>https://mirrors.huaweicloud.com/repository/maven/</url>
</mirror>
```

## 启动出错？目前社区反馈有如下几个错误原因

- 1、添加了错误的数据源依赖版本，比如 SpringBoot v2.x 使用 HikariCP 时，应该是 HikariCP 的 4.x 版本。而 SpringBoot v3.x 应该使用
  HikariCP 的 5.x 版本。
- 2、主动添加了 MyBatis 或者 `mybatis-spring-boot-starter` 的依赖，导致版本不匹配。使用 SpringBoot
  的情况下，应该引用 `mybatis-flex-spring-boot-starter` 就可以了，不需要再添加其他 MyBatis 依赖。
- 3、使用了 `druid-spring-boot-starter` 依赖，导致 flex 的 DataSource 无法被接管。应该使用 `druid`
  就可以了，不要用 `druid-spring-boot-starter`。
- 4、使用了 `MyBatis-Plus` 或者 `pagehelper-spring-boot-starter` 而被这些框架优先初始化 MyBatis， MyBatis-Flex 未得到初始化。

## SpringBoot 项目，启动报错 Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required

如果当前依赖没有连接池相关依赖，则建议添加 HikariCP 依赖。

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

> 如果使用的是 druid 数据库连接池，则需要添加数据源类型的配置 `spring.datasource.type=com.alibaba.druid.pool.DruidDataSource`。

## java.sql.SQLException: No value specified for parameter x
出现这个问题，原因是 MyBatis-Flex 未能正常启动，SQL 执行没有经过 MyBatis-Flex 导致的。其直接是因为和其他第三方增强框架整合使用了，
比如和 MyBatis-Plus、或者 PageHelper 等整合造成的。

如何与 PageHelper 整合可以点击 [这里](/zh/faq.html#%E4%B8%8E-pagehelper-%E9%9B%86%E6%88%90%E5%87%BA%E7%8E%B0%E9%94%99%E8%AF%AF) 查看


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

```yaml:line-numbers 3
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:mysql://127.0.0.1:3306/dbtest
    username: root
    password: 123456
```
第 3 行中的 `type` 字段不能为空，这个并非是 MyBaits-Flex 的问题，而是 Spring 没有内置对 Druid 数据源类型
的主动发现机制。若使用 `hikariCP` 数据源，则可以不配置 `type` 内容。

> 若把数据源配置到 `mybatis-flex.datasource` 下，使用 mybatis-flex 的数据源发现机制，
> 使用 druid 则可以不用配置 type，更多文档参考：[多数据源章节](./core/multi-datasource.md)。

## 使用 `druid-spring-boot-starter`，出现无法启动 或者 数据源识别错误的问题

在 MyBatis-Flex 不能使用 "druid-spring-boot-starter" 依赖，只能使用 "druid" 。

```xml
 <dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>${druid.version}</version>
</dependency>
```

需要把以上的依赖，修改如下：

```xml
 <dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>${druid.version}</version>
</dependency>
```

> 原因是：druid-spring-boot-starter 内的 DruidDataSourceAutoConfigure 会去自动加载 spring.datasource 下的配置，当使用 MyBatis-Flex 的多数据源时，
> 这个配置已经不存在了。

## 与 PageHelper 集成出现错误

在社区中，一些老的项目在使用到了开源项目 PageHelper，用于解决 xml 的分页问题，在和 MyBatis-flex 整合使用中，出现了一些错误，
需要把 `pagehelper-spring-boot-starter` 依赖替换为 `pagehelper`;


```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>版本号</version>
</dependency>
```
需要把以上依赖替换如下：

```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper</artifactId>
    <version>版本号</version>
</dependency>
```
解决方案：https://gitee.com/mybatis-flex/mybatis-flex/issues/I71AUE




## 如何自定义 MyBatis 的 Configuration?

1、在不使用 Spring 的场景下：

```java
FlexConfiguration configuration = new FlexConfiguration();
MybatisFlexBootstrap.getInstance().setConfiguration(configuration);
```

2、在使用 Spring-Boot 的场景下：

```java
@Configuration
public class MyConfigurationCustomizer implements ConfigurationCustomizer {

    @Override
    public void customize(FlexConfiguration configuration) {
        // 在这里为 configuration 进行配置
        configuration.setLogImpl(StdOutImpl.class);
    }
}
```

3、只使用 Spring（不使用 Spring-Boot ） 的场景：

```java
@Bean
public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
    SqlSessionFactoryBean factoryBean = new FlexSqlSessionFactoryBean();
    factoryBean.setDataSource(dataSource);
    
    // 在这里配置
    FlexConfiguration configuration = new FlexConfiguration();
    configuration.setLogImpl(StdOutImpl.class);
    
    factoryBean.setConfiguration(configuration);
    return factoryBean.getObject();
}
```