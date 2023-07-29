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

package com.mybatisflex.core.query;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.SqlUtil;

import java.util.List;
import java.util.Optional;

/**
 * {@link QueryWrapper}链式调用。
 *
 * @author 王帅
 * @since 2023-07-22
 */
public class QueryChain<T> extends QueryWrapperAdapter<QueryChain<T>> {

    private final BaseMapper<T> baseMapper;

    public QueryChain(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    public static <E> QueryChain<E> of(BaseMapper<E> baseMapper) {
        return new QueryChain<>(baseMapper);
    }

    public long count() {
        return baseMapper.selectCountByQuery(this);
    }

    public boolean exists() {
        return SqlUtil.toBool(count());
    }

    public T one() {
        return baseMapper.selectOneByQuery(this);
    }

    public <R> R oneAs(Class<R> asType) {
        return baseMapper.selectOneByQueryAs(this, asType);
    }

    public T oneWithRelations() {
        return baseMapper.selectOneWithRelationsByQuery(this);
    }

    public <R> R oneWithRelationsAs(Class<R> asType) {
        return baseMapper.selectOneWithRelationsByQueryAs(this, asType);
    }

    public Optional<T> oneOpt() {
        return Optional.ofNullable(baseMapper.selectOneByQuery(this));
    }

    public <R> Optional<R> oneAsOpt(Class<R> asType) {
        return Optional.ofNullable(baseMapper.selectOneByQueryAs(this, asType));
    }

    public Optional<T> oneWithRelationsOpt() {
        return Optional.ofNullable(baseMapper.selectOneWithRelationsByQuery(this));
    }

    public <R> Optional<R> oneWithRelationsAsOpt(Class<R> asType) {
        return Optional.ofNullable(baseMapper.selectOneWithRelationsByQueryAs(this, asType));
    }

    public Object obj() {
        return baseMapper.selectObjectByQuery(this);
    }

    public <R> R objAs(Class<R> asType) {
        return baseMapper.selectObjectByQueryAs(this, asType);
    }

    public Optional<Object> objOpt() {
        return Optional.ofNullable(baseMapper.selectObjectByQuery(this));
    }

    public <R> Optional<R> objAsOpt(Class<R> asType) {
        return Optional.ofNullable(baseMapper.selectObjectByQueryAs(this, asType));
    }

    public List<Object> objList() {
        return baseMapper.selectObjectListByQuery(this);
    }

    public <R> List<R> objListAs(Class<R> asType) {
        return baseMapper.selectObjectListByQueryAs(this, asType);
    }

    public List<T> list() {
        return baseMapper.selectListByQuery(this);
    }

    public List<T> listWithRelations() {
        return baseMapper.selectListWithRelationsByQuery(this);
    }

    public <R> List<R> listAs(Class<R> asType) {
        return baseMapper.selectListByQueryAs(this, asType);
    }

    public <R> List<R> listWithRelationsAs(Class<R> asType) {
        return baseMapper.selectListWithRelationsByQueryAs(this, asType);
    }

    public Page<T> page(Page<T> page) {
        return baseMapper.paginate(page, this);
    }

    public Page<T> pageWithRelations(Page<T> page) {
        return baseMapper.paginateWithRelations(page, this);
    }

    public <R> Page<R> pageAs(Page<R> page, Class<R> asType) {
        return baseMapper.paginateAs(page, this, asType);
    }

    public <R> Page<R> pageWithRelationsAs(Page<R> page, Class<R> asType) {
        return baseMapper.paginateWithRelationsAs(page, this, asType);
    }

    @Override
    public String toSQL() {
        TableInfo tableInfo = TableInfoFactory.ofMapperClass(baseMapper.getClass());
        CPI.setFromIfNecessary(this, tableInfo.getSchema(), tableInfo.getTableName());
        return super.toSQL();
    }
}
