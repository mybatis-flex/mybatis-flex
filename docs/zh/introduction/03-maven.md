# Maven 依赖

> 以下的 xml maven 依赖示例中，可能并非最新的 Mybatis-Flex 版本，请自行查看最新版本，并修改版本号。


1、只用到了 Mybatis，没用到 Spring 的场景：

```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-core</artifactId>
    <version>1.1.9</version>
</dependency>
```

2、用到了 Spring 的场景

```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-spring</artifactId>
    <version>1.1.9</version>
</dependency>
``````

3、用到了 Spring Boot 的场景

```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-spring-boot-starter</artifactId>
    <version>1.1.9</version>
</dependency>
```