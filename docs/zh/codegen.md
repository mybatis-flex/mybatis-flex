# Mybatis-Flex 代码生成器

在 mybatis-flex 的模块 `mybatis-flex-codegen` 中，提供了可以通过数据库表，生成 Entity 类和 Mapper 类的功能。当我们把数据库表设计完成
后可以使用其快速生成 Entity 和 Mapper 的 java 类。

在使用前先添加 `mybatis-flex-codegen` 的 Maven 依赖：

```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-codegen</artifactId>
    <version>1.1.9</version>
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
        //globalConfig.setMapperGenerateEnable(true);

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

注意：由于 MyBatis-Flex 的 APT 功能会自动帮我们生成了 Mapper 的 Java 类，如果我们在代码生成器中选择生成 Mapper，
则建议把 APT 的 Mapper 生成功能给关闭掉，否则系统中会存在两份一样功能的 Mapper。

关闭 APT 的 Mapper 类文件生成，请参考：[APT 设置章节](./apt.md)

## 全局配置 GlobalConfig 

GlobalConfig 支持更多的配置如下：

```java

public class GlobalConfig {

    //代码生成目录
    private String sourceDir;

    //entity 的包名
    private String entityPackage;

    //entity 类的前缀
    private String entityClassPrefix;

    //entity 类的后缀
    private String entityClassSuffix;

    //entity 类的父类，可以自定义一些 BaseEntity 类
    private Class<?> entitySupperClass;

    //entity 默认实现的接口
    private Class<?>[] entityInterfaces = {Serializable.class};

    //entity 是否使用 Lombok
    private boolean entityWithLombok = false;

    //是否生成 mapper 类
    private boolean mapperGenerateEnable = false;

    //是否覆盖已经存在的 mapper
    private boolean mapperOverwriteEnable = false;

    //mapper 类的前缀
    private String mapperClassPrefix;

    //mapper 类的后缀
    private String mapperClassSuffix = "Mapper";

    //mapper 的包名
    private String mapperPackage;

    //自定义 mapper 的父类
    private Class<?> mapperSupperClass = BaseMapper.class;

    //数据库表前缀，多个前缀用英文逗号（,） 隔开
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

    // 是否启用ATP生成Mapper
    private Boolean mapperGenerateEnable = Boolean.TRUE;
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
    
    // 是否是租户列
    private Boolean tenantId;
}
```

## 自定义属性类型

Mybatis-Flex 内置了一个名为：`JdbcTypeMapping` 的 java 类，我们可以用其配置映射 Jdbc 驱动的数据类型为自定义的
数据类型，在开始生成代码之前，可以先调用其进行配置，例如：

```java
JdbcTypeMapping.registerMapping(LocalDateTime.class, Date.class);
```
那么，当我们生成代码的时候，发现 JDBC 驱动的数据类型为 `LocalDateTime`，则 Entity 对应的属性类型为 `Date`。

## 自定义代码模板

通过 `GlobalConfig`（全局配置）的 `setTemplateEngine()` 方法，可以配置自己的模板引擎以及模板，以下是内置的 `EnjoyTemplate` 的代码示例：

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