# 快速开始

在开始之前，我们假定您已经：

- 熟悉 Java 环境配置及其开发
- 熟悉 关系型 数据库，比如 MySQL

## Hello World

**第 1 步：创建数据库表**

```sql
CREATE TABLE IF NOT EXISTS `tb_account`
(
    `id`        INTEGER PRIMARY KEY auto_increment,
    `user_name` VARCHAR(100),
    `age`       Integer,
    `birthday`  DATETIME
);
```

**第 2 步：创建 java 项目，并添加 Maven/Gradle 依赖**

Maven示例：

```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-core</artifactId>
    <version>1.2.4</version>
</dependency>
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-processor</artifactId>
    <version>1.2.4</version>
    <scope>provided</scope>
</dependency>
```

> 如果配置了annotationProcessorPaths，此处可以省略mybatis-flex-processor的依赖
>
> 参考：[APT 设置章节](../others/apt.md)

Gradle示例：

```groovy
// file: build.gradle
ext {
    mybatis_flex_version = '1.2.4'
}
dependencies {
    implementation("com.mybatis-flex:mybatis-flex-core:${mybatis_flex_version}")
    
    // 启用APT
    annotationProcessor("com.mybatis-flex:mybatis-flex-processor:${mybatis_flex_version}")
}
```

**第 3 步：编写实体类和 Mapper**

```java
@Table("tb_account")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String userName;
    private Integer age;
    private Date birthday;
    
    //getter setter
}
```
- 使用 `@Table("tb_account")` 设置实体类与表名的映射关系
- 使用 `@Id(keyType = KeyType.Auto)` 标识主键为自增

Mapper 接口继承 BaseMapper 接口：
```java
public interface AccountMapper extends BaseMapper<Account> {
    
}
```

> 这部分也可以使用 Mybatis-Flex 的代码生成器来生，功能非常强大的。详情进入：[代码生成器章节](../others/codegen.md) 了解。


**第4步：编译项目，自动生成查询辅助类**

Mybatis-Flex 使用了 APT（Annotation Processing Tool）技术，在项目编译的时，会自动生成辅助操作类。

- Maven 编译： `mvn clean package`
- Gradle 编译： `gradlew classes`


更多信息请参考：[APT 设置章节](../others/apt.md)



**第 5 步：通过 main 方法开始使用 Mybatis-Flex（无 Spring 的场景）**

```java
public class HelloWorld {
    public static void main(String... args) {

        //创建数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/mybatis-flex");
        dataSource.setUsername("username");
        dataSource.setPassword("password");

        //配置数据源
        MybatisFlexBootstrap.getInstance()
                .setDatasource(dataSource)
                .addMapper(AccountMapper.class)
                .start();

        //获取 mapper
        AccountMapper mapper = MybatisFlexBootstrap.getInstance()
                .getMapper(AccountMapper.class);
        
        //示例1：查询 id=1 的数据
        Account account = mapper.selectOneById(1);
        
        
        //示例2：根据 QueryWrapper 查询 id >= 100 的数据列表
        QueryWrapper query = QueryWrapper.create()
                .where(ACCOUNT.ID.ge(100));
        List<Account> accounts = mapper.selectListByQuery(query);
        
        
        //示例3：者使用 Db + Row 查询
        String sql = "select * from tb_account where age > ?";
        List<Row> rows = Db.selectListBySql(sql, 18);
    }
}
```

> 以上的示例中， `ACCOUNT` 为 Mybatis-Flex 通过 APT 自动生成，无需手动编码。更多查看 [APT 文档](../others/apt.md)。
> 
>若觉得 APT 使用不习惯，
> 也可以使用代码生成器来生成。点击 [代码生成器文档](../others/codegen.md) 了解。


## 更多示例

- 示例 1：[Mybatis-Flex 原生（非 Spring）](https://gitee.com/mybatis-flex/mybatis-flex-samples)
- 示例 2：[Mybatis-Flex with Spring](https://gitee.com/mybatis-flex/mybatis-flex-samples)
- 示例 3：[Mybatis-Flex with Spring boot](https://gitee.com/mybatis-flex/mybatis-flex-samples)
- 示例 4：[Db + Row](https://gitee.com/mybatis-flex/mybatis-flex/blob/main/mybatis-flex-test/mybatis-flex-native-test/src/main/java/com/mybatisflex/test/DbTestStarter.java)