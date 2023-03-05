# Mybatis-Flex APT 配置

Mybatis-Flex 使用了 APT（Annotation Processing Tool）技术，在项目编译的时候，会自动根据 Entity 类定义的字段帮你生成 "ACCOUNT" 类，
通过开发工具构建项目（如下图），或者执行 maven 编译命令: `mvn clean package` 都可以自动生成。这个原理和 lombok 一致。

![](../assets/images/build_idea.png)


## 关闭 APT 功能

在项目的 resources 目录下添加 `mybatis-flex.porperties` 配置文件，配置内容如下：

```properties
processer.enable = false
```


## APT 代码生成路径

默认在 Entity 类所在的 maven 项目的 `target/generated-sources/annotations` 目录下，
如果 Entity 是 `test/java` 目录下的测试代码，APT 生成的代码则放在 `target/generated-test-sources/test-annotations`
目录下。

如果我们不想让生成的代码放在这些目录，这可以添加如下配置：

```properties
processer.genPath = your-path
```

genPath 可以是绝对路径，也可以是相对路径，如果填写的是相对路径，那么则是相对 Maven 根模块的目录。


## APT 生成的类名和包名

默认情况下， APT 生成的类名为 "Tables"，而包名为 entity 的包添加上 ".table"，假设 Account.java
的包名为 "com.mybatisflex.entity"，那么生成的包名则为 "com.mybatisflex.entity.table"。

添加如下配置，自定义生成的类名和包名。

```properties
processer.package = com.your-package
processer.className = your-class-name
```