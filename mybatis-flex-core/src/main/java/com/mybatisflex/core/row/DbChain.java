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
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.QueryWrapperAdapter;
import com.mybatisflex.core.table.ColumnInfo;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
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
public class DbChain extends QueryWrapperAdapter<DbChain> {

    public static DbChain create() {
        return new DbChain();
    }

    public boolean remove() {
        return SqlUtil.toBool(Db.deleteByQuery(null, this));
    }

    private Row toRow(Object entity) {
        Class<?> entityClass = entity.getClass();
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entity.getClass());
        Row row = new Row();

        // 添加非主键列设置的值
        for (ColumnInfo columnInfo : tableInfo.getColumnInfoList()) {
            try {
                Field declaredField = entityClass.getDeclaredField(columnInfo.getProperty());
                declaredField.setAccessible(true);
                Object value = declaredField.get(entity);
                if (value != null) {
                    row.put(columnInfo.getColumn(), value);
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
        return SqlUtil.toBool(Db.updateByQuery(null, data, this));
    }

    public boolean update(Map<String, Object> data) {
        Row row = new Row();
        row.putAll(data);
        return update(row);
    }

    public long count() {
        return Db.selectCountByQuery(this);
    }

    public boolean exists() {
        return SqlUtil.toBool(count());
    }

    public Row one() {
        return Db.selectOneByQuery(this);
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
        return Db.selectObject(this);
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
        return Db.selectObjectList(this);
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
        return Db.selectListByQuery(this);
    }

    public <R> List<R> listAs(Class<R> asType) {
        return list()
            .stream()
            .map(row -> row.toEntity(asType))
            .collect(Collectors.toList());
    }

    public Page<Row> page(Page<Row> page) {
        return Db.paginate(null, page, this);
    }

    public <R> Page<R> pageAs(Page<R> page, Class<R> asType) {
        Page<Row> rowPage = new Page<>();
        rowPage.setPageNumber(page.getPageNumber());
        rowPage.setPageSize(page.getPageSize());
        rowPage.setTotalRow(page.getTotalRow());
        return page(rowPage).map(row -> row.toEntity(asType));
    }

}
