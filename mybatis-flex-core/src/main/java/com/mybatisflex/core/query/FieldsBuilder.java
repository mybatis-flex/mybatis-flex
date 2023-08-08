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

import com.mybatisflex.core.field.FieldQuery;
import com.mybatisflex.core.field.FieldQueryManager;
import com.mybatisflex.core.field.QueryBuilder;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.util.FieldWrapper;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用 {@code Fields Query} 的方式进行关联查询。
 *
 * @author 王帅
 * @since 2023-08-08
 */
public class FieldsBuilder<T> extends AbstractQueryBuilder<T> {

    protected final Map<String, FieldQuery> fieldQueryMap;

    public FieldsBuilder(MapperQueryChain<T> delegate) {
        super(delegate);
        this.fieldQueryMap = new HashMap<>();
    }

    /**
     * 设置属性对应的 {@code QueryWrapper} 查询。
     *
     * @param field   属性
     * @param builder {@code QueryWrapper} 构建
     * @param <F>     属性类型
     * @return 属性查询构建
     */
    public <F> FieldsBuilder<T> fieldMapping(LambdaGetter<F> field, QueryBuilder<F> builder) {
        return fieldMapping(field, false, builder);
    }

    /**
     * 设置属性对应的 {@code QueryWrapper} 查询。
     *
     * @param field   属性
     * @param prevent 阻止对嵌套类属性的查询
     * @param builder {@code QueryWrapper} 构建
     * @param <F>     属性类型
     * @return 属性查询构建
     */
    public <F> FieldsBuilder<T> fieldMapping(LambdaGetter<F> field, boolean prevent, QueryBuilder<F> builder) {
        String fieldName = LambdaUtil.getFieldName(field);
        Class<?> entityClass = LambdaUtil.getImplClass(field);
        FieldQuery fieldQuery = new FieldQuery();
        fieldQuery.setPrevent(prevent);
        fieldQuery.setFieldName(fieldName);
        fieldQuery.setQueryBuilder(builder);
        fieldQuery.setEntityClass(entityClass);
        fieldQuery.setFieldWrapper(FieldWrapper.of(entityClass, fieldName));
        this.fieldQueryMap.put(entityClass.getName() + '#' + fieldName, fieldQuery);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T one() {
        List<T> entities = Collections.singletonList(baseMapper().selectOneByQuery(queryWrapper()));
        FieldQueryManager.queryFields(baseMapper(), entities, fieldQueryMap);
        return entities.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R oneAs(Class<R> asType) {
        List<R> entities = Collections.singletonList(baseMapper().selectOneByQueryAs(queryWrapper(), asType));
        FieldQueryManager.queryFields(baseMapper(), entities, fieldQueryMap);
        return entities.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> list() {
        List<T> entities = baseMapper().selectListByQuery(queryWrapper());
        FieldQueryManager.queryFields(baseMapper(), entities, fieldQueryMap);
        return entities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> List<R> listAs(Class<R> asType) {
        List<R> entities = baseMapper().selectListByQueryAs(queryWrapper(), asType);
        FieldQueryManager.queryFields(baseMapper(), entities, fieldQueryMap);
        return entities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> page(Page<T> page) {
        baseMapper().paginate(page, queryWrapper());
        FieldQueryManager.queryFields(baseMapper(), page.getRecords(), fieldQueryMap);
        return page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Page<R> pageAs(Page<R> page, Class<R> asType) {
        baseMapper().paginateAs(page, queryWrapper(), asType);
        FieldQueryManager.queryFields(baseMapper(), page.getRecords(), fieldQueryMap);
        return page;
    }

}
