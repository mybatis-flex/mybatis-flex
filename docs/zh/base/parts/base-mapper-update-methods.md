- **`update(entity)`**：根据主键来更新数据，若实体类属性数据为 `null`，该属性不会更新到数据库。
- **`update(entity, ignoreNulls)`**：根据主键来更新数据到数据库。
- **`updateByMap(entity, whereConditions)`**：根据 Map 构建的条件来更新数据。
- **`updateByMap(entity, ignoreNulls, whereConditions)`**：根据 Map 构建的条件来更新数据。
- **`updateByCondition(entity, whereConditions)`**：根据查询条件来更新数据。
- **`updateByCondition(entity, ignoreNulls, whereConditions)`**：根据查询条件来更新数据。
- **`updateByQuery(entity, queryWrapper)`**：根据查询条件来更新数据。
- **`updateByQuery(entity, ignoreNulls, queryWrapper)`**：根据查询条件来更新数据。
- ~~**`updateNumberAddByQuery(fieldName, value, queryWrapper)`**~~：执行类似 `update table set field = field + 1 where ... `
  的场景。
- ~~**`updateNumberAddByQuery(column, value, queryWrapper)`**~~：执行类似 `update table set field = field + 1 where ... `
  的场景。
- ~~**`updateNumberAddByQuery(fn, value, queryWrapper)`**~~：执行类似 `update table set field = field + 1 where ... ` 的场景。
