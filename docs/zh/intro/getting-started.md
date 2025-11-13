# 快速开始

在开始之前，我们假定您已经：

- 熟悉 Java 环境配置及其开发
- 熟悉 关系型 数据库，比如 MySQL
- 熟悉 Spring Boot 或 Solon 等相关框架
- 熟悉 Java 构建工具，比如 Maven

> 当前章节涉及到的源码已经全部上传到：https://gitee.com/Suomm/mybatis-flex-test ，在开始之前，
> 您也可以先下载到本地，导入到 idea 开发工具后，在继续看文档。



## 本章节视频教程


<iframe width="100%" height="400px" src="//player.bilibili.com/player.html?aid=955526987&bvid=BV1yW4y1Z74j&cid=1187300793&page=1&autoplay=no" scrolling="no" border="0" frameborder="no" framespacing="0" allowfullscreen="true"> </iframe>

> MyBatis-Flex 视频系列：https://www.bilibili.com/video/BV1yW4y1Z74j



## Hello World 文档


**第 1 步：创建数据库表**

```sql
CREATE TABLE IF NOT EXISTS `tb_account`
(
    `id`        INTEGER PRIMARY KEY auto_increment,
    `user_name` VARCHAR(100),
    `age`       INTEGER,
    `birthday`  DATETIME
);

INSERT INTO tb_account(id, user_name, age, birthday)
VALUES (1, '张三', 18, '2020-01-11'),
       (2, '李四', 19, '2021-03-21');
```

**第 2 步：创建 Spring Boot 项目，并添加 Maven 依赖**

::: tip
可以使用 [Spring Initializer](https://start.spring.io/) 快速初始化一个 Spring Boot 工程。
:::

需要添加的 Maven 主要依赖示例：

```xml
<dependencies>
    <dependency>
        <groupId>com.mybatis-flex</groupId>
        <artifactId>mybatis-flex-spring-boot-starter</artifactId>
        <version>1.11.4</version>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
    </dependency>
    <!-- for test only -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**注意**： 如果您当前使用的是 SpringBoot v3.x 版本，需要把依赖 `mybatis-flex-spring-boot-starter` 修改为：`mybatis-flex-spring-boot3-starter`,
如下代码所示：

```xml 4
<dependencies>
    <dependency>
        <groupId>com.mybatis-flex</groupId>
        <artifactId>mybatis-flex-spring-boot3-starter</artifactId>
        <version>1.11.4</version>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
    </dependency>
    <!-- for test only -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```


**第 3 步：对 Spring Boot 项目进行配置**

在 application.yml 中配置数据源：

```yaml
# DataSource Config
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/flex_test
    username: root
    password: 12345678
```

在 Spring Boot 启动类中添加 `@MapperScan` 注解，扫描 Mapper 文件夹：

```java
@SpringBootApplication
@MapperScan("com.mybatisflex.test.mapper")
public class MybatisFlexTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisFlexTestApplication.class, args);
    }

}
```

**第 4 步：编写实体类和 Mapper 接口**

这里使用了 [Lombok](https://www.projectlombok.org/) 来简化代码。

```java
@Data
@Table("tb_account")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String userName;
    private Integer age;
    private Date birthday;

}
```

- 使用 `@Table("tb_account")` 设置实体类与表名的映射关系
- 使用 `@Id(keyType = KeyType.Auto)` 标识主键为自增

Mapper 接口继承 BaseMapper 接口：

```java
public interface AccountMapper extends BaseMapper<Account> {

}
```

> 这部分也可以使用 MyBatis-Flex 的代码生成器来生，功能非常强大的。详情进入：[代码生成器章节](../others/codegen.md) 了解。


**第 5 步：开始使用**

添加测试类，进行功能测试：

```java
import static com.mybatisflex.test.entity.table.AccountTableDef.ACCOUNT;

@SpringBootTest
class MybatisFlexTestApplicationTests {

    @Autowired
    private AccountMapper accountMapper;

    @Test
    void contextLoads() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .where(ACCOUNT.AGE.eq(18));
        Account account = accountMapper.selectOneByQuery(queryWrapper);
        System.out.println(account);
    }

}
```

控制台输出：

```txt
Account(id=1, userName=张三, age=18, birthday=Sat Jan 11 00:00:00 CST 2020)
```

> 以上的 [示例](https://gitee.com/Suomm/mybatis-flex-test) 中， `ACCOUNT` 为 MyBatis-Flex 通过 APT
> 自动生成，只需通过静态导入即可，无需手动编码。更多查看 [APT 文档](../others/apt.md)。
>
> 若觉得 APT 使用不习惯，也可以使用代码生成器来生成。点击 [代码生成器文档](../others/codegen.md) 了解。

## 更多示例

- 示例 1：[MyBatis-Flex 原生（非 Spring）](https://gitee.com/mybatis-flex/mybatis-flex-samples)
- 示例 2：[MyBatis-Flex with Spring](https://gitee.com/mybatis-flex/mybatis-flex-samples)
- 示例 3：[MyBatis-Flex with Spring boot](https://gitee.com/mybatis-flex/mybatis-flex-samples)
- 示例 4：[Db + Row](https://gitee.com/mybatis-flex/mybatis-flex/blob/main/mybatis-flex-test/mybatis-flex-native-test/src/main/java/com/mybatisflex/test/DbTestStarter.java)
