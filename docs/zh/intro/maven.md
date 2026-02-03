# Maven 依赖

本文档介绍如何为 MyBatis-Flex 配置 Maven 依赖关系。

> [!TiP] Tip
> 在示例中，MyBatis-Flex 的版本号可能并非最新版，请自行查看[最新版本](/zh/changes.html)，并修改版本号。

## 配置 `dependency`

> [!TIP] Tip
> `mybatis-flex-processor` 依赖提供 APT 服务，**建议**将其配置到 `<annotationProcessorPaths>`,
> 配置后，无需在依赖 `<dependencies>` 标签中声明 `mybatis-flex-processor` 依赖。

### Java Native

只用了 `MyBatis` ，没用 `Spring` 的场景

```xml 3
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-core</artifactId>
    <version>1.11.6</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.11.6</version>
    <scope>provided</scope>
</dependency>
```

### Spring

使用 `Spring` ，但没用 `Spring Boot` 的场景

```xml 3
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-spring</artifactId>
    <version>1.11.6</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.11.6</version>
    <scope>provided</scope>
</dependency>
```

### Spring Boot v2.x

使用 `Spring Boot`v2.x 的场景

```xml 3
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-spring-boot-starter</artifactId>
    <version>1.11.6</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.11.6</version>
    <scope>provided</scope>
</dependency>
```

### Spring Boot v3.x

使用 `Spring Boot`v3.x 的场景

```xml 3
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-spring-boot3-starter</artifactId>
    <version>1.11.6</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.11.6</version>
    <scope>provided</scope>
</dependency>
```

### Spring Boot v4.x

使用 `Spring Boot`v4.x 的场景

```xml 3
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-spring-boot4-starter</artifactId>
    <version>1.11.6</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.11.6</version>
    <scope>provided</scope>
</dependency>
```

> [!WARNING] Tip
> Spring Boot v4.x 版本，因 `stater` 提供的 `spring-boot-autoconfigure` ，不再默认包含 `jdbc`(datasource) 的 autoconfigure，需要添加依赖 `spring-boot-starter-jdbc`
>
> > 参考：[Modularizing Spring Boot](https://spring.io/blog/2025/10/28/modularizing-spring-boot)

### Solon

使用 `Solon` 的场景

```xml 3
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-solon-plugin</artifactId>
    <version>1.11.6</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.11.6</version>
    <scope>provided</scope>
</dependency>
```

## 配置 `annotationProcessor`

`mybatis-flex-processor` 依赖提供 APT 服务，**建议**将其配置到 `<annotationProcessorPaths>`,
配置后，无需在依赖 `<dependencies>` 中声明 `mybatis-flex-processor` 依赖。

> [APT 设置-和 Lombok、Mapstruct 整合](../others/apt.md)

> 如果在 Kotlin 中使用，请参考：[在 Kotlin 中使用注解处理器](../others/kapt.md)

```xml 8-10
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>com.mybatis-flex</groupId>
                <artifactId>mybatis-flex-processor</artifactId>
                <version>1.11.6</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## 配置 `dependencyManagement`

**可选**，统一管理 Mybatis-flex 依赖版本

在 `<dependencyManagement>` 标签中引入 `mybatis-flex-dependencies`，可以简化 Mybatis-flex 版本管理。
引入后，在 `<dependency>` 标签中使用时可以不指定 `<version>`

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.mybatis-flex</groupId>
            <artifactId>mybatis-flex-dependencies</artifactId>
            <version>${mybatis-flex.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

在使用时就可以不指定 `<version>` 标签了，例如：

```xml
<dependencies>
    <dependency>
        <groupId>com.mybatis-flex</groupId>
        <artifactId>mybatis-flex-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mybatis-flex</groupId>
        <artifactId>mybatis-flex-codegen</artifactId>
    </dependency>
</dependencies>
```
