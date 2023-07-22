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
import com.mybatisflex.core.util.SqlUtil;

import java.util.List;
import java.util.Optional;

/**
 * {@link QueryWrapper}链式调用。
 *
 * @author 王帅
 * @since 2023-07-22
 */
public class QueryWrapperChain<T> extends QueryWrapperAdapter<QueryWrapperChain<T>> {

    private final BaseMapper<T> baseMapper;

    public QueryWrapperChain(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    public static <E> QueryWrapperChain<E> create(BaseMapper<E> baseMapper) {
        return new QueryWrapperChain<>(baseMapper);
    }

    public boolean remove() {
        return SqlUtil.toBool(baseMapper.deleteByQuery(this));
    }

    public boolean update(T entity) {
        return SqlUtil.toBool(baseMapper.updateByQuery(entity, this));
    }

    public boolean update(T entity, boolean ignoreNulls) {
        return SqlUtil.toBool(baseMapper.updateByQuery(entity, ignoreNulls, this));
    }

    public long count() {
        return baseMapper.selectCountByQuery(this);
    }

    public boolean exists() {
        return SqlUtil.toBool(count());
    }

    public T getOne() {
        return baseMapper.selectOneByQuery(this);
    }

    public <R> R getOneAs(Class<R> asType) {
        return baseMapper.selectOneByQueryAs(this, asType);
    }

    public T getOneWithRelations() {
        return baseMapper.selectOneWithRelationsByQuery(this);
    }

    public <R> R getOneWithRelationsAs(Class<R> asType) {
        return baseMapper.selectOneWithRelationsByQueryAs(this, asType);
    }

    public Optional<T> getOneOpt() {
        return Optional.ofNullable(baseMapper.selectOneByQuery(this));
    }

    public <R> Optional<R> getOneAsOpt(Class<R> asType) {
        return Optional.ofNullable(baseMapper.selectOneByQueryAs(this, asType));
    }

    public Optional<T> getOneWithRelationsOpt() {
        return Optional.ofNullable(baseMapper.selectOneWithRelationsByQuery(this));
    }

    public <R> Optional<R> getOneWithRelationsAsOpt(Class<R> asType) {
        return Optional.ofNullable(baseMapper.selectOneWithRelationsByQueryAs(this, asType));
    }



    public List<T> getList() {
        return baseMapper.selectListByQuery(this);
    }

    public List<T> getListWithRelations() {
        return baseMapper.selectListWithRelationsByQuery(this);
    }

    public <R> List<R> getListAs(Class<R> asType) {
        return baseMapper.selectListByQueryAs(this, asType);
    }

    public <R> List<R> getListWithRelationsAs(Class<R> asType) {
        return baseMapper.selectListWithRelationsByQueryAs(this, asType);
    }

    public Page<T> getPage(Page<T> page) {
        return baseMapper.paginate(page, this);
    }

    public Page<T> getPageWithRelations(Page<T> page) {
        return baseMapper.paginateWithRelations(page, this);
    }

    public <R> Page<R> getPageAs(Page<R> page, Class<R> asType) {
        return baseMapper.paginateAs(page, this, asType);
    }

    public <R> Page<R> getPageWithRelationsAs(Page<R> page, Class<R> asType) {
        return baseMapper.paginateWithRelationsAs(page, this, asType);
    }


    public Object getObj() {
        return baseMapper.selectObjectByQuery(this);
    }

    public <R> R getObjAs(Class<R> asType) {
        return baseMapper.selectObjectByQueryAs(this, asType);
    }

    public Optional<Object> getObjOpt() {
        return Optional.ofNullable(baseMapper.selectObjectByQuery(this));
    }

    public <R> Optional<R> getObjAsOpt(Class<R> asType) {
        return Optional.ofNullable(baseMapper.selectObjectByQueryAs(this, asType));
    }


    public List<Object> getObjList() {
        return baseMapper.selectObjectListByQuery(this);
    }

    public <R> List<R> getObjListAs(Class<R> asType) {
        return baseMapper.selectObjectListByQueryAs(this, asType);
    }

    public Optional<Object> getObjListOpt() {
        return Optional.ofNullable(baseMapper.selectObjectListByQuery(this));
    }

    public <R> Optional<List<R>> getObjListAsOpt(Class<R> asType) {
        return Optional.ofNullable(baseMapper.selectObjectListByQueryAs(this, asType));
    }

}
