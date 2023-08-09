# 在 Kotlin 中使用注解处理器

> 在 Kotlin 中想要使 `@Table` 等注解生效十分简单。只需要使用 KAPT 即可。

## 在 Gradle 中使用

1、应用 Gradle 插件：`kotlin-kapt`

**【Kotlin】**

```kotlin
 plugins {
    kotlin("kapt") version "1.9.0"
}
```

**【Groovy】**

```groovy
plugins {
    id "org.jetbrains.kotlin.kapt" version "1.9.0"
}
```

2、在 dependencies 块中使用 KAPT 配置添加相应的依赖项


**【Kotlin】**

```kotlin
dependencies {
    kapt("org.springframework.boot:spring-boot-configuration-processor:1.5.6")
}
```

**【Groovy】**
```groovy
dependencies {
    kapt 'org.springframework.boot:spring-boot-configuration-processor:1.5.6'
}
```

## 在 Maven 中使用

在 compile 之前在 kotlin-maven-plugin 中添加 kapt 目标的执行：

```xml
<execution>
    <id>kapt</id>
    <goals>
        <goal>kapt</goal>
    </goals>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>com.mybatis-flex</groupId>
                <artifactId>mybatis-flex-processor</artifactId>
                <version>1.5.6</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</execution>
```

> 关于 KAPT 更详细的说明，请看 [Kotlin 官网说明](https://book.kotlincn.net/text/kapt.html)，或 [Kotlin 语言中文站](https://www.kotlincn.net/docs/reference/kapt.html)。
