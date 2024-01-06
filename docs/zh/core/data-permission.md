# 数据权限

数据权限指的是不同的用户，通过某一个方法去查询的时候，得到的是不同的数据结果集。常见的数据权限有：

- 获取全部数据
- 仅获取本人创建的数据
- 获取当前用户的部门数据
- 获取部门级以下部门的数据
- 获取某个地区的数据
- 等等

这一些，都是通过当前的用户的信息（部门、角色、权限等），查询时，添加特定的条件。在 MyBatis-Flex 中，我们可以通过 2 种方式来实现这一种需求。

## 方式1：使用自定义数据方言 `IDialect`

在自定义方言中，重写 `forSelectByQuery` 方法，这个方法是用于构建返回根据 `QueryWrapper` 查询的方法， 以下是示例代码：

```java
public class MyPermissionDialect extends CommonsDialectImpl {

    @Override
    public String forSelectByQuery(QueryWrapper queryWrapper) {

        //获取当前用户信息，为 queryWrapper 添加额外的条件
        queryWrapper.and("...");

        return super.buildSelectSql(queryWrapper);
    }
}
```

特别的，如果您使用的是 `Oracle` 数据库，需要继承 `OracleDialect` 类，示例：

```java
public class MyOraclePermissionDialect extends OracleDialect {

    // 这个构造器只有在 Oracle 12C 的版本下才需要添加。
    // public MyOraclePermissionDialect() {
    //    super(LimitOffsetProcessor.DERBY);
    // }

}
```

在项目启动时，通过 `DialectFactory` 注册 `MyPermissionDialect`：

```java
DialectFactory.registerDialect(DbType.MYSQL,new MyPermissionDialect());
```

**常见问题1：通过重写 `IDialect` 后，所有的查询都添加了条件，但是有些表不需要条件如何做？**

>答：可以通过 CPI 获取 QueryWrapper 查询了哪些表，然后进行动态处理。例如 `List<QueryTable> tables = CPI.getQueryTables(queryWrapper)`，然后进一步对
> `tables` 进行验证是否需要添加数据权限。

## 方式2：重写 IService 的查询方法

在一般的应用中，查询是通过 Service 进行的，MyBatis-Flex 提供了 `IService` 接口及其默认的 `ServiceImpl` 实现类。

我们可以通过构建自己的 `IServiceImpl` 来实现这一种需求，例如：

```java
public class MyServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {

    @Autowired
    protected M mapper;

    @Override
    public BaseMapper<T> getMapper() {
        return mapper;
    }


    @Override
    public List<T> list(QueryWrapper query) {
        //获取当前用户信息，为 queryWrapper 添加额外的条件
        return IService.super.list(query);
    }
}
```
当然，在 `IService` 中，除了 `list` 方法以外，还有其他的查询方法，可能也需要复写。

## 方式3：使用自定义数据方言 `IDialect`
在自定义方言中，重写 `prepareAuth` 方法，根据自己的需求对 `QueryWrapper`、`sql`语句进行处理，添加额外条件， 以下是示例代码：
```java
public class AuthDialectImpl extends CommonsDialectImpl {

    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (queryTables == null || queryTables.isEmpty()) {
            return;
        }
        for (QueryTable queryTable : queryTables) {
            if (PROJECT.getTableName().equals(queryTable.getName())) {
                queryWrapper.and(PROJECT.INSERT_USER_ID.eq(1));
            }
        }
        super.prepareAuth(queryWrapper, operateType);
    }

    @Override
    public void prepareAuth(String schema, String tableName, StringBuilder sql, OperateType operateType) {
        if (PROJECT.getTableName().equals(tableName)) {
            sql.append(AND).append(wrap("insert_user_id")).append(EQUALS).append(1);
        }
        super.prepareAuth(schema, tableName, sql, operateType);
    }

    @Override
    public void prepareAuth(TableInfo tableInfo, StringBuilder sql, OperateType operateType) {
        if (PROJECT.getTableName().equals(tableInfo.getTableName())) {
            sql.append(AND).append(wrap("insert_user_id")).append(EQUALS).append(1);
        }
        super.prepareAuth(tableInfo, sql, operateType);
    }
}
```
对`QueryWrapper`的表做筛选可参考 **方式1**
在项目启动时通过 `DialectFactory` 注册 `AuthDialectImpl`，以spring boot项目为例：

```java
@Configuration
public class MybatisFlexConfig implements MyBatisFlexCustomizer {

    @Override
    public void customize(FlexGlobalConfig flexGlobalConfig) {
        

        // 注册查询权限监听方言
        DialectFactory.registerDialect(DbType.MYSQL, new AuthDialectImpl());
    }
}
```
单元测试请参考源码中的`com.mybatisflex.coretest.AuthTest`类
