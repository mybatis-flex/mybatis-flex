# Gradle 依赖

> 以下的 gradle 依赖示例中，可能并非最新的 MyBatis-Flex 版本，请自行查看最新版本，并修改版本号。

> 建议配置 annotationProcessor，那么可以省略 `mybatis-flex-processor` 的依赖。

1、只用到了 MyBatis，没用到 Spring 的场景：

**【Kotlin】**

```kotlin
dependencies {
    implementation("com.mybatis-flex:mybatis-flex-core:1.11.4")
}
```

**【Groovy】**

```groovy
dependencies {
    implementation 'com.mybatis-flex:mybatis-flex-core:1.11.4'
}
```

2、用到了 Spring 的场景

**【Kotlin】**

```kotlin
dependencies {
    implementation("com.mybatis-flex:mybatis-flex-spring:1.11.4")
}
```

**【Groovy】**

```groovy
dependencies {
    implementation 'com.mybatis-flex:mybatis-flex-spring:1.11.4'
}
```

3、用到了 Spring Boot 的场景

**【Kotlin】**

```kotlin
dependencies {
    implementation("com.mybatis-flex:mybatis-flex-spring-boot-starter:1.11.4")
}
```

**【Groovy】**

```groovy
dependencies {
    implementation 'com.mybatis-flex:mybatis-flex-spring-boot-starter:1.11.4'
}
```


4、用到了 Solon 的场景

**【Kotlin】**

```kotlin
dependencies {
    implementation("com.mybatis-flex:mybatis-flex-solon-plugin:1.11.4")
}
```

**【Groovy】**

```groovy
dependencies {
    implementation 'com.mybatis-flex:mybatis-flex-solon-plugin:1.11.4'
}
```



5、配置 annotationProcessor

由 `mybatis-flex-processor` 提供APT服务。

参考：[APT 设置-和 Lombok、Mapstruct 整合](../others/apt.md)

> 在Kotlin中使用时，请参考[在Kotlin中使用注解处理器](../others/kapt.md)

**【Kotlin】**

```kotlin
dependencies {
    annotationProcessor("com.mybatis-flex:mybatis-flex-processor:1.11.4")
}
```

**【Groovy】**

```groovy
dependencies {
    annotationProcessor 'com.mybatis-flex:mybatis-flex-processor:1.11.4'
}
```
