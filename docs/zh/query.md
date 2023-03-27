# Mybatis-Flex 的查询和分页

## 基础查询

在 Mybatis-Flex 的 BaseMapper 中，提供了如下的功能用于查询数据库的数据：

- **selectOneById(id)**：根据主键 id 查询数据
- **selectOneByMap(map)**：根据 `map<字段名，值>` 组成的条件查询 1 条数据，若命中多条数据，则只返回第一条数据。
- **selectOneByQuery(query)**：根据 QueryWrapper 组成的条件查询 1 条数据，若命中多条数据，则只返回第一条数据。
- **selectListByIds(idList)**：根据多个 id 查询，返回多条数据
- **selectListByMap(map)**：根据  `map<字段名，值>` 组成的条件查询数据。
- **selectListByMap(map, count)**：根据  `map<字段名，值>` 组成的条件查询数据，只取前 count 条。
- **selectListByQuery(query)**： 根据  QueryWrapper 组成的条件查询数据。
- **selectAll**：查询所有数据。
- **selectCountByQuery**：根据 QueryWrapper 查询数据量。

## 分页查询

在 Mybatis-Flex 的 BaseMapper 中，提供了如下的分页查询功能：

```java
Page<T> paginate(int pageNumber, int pageSize, QueryWrapper queryWrapper);
```
- pageNumber： 当前页码，从 1 开始
- pageSize： 每 1 页的数据量
- queryWrapper： 查询条件

paginate 的返回值为 Page 对象，Page 类的定义如下：

```java
public class Page<T> implements Serializable {
    private List<T> list;                // list result of this page
    private int pageNumber;              // page number
    private int pageSize;                // result amount of this page
    private long totalPage;              // total page
    private long totalRow;               // total row
}
```

在 Page 的定义中，我们知道：通过 `paginate` 方法去查询数据的时候，除了数据列表以外，还查询的数据的总量，才能构造出 `Page` 对象。


在一般的分页场景中，只有第一页的时候有必要去查询数据总量，第二页以后是没必要的（因为第一页已经拿到总量了），因此，Mybatis-Flex 的分页查询还提供了另一个方法：

```java
Page<T> paginate(Page<T> page, QueryWrapper queryWrapper);
```

这个方法可以直接传入 数据的总量 `totalPage`，示例如下：

```java
// 多一个 totalPage 参数
Page<T> page = new Page<>(pageNumber, pageSize, totalPage);
Page<T> resultPage = paginate(page, queryWrapper);
```
当构造的 `page` 对象已经有 totalPage 后，再通过 `paginate(page, queryWrapper)` 方法去查询，则不会再查询数据总量，从提高了性能。

> 只有 totalRow 小于 0 的时候才会去查询总量。

