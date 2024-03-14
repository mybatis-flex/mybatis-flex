/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mybatisflex.core.row;

import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.QueryWrapperAdapter;
import com.mybatisflex.core.table.ColumnInfo;
import com.mybatisflex.core.table.IdInfo;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.update.PropertySetter;
import com.mybatisflex.core.util.FieldWrapper;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.SqlUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 链式构建 {@link QueryWrapper} 并执行 {@link Db} 方法。
 *
 * @author 王帅
 * @since 2023-07-22
 */
public class DbChain extends QueryWrapperAdapter<DbChain> implements PropertySetter<DbChain> {

    private String schema;
    private final String tableName;
    private Row rowData;

    private DbChain(String tableName) {
        this.tableName = tableName;
    }

    private DbChain(String schema, String tableName) {
        this.schema = schema;
        this.tableName = tableName;
    }

    public static DbChain create() {
        throw new UnsupportedOperationException("Please use DbChain#table(...) to replace DbChain.create()");
    }

    public static DbChain create(Object entity) {
        throw new UnsupportedOperationException("Please use DbChain#table(...) to replace DbChain.create(entity)");
    }

    public static DbChain table(String tableName) {
        return new DbChain(tableName);
    }

    public static DbChain table(String schema, String tableName) {
        return new DbChain(schema, tableName);
    }

    public static DbChain table(Class<?> entityClass) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
        return table(tableInfo.getSchema(), tableInfo.getTableName());
    }

    public static DbChain table(QueryTable queryTable) {
        return table(queryTable.getSchema(), queryTable.getName());
    }

    private Row getRow() {
        if (rowData == null) {
            this.rowData = new Row();
        }
        return rowData;
    }

    public DbChain setId(RowKey rowKey) {
        getRow().getPrimaryKeys().add(rowKey);
        return this;
    }

    public DbChain setId(RowKey rowKey, Object value) {
        getRow().getPrimaryKeys().add(rowKey);
        getRow().put(rowKey.keyColumn, value);
        return this;
    }

    @Override
    public DbChain set(String property, Object value, boolean isEffective) {
        getRow().set(property, value, isEffective);
        return this;
    }

    @Override
    public DbChain set(QueryColumn property, Object value, boolean isEffective) {
        getRow().set(property, value, isEffective);
        return this;
    }

    @Override
    public <T> DbChain set(LambdaGetter<T> property, Object value, boolean isEffective) {
        getRow().set(property, value, isEffective);
        return this;
    }

    @Override
    public DbChain setRaw(String property, Object value, boolean isEffective) {
        getRow().setRaw(property, value, isEffective);
        return this;
    }

    @Override
    public DbChain setRaw(QueryColumn property, Object value, boolean isEffective) {
        getRow().setRaw(property, value, isEffective);
        return this;
    }

    @Override
    public <T> DbChain setRaw(LambdaGetter<T> property, Object value, boolean isEffective) {
        getRow().setRaw(property, value, isEffective);
        return this;
    }

    public boolean save(Object entity) {
        return SqlUtil.toBool(Db.insert(schema, tableName, toRow(entity)));
    }

    public boolean save() {
        return SqlUtil.toBool(Db.insert(schema, tableName, getRow()));
    }

    public boolean remove() {
        return SqlUtil.toBool(Db.deleteByQuery(schema, tableName, this));
    }

    public boolean removeById() {
        return SqlUtil.toBool(Db.deleteById(schema, tableName, getRow()));
    }

    public boolean update() {
        return SqlUtil.toBool(Db.updateByQuery(schema, tableName, getRow(), this));
    }

    public boolean updateById() {
        return SqlUtil.toBool(Db.updateById(schema, tableName, getRow()));
    }

    private static Row toRow(Object entity) {
        Class<?> entityClass = entity.getClass();
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entity.getClass());
        Row row = new Row();

        // 添加非主键列设置的值
        for (ColumnInfo columnInfo : tableInfo.getColumnInfoList()) {
            try {
                FieldWrapper fieldWrapper = FieldWrapper.of(entityClass, columnInfo.getProperty());
                Object value = fieldWrapper.get(entity);
                if (value != null) {
                    row.put(columnInfo.getColumn(), value);
                }
            } catch (Exception e) {
                throw FlexExceptions.wrap(e);
            }
        }

        // 添加主键列设置的值
        for (IdInfo idInfo : tableInfo.getPrimaryKeyList()) {
            try {
                FieldWrapper fieldWrapper = FieldWrapper.of(entityClass, idInfo.getProperty());
                Object value = fieldWrapper.get(entity);
                if (value != null) {
                    RowKey rowKey = RowKey.of(idInfo.getColumn()
                        , idInfo.getKeyType()
                        , idInfo.getValue()
                        , idInfo.getBefore());
                    row.getPrimaryKeys().add(rowKey);
                    row.put(rowKey.keyColumn, value);
                }
            } catch (Exception e) {
                throw FlexExceptions.wrap(e);
            }
        }

        return row;
    }

    public boolean update(Object entity) {
        return update(toRow(entity));
    }

    public boolean update(Row data) {
        return SqlUtil.toBool(Db.updateByQuery(schema, tableName, data, this));
    }

    public boolean update(Map<String, Object> data) {
        Row row = new Row();
        row.putAll(data);
        return update(row);
    }

    public long count() {
        return Db.selectCountByQuery(schema, tableName, this);
    }

    public boolean exists() {
        return SqlUtil.toBool(count());
    }

    public Row one() {
        return Db.selectOneByQuery(schema, tableName, this);
    }

    public Optional<Row> oneOpt() {
        return Optional.ofNullable(one());
    }

    public <R> R oneAs(Class<R> asType) {
        return one().toEntity(asType);
    }

    public <R> Optional<R> oneAsOpt(Class<R> asType) {
        return Optional.ofNullable(oneAs(asType));
    }

    public Object obj() {
        return Db.selectObject(schema, tableName, this);
    }

    public Optional<Object> objOpt() {
        return Optional.ofNullable(obj());
    }

    @SuppressWarnings("unchecked")
    public <R> R objAs() {
        return (R) obj();
    }

    public <R> R objAs(Class<R> asType) {
        return asType.cast(obj());
    }

    public <R> Optional<R> objAsOpt() {
        return Optional.ofNullable(objAs());
    }

    public <R> Optional<R> objAsOpt(Class<R> asType) {
        return Optional.ofNullable(objAs(asType));
    }

    public List<Object> objList() {
        return Db.selectObjectList(schema, tableName, this);
    }

    @SuppressWarnings("unchecked")
    public <R> List<R> objListAs() {
        return objList()
            .stream()
            .map(obj -> (R) obj)
            .collect(Collectors.toList());
    }

    public <R> List<R> objListAs(Class<R> asType) {
        return objList()
            .stream()
            .map(asType::cast)
            .collect(Collectors.toList());
    }

    public List<Row> list() {
        return Db.selectListByQuery(schema, tableName, this);
    }

    public <R> List<R> listAs(Class<R> asType) {
        return list()
            .stream()
            .map(row -> row.toEntity(asType))
            .collect(Collectors.toList());
    }

    public Page<Row> page(Page<Row> page) {
        return Db.paginate(schema, tableName, page, this);
    }

    public <R> Page<R> pageAs(Page<R> page, Class<R> asType) {
        Page<Row> rowPage = new Page<>();
        rowPage.setPageNumber(page.getPageNumber());
        rowPage.setPageSize(page.getPageSize());
        rowPage.setTotalRow(page.getTotalRow());
        return page(rowPage).map(row -> row.toEntity(asType));
    }

}
