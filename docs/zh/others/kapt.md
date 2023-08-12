# 在Kotlin中使用注解处理器

> 在Kotlin中想要使`@Table`等注解生效十分简单。只需要使用kapt即可。
>

## 在Gradle中使用

1. 应用Gradle插件：kotlin-kapt

**【Kotlin】**

```kotlin
 plugins {
    kotlin("kapt") version "1.9.0"
}
```

**【Groovy】**

```groovy
plugins {
    id 'org.jetbrains.kotlin.kapt' version '1.9.0'
}
```

2. 在 dependencies 块中使用 kapt 配置添加相应的依赖项
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

## 在Maven中使用

1. 将以下kapt配置插入指定位置。

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

你需要使kapt在compile前工作。将其插入到`kotlin-maven-plugin`中的compile前，
然后将compile的时机改为`process-sources`

```xml
<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <version>${kotlin.version}</version>
    <executions>
        <!--上述代码需插入到此处，compile前-->

        <execution>
            <id>compile</id>
            <!--将此处的phase改为process-sources-->
            <phase>process-sources</phase>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
        <execution>
            <id>test-compile</id>
            <phase>test-compile</phase>
            <goals>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
2. 令kapt在构建前运行

以idea举例
1. 点击maven图标
2. 找到对应项目
3. 点击插件
4. 点击kotlin
5. 右击kotlin:kapt，在选项中点击"**构建前执行**"，以让kapt能够正确的生成代码。
![](../../assets/images/kapt1.png)


> 关于Kapt更详细的说明，请看[Kotlin官网说明](https://book.kotlincn.net/text/kapt.html)
> ，或[Kotlin语言中文站](https://www.kotlincn.net/docs/reference/kapt.html)。
