# Maven 依赖

> 以下的 xml maven 依赖示例中，可能并非最新的 MyBatis-Flex 版本，请自行查看最新版本，并修改版本号。
>
> 建议配置 annotationProcessorPaths，那么可以省略mybatis-flex-processor的依赖
>


1、只用到了 MyBatis，没用到 Spring 的场景：

```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-core</artifactId>
    <version>1.11.5</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.11.5</version>
    <scope>provided</scope>
</dependency>
```

2、用到了 Spring 的场景

```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-spring</artifactId>
    <version>1.11.5</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.11.5</version>
    <scope>provided</scope>
</dependency>
``````

3、用到了 SpringBoot v2.x  的场景

```xml 3
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-spring-boot-starter</artifactId>
    <version>1.11.5</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.11.5</version>
    <scope>provided</scope>
</dependency>
```

4、用到了 SpringBoot v3.x  的场景

```xml 3
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-spring-boot3-starter</artifactId>
    <version>1.11.5</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.11.5</version>
    <scope>provided</scope>
</dependency>
```

5、用到了 SpringBoot v4.x  的场景

```xml 3
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-spring-boot4-starter</artifactId>
    <version>1.11.5</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.11.5</version>
    <scope>provided</scope>
</dependency>
```

6、用到了 Solon  的场景

```xml 3
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-solon-plugin</artifactId>
    <version>1.11.5</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.11.5</version>
    <scope>provided</scope>
</dependency>
```

7、配置 annotationProcessor

   `mybatis-flex-processor`提供APT服务，可以配置到annotationProcessorPaths，配置后，无需在依赖中声明`mybatis-flex-processor`依赖。

   参考：[APT 设置-和 Lombok、Mapstruct 整合](../others/apt.md)

> 在Kotlin中使用时，请参考[在Kotlin中使用注解处理器](../others/kapt.md)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>com.mybatis-flex</groupId>
                <artifactId>mybatis-flex-processor</artifactId>
                <version>1.11.5</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

8、配置依赖管理

MyBatis-Flex 提供了 `mybatis-flex-dependencies` 模块进行依赖管理，只需要在 `<dependencyManagement>` 标签下进行配置就可以了。

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
