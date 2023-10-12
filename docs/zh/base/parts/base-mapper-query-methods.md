- **`selectOneById(id)`**：根据主键查询数据。
- **`selectOneByEntityId(entity)`**：根据实体主键查询数据，便于对复合主键实体类的查询。
- **`selectOneByMap(whereConditions)`**：根据 Map 构建的条件来查询数据。
- **`selectOneByCondition(whereConditions)`**：根据查询条件查询数据。
- **`selectOneByQuery(queryWrapper)`**：根据查询条件来查询 1 条数据。
- **`selectOneByQueryAs(queryWrapper, asType)`**：根据查询条件来查询 1 条数据。
- **`selectOneWithRelationsByMap(whereConditions)`**：根据 Map 构建的条件来查询 1 条数据。
- **`selectOneWithRelationsByCondition(whereConditions)`**：根据查询条件查询 1 条数据。
- **`selectOneWithRelationsByQuery(queryWrapper)`**：根据查询条件来查询 1 条数据。
- **`selectOneWithRelationsByQueryAs(queryWrapper, asType)`**：根据查询条件来查询 1 条数据。
- **`selectListByIds(ids)`**：根据多个主键来查询多条数据。
- **`selectListByMap(whereConditions)`**：根据 Map 来构建查询条件，查询多条数据。
- **`selectListByMap(whereConditions, count)`**：根据 Map 来构建查询条件，查询多条数据。
- **`selectListByCondition(whereConditions)`**：根据查询条件查询多条数据。
- **`selectListByCondition(whereConditions, count)`**：根据查询条件查询多条数据。
- **`selectListByQuery(queryWrapper)`**：根据查询条件查询数据列表。
- **`selectListByQuery(queryWrapper, consumers)`**：根据查询条件查询数据列表。
- **`selectCursorByQuery(queryWrapper)`**：根据查询条件查询游标数据，该方法必须在事务中才能正常使用，非事务下无法获取数据。
- **`selectRowsByQuery(queryWrapper)`**：根据查询条件查询 Row 数据。
- **`selectListByQueryAs(queryWrapper, asType)`**：根据查询条件查询数据列表，要求返回的数据为 asType。这种场景一般用在 left
  join 时，有多出了实体类本身的字段内容，可以转换为 dto、vo 等场景。
- **`selectListByQueryAs(queryWrapper, asType, consumers)`**：根据查询条件查询数据列表，要求返回的数据为 asType 类型。
- **`selectListWithRelationsByQuery(queryWrapper)`**：查询实体类及其 Relation 注解字段。
- **`selectListWithRelationsByQueryAs(queryWrapper, asType)`**：查询实体类及其 Relation 注解字段。
- **`selectListWithRelationsByQueryAs(queryWrapper, asType, consumers)`**：查询实体类及其 Relation 注解字段。
- **`selectAll()`**：查询全部数据。
- **`selectAllWithRelations()`**：查询全部数据，及其 Relation 字段内容。
- **`selectObjectByQuery(queryWrapper)`**：查询第一列返回的数据，QueryWrapper 执行的结果应该只有 1
  列，例如：`QueryWrapper.create().select(ACCOUNT.id).where(...);`
- **`selectObjectByQueryAs(queryWrapper, asType)`**：查询第一列返回的数据，QueryWrapper 执行的结果应该只有 1
  列，例如：`QueryWrapper.create().select(ACCOUNT.id).where(...);`
- **`selectObjectListByQuery(queryWrapper)`**：查询第一列返回的数据集合，QueryWrapper 执行的结果应该只有 1
  列，例如：`QueryWrapper.create().select(ACCOUNT.id).where(...);`
- **`selectObjectListByQueryAs(queryWrapper, asType)`**：查询第一列返回的数据集合，QueryWrapper 执行的结果应该只有 1
  列，例如：`QueryWrapper.create().select(ACCOUNT.id).where(...);`
- **`selectCountByQuery(queryWrapper)`**：查询数据量。
- **`selectCountByCondition(whereConditions)`**：根据条件查询数据总量。
