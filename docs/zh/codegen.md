# Mybatis-Flex 代码生成器

在 mybatis-flex 的模块 `mybatis-flex-codegen` 中，提供了可以通过数据库表，生成 Entity 类和 Mapper 类的功能。当我们把数据库表设计完成
后可以使用其快速生成 Entity 和 Mapper 的 java 类。

在使用前先添加 `mybatis-flex-codegen` 的 Maven 依赖：

```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-codegen</artifactId>
    <version>1.0.4</version>
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
        globalConfig.setEntityPackage("com.test.entity");

        //设置表前缀
        //globalConfig.setTablePrefix("tb_");
        
        //设置 entity 是否使用 Lombok
        //globalConfig.setEntityWithLombok(true);

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

## 全局配置 GlobalConfig 

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

## 表配置 TableConfig 

TableConfig 支持的配置如下：

```java
public class TableConfig {

    private String tableName;

    /**
     * 数据库的 schema
     */
    private String schema;

    /**
     * 默认为 驼峰属性 转换为 下划线字段
     */
    private Boolean camelToUnderline;


    private Class<? extends InsertListener> insertListenerClass;


    private Class<? extends UpdateListener> updateListenerClass;
}
```

## 列配置 ColumnConfig

ColumnConfig 支持的配置如下：

```java
public class ColumnConfig implements Serializable {

    private String onInsertValue;
    private String onUpdateValue;

    private Boolean isLarge;
    private Boolean isLogicDelete;
    private Boolean version;

    private JdbcType jdbcType;
    private Class<? extends TypeHandler> typeHandler;

    private String mask;

    private boolean isPrimaryKey = false;
    private KeyType keyType;
    private String keyValue;
    private Boolean keyBefore;
}
```

## 常见问题

**1、如何自定义代码模板**
> 答：通过 `globalConfig.setTemplateEngine()` 配置自己的模板引擎即可，以下是内置的 `EnjoyTemplate` 的代码示例：

```java
public class EnjoyTemplate implements ITemplate {

    private Engine engine;

    public EnjoyTemplate() {
        engine = Engine.create("mybatis-flex", engine -> {
            engine.setToClassPathSourceFactory();
            engine.addSharedMethod(StringUtil.class);
        });
    }

    /**
     * 生成 entity 的方法实现
     */
    @Override
    public void generateEntity(GlobalConfig globalConfig, Table table, File entityJavaFile) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("globalConfig", globalConfig);
        params.put("table", table);


        FileOutputStream fileOutputStream = new FileOutputStream(entityJavaFile);
        engine.getTemplate("/templates/enjoy/entity.tpl").render(params, fileOutputStream);
    }


    /**
     * 生成 mapper 的方法实现
     */
    @Override
    public void generateMapper(GlobalConfig globalConfig, Table table, File mapperJavaFile) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("globalConfig", globalConfig);
        params.put("table", table);


        FileOutputStream fileOutputStream = new FileOutputStream(mapperJavaFile);
        engine.getTemplate("/templates/enjoy/mapper.tpl").render(params, fileOutputStream);
    }
}

```