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

package com.mybatisflex.spring.service.impl;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ClassUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>可缓存数据的 Service 实现类。
 *
 * <p>该实现类对缓存做了以下处理：
 *
 * <ul>
 *     <li>重写 {@link #saveOrUpdate(Object)} 方法，分别调用 {@link #save(Object)} 和 {@link #updateById(Object)}
 *     方法，避免缓存无法更新造成数据不一致。
 *     <li>重写 {@link #query()} 方法，解决使用 {@link QueryWrapper#toSQL()} 作为缓存
 *     的主键时，"SELECT * FROM" 后面没有表名的问题。
 * </ul>
 *
 * @author 王帅
 * @since 2023-05-30
 */
public class CacheableServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    protected M mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseMapper<T> getMapper() {
        return mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveOrUpdate(T entity) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entity.getClass());
        Object[] pkArgs = tableInfo.buildPkSqlArgs(entity);
        if (pkArgs.length == 0 || pkArgs[0] == null) {
            return save(entity);
        } else {
            return updateById(entity);
        }
    }

    /**
     * <p>获取默认的 {@link QueryWrapper}。
     *
     * <p>使用 {@link QueryWrapper#create()} 构建默认查询条件的时候，
     * 要使用 {@link QueryWrapper#from(String...)} 方法指定从哪个表
     * 查询数据，不然使用 {@link QueryWrapper#toSQL()} 生成的
     * SQL 语句就是 {@code "SELECT * FROM"}，没有表名信息。
     *
     * <p>默认通过反射获取表名，建议重写，根据情况设置默认表名，以提升效率。
     *
     * <p>例如：
     *
     * <pre>{@code
     * @Override
     * public QueryWrapper query() {
     *     return QueryWrapper.create().from(ACCOUNT);
     * }
     * }</pre>
     *
     * @return 默认的 {@link QueryWrapper}
     */
    @Override
    public QueryWrapper query() {
        Class<?> mapperClass = ClassUtil.getUsefulClass(getMapper().getClass());
        TableInfo tableInfo = TableInfoFactory.ofMapperClass(mapperClass);
        return QueryWrapper.create().from(new QueryTable(tableInfo.getSchema(), tableInfo.getTableName()));
    }

}
