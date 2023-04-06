# Mybatis-Flex 的查询和分页

## 基础查询

在 Mybatis-Flex 的 BaseMapper 中，提供了如下的功能用于查询数据库的数据：

- **selectOneById(id)**：根据主键 id 查询数据
- **selectOneByMap(map)**：根据 `map<字段名，值>` 组成的条件查询 1 条数据，若命中多条数据，则只返回第一条数据。
- **selectOneByCondition(condition)**：根据 condition 组成的条件查询 1 条数据，若命中多条数据，则只返回第一条数据。
- **selectOneByQuery(query)**：根据 QueryWrapper 组成的条件查询 1 条数据，若命中多条数据，则只返回第一条数据。
- **selectListByIds(idList)**：根据多个 id 查询，返回多条数据
- **selectListByMap(map)**：根据  `map<字段名，值>` 组成的条件查询数据。
- **selectListByMap(map, count)**：根据  `map<字段名，值>` 组成的条件查询数据，只取前 count 条。
- **selectListByCondition(condition)**：根据 condition 组成的条件查询数据。
- **selectListByCondition(condition, count)**：根据  condition 组成的条件查询数据，只取前 count 条。
- **selectListByQuery(query)**： 根据  QueryWrapper 组成的条件查询数据。
- **selectAll**：查询所有数据。
- **selectCountByCondition**：根据 QueryWrapper 查询数据量。
- **selectCountByQuery**：根据 QueryWrapper 查询数据量。

## 分页查询

在 Mybatis-Flex 的 BaseMapper 中，提供了如下的分页查询功能：

```java
Page<T> paginate(int pageNumber, int pageSize, QueryWrapper queryWrapper);
Page<T> paginate(int pageNumber, int pageSize, int totalRow, QueryWrapper queryWrapper);

Page<T> paginate(int pageNumber, int pageSize, QueryCondition condition);
Page<T> paginate(int pageNumber, int pageSize, int totalRow, QueryCondition condition);
```
- pageNumber： 当前页码，从 1 开始
- pageSize： 每 1 页的数据量
- totalRow： 非必须值，若传入该值，mybatis-flex 则不再去查询总数据量（若传入小于 0 的数值，也会去查询总量）。
- queryWrapper： 查询条件
- QueryCondition： 查询条件

::: tip totalRow 的说明
在一般的分页场景中，只有第一页的时候有必要去查询数据总量，第二页以后是没必要的（因为第一页已经拿到总量了），因此，
第二页的时候，我们可以带入 `totalRow`，这样能提高程序的查询效率。
:::

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


