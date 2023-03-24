# Mybatis-Flex 代码生成器

在 mybatis-flex 的模块 `mybatis-flex-codegen` 中，提供了可以通过数据库表，生成 Entity 类和 Mapper 类的功能。当我们把数据库表设计完成
后可以使用其快速生成 Entity 和 Mapper 的 java 类。

在使用前先添加 `mybatis-flex-codegen` 的 Maven 依赖：

```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-codegen</artifactId>
    <version>1.0.3</version>
</dependency>
```

同时需要添加数据源的 Maven 依赖和 jdbc 驱动依赖：

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>4.0.3</version>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.32</version>
</dependency>
```

然后，编写一个任意带有 main 方法的类，如下所示：

```java
public class Codegen {
    
    public static void main(String[] args) {
        
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/your-database?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("******");

        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置只生成哪些表
        globalConfig.addGenerateTable("account", "account_session");

        //设置 entity 的包名
        globalConfig.setEntityPackage("com.test.test");

        //是否生成 mapper 类，默认为 false
        globalConfig.setMapperGenerateEnable(true);

        //设置 mapper 类的包名
        globalConfig.setMapperPackage("com.test.mapper");

        //可以单独配置某个列
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName("tenant_id");
        columnConfig.setLarge(true);
        columnConfig.setVersion(true);
        globalConfig.addColumnConfig("account", columnConfig);

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }
}
```

GlobalConfig 支持更多的配置如下：

```java

public class GlobalConfig {

    //代码生成目录
    private String sourceDir;

    //entity 的包名
    private String entityPackage;

    //entity 是否使用 Lombok
    private Boolean entityWithLombok = false;

    //是否生成 mapper 类
    private boolean mapperGenerateEnable = false;

    //mapper 的包名
    private String mapperPackage;

    //数据库表前缀
    private String tablePrefix;

    //逻辑删除的默认字段名称
    private String logicDeleteColumn;

    //乐观锁的字段名称
    private String versionColumn;

    //是否生成视图映射
    private boolean generateForView = false;

    //单独为某张表添加独立的配置
    private Map<String, TableConfig> tableConfigMap;

    //设置某个列的全局配置
    private Map<String, ColumnConfig> defaultColumnConfigMap;

    //生成那些表，白名单
    private Set<String> generateTables;

    //不生成那些表，黑名单
    private Set<String> unGenerateTables;

    //使用哪个模板引擎来生成代码
    protected ITemplate templateEngine;

}
```