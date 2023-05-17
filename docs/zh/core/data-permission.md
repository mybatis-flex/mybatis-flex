# 数据权限

数据权限指的是不同的用户，通过某一个方法去查询的时候，得到的是不同的数据结果集。常见的数据权限有：

- 获取全部数据
- 仅获取本人创建的数据
- 获取当前用户的部门数据
- 获取部门级以下部门的数据
- 获取某个地区的数据
- 等等

这一些，都是通过当前的用户的信息（部门、角色、权限等），查询时，添加特定的条件。在 MyBatis-Flex 中，我们可以使用自定义数据方言 `IDialect` 的方式来实现这一种需求。

以下是示例代码：

```java
public class MyPermissionDialect extends CommonsDialectImpl{

    @Override
    public String forSelectByQuery(QueryWrapper queryWrapper) {
        
        //获取当前用户信息，为 queryWrapper 添加额外的条件
        queryWrapper.and("...");
        
        return supper.buildSelectSql(queryWrapper);
    }
}
```

在项目启动时，通过 `DialectFactory` 注册 `MyPermissionDialect`：

```java
DialectFactory.registerDialect(DbType.MYSQL, new MyPermissionDialect());
```