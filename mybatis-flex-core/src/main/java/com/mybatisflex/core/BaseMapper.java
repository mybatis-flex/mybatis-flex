/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core;

import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.provider.EntitySqlProvider;
import com.mybatisflex.core.querywrapper.QueryColumn;
import com.mybatisflex.core.querywrapper.QueryWrapper;
import com.mybatisflex.core.querywrapper.CPI;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BaseMapper<T> {

    /**
     * 插入 entity 数据
     *
     * @param entity
     * @return 返回影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#insert(Map, ProviderContext)
     */
    @InsertProvider(type = EntitySqlProvider.class, method = "insert")
    int insert(@Param(FlexConsts.ENTITY) T entity);


    /**
     * 批量插入 entity 数据，只会根据第一条数据来构建插入的字段内容
     *
     * @param entities 插入的数据列表
     * @return 返回影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#insertBatch(Map, ProviderContext)
     * @see com.mybatisflex.core.FlexConsts#METHOD_INSERT_BATCH
     */
    @InsertProvider(type = EntitySqlProvider.class, method = FlexConsts.METHOD_INSERT_BATCH)
    int insertBatch(@Param(FlexConsts.ENTITIES) List<T> entities);

    /**
     * 根据 id 删除数据
     * 如果是多个主键的情况下，需要传入数组 new Object[]{100,101}
     *
     * @param id 主键数据
     * @return 返回影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#deleteById(Map, ProviderContext)
     */
    @DeleteProvider(type = EntitySqlProvider.class, method = "deleteById")
    int deleteById(@Param(FlexConsts.PRIMARY_VALUE) Serializable id);


    /**
     * 根据多个 id 批量删除数据
     *
     * @param ids ids 列表
     * @return 返回影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#deleteBatchByIds(Map, ProviderContext)
     */
    @DeleteProvider(type = EntitySqlProvider.class, method = "deleteBatchByIds")
    int deleteBatchByIds(@Param(FlexConsts.PRIMARY_VALUE) Collection<? extends Serializable> ids);


    /**
     * 根据 map 构建的条件来删除数据
     *
     * @param whereConditions
     * @return 返回影响的行数
     */
    default int deleteByMap(Map<String, Object> whereConditions) {
        if (whereConditions == null || whereConditions.isEmpty()) {
            throw FlexExceptions.wrap("deleteByMap is not allow empty map.");
        }
        return deleteByQuery(QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据 query 构建的条件来数据吗
     *
     * @param queryWrapper query 条件
     * @return 返回影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#deleteByQuery(Map, ProviderContext)
     */
    @DeleteProvider(type = EntitySqlProvider.class, method = "deleteByQuery")
    int deleteByQuery(@Param(FlexConsts.QUERY) QueryWrapper queryWrapper);


    /**
     * 根据主键来更新数据，若数据为 null，也会更新到数据库
     *
     * @param entity 数据内容，必须包含有主键
     * @return 返回影响的行数
     */
    default int update(T entity) {
        return update(entity, true);
    }

    /**
     * 根据主键来更新数据到数据库
     *
     * @param entity      数据内容
     * @param ignoreNulls 是否忽略空内容字段
     * @return 返回影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#update(Map, ProviderContext)
     */
    @UpdateProvider(type = EntitySqlProvider.class, method = "update")
    int update(@Param(FlexConsts.ENTITY) T entity, @Param(FlexConsts.IGNORE_NULLS) boolean ignoreNulls);


    /**
     * 根据 map 构建的条件来更新数据
     *
     * @param entity 数据
     * @param map    where 条件内容
     * @return 返回影响的行数
     */
    default int updateByMap(T entity, Map<String, Object> map) {
        return updateByQuery(entity, QueryWrapper.create().where(map));
    }


    /**
     * 根据 query 构建的条件来更新数据
     *
     * @param entity       数据内容
     * @param queryWrapper query 条件
     * @return 返回影响的行数
     */

    default int updateByQuery(@Param(FlexConsts.ENTITY) T entity, @Param(FlexConsts.QUERY) QueryWrapper queryWrapper) {
        return updateByQuery(entity, true, queryWrapper);
    }

    /**
     * 根据 query 构建的条件来更新数据
     *
     * @param entity       数据内容
     * @param ignoreNulls  是否忽略空值
     * @param queryWrapper query 条件
     * @return
     * @see com.mybatisflex.core.provider.EntitySqlProvider#updateByQuery(Map, ProviderContext)
     */
    @UpdateProvider(type = EntitySqlProvider.class, method = "updateByQuery")
    int updateByQuery(@Param(FlexConsts.ENTITY) T entity, @Param("ignoreNulls") boolean ignoreNulls, @Param(FlexConsts.QUERY) QueryWrapper queryWrapper);


    /**
     * 根据主键来选择数据
     *
     * @param id 多个主键
     * @return entity
     * @see com.mybatisflex.core.provider.EntitySqlProvider#selectOneById(Map, ProviderContext)
     */
    @SelectProvider(type = EntitySqlProvider.class, method = "selectOneById")
    T selectOneById(@Param(FlexConsts.PRIMARY_VALUE) Serializable id);


    /**
     * 根据 map 构建的条件来查询数据
     *
     * @param whereConditions where 条件
     * @return entity 数据
     */
    default T selectOneByMap(Map<String, Object> whereConditions) {
        return selectOneByQuery(QueryWrapper.create().where(whereConditions));
    }


    /**
     * 根据 queryWrapper 构建的条件来查询 1 条数据
     *
     * @param queryWrapper query 条件
     * @return entity 数据
     */
    default T selectOneByQuery(@Param(FlexConsts.QUERY) QueryWrapper queryWrapper) {
        List<T> entities = selectListByQuery(queryWrapper.limit(1));
        if (entities == null || entities.isEmpty()) {
            return null;
        } else {
            return entities.get(0);
        }
    }

    /**
     * 根据多个主键来查询多条数据
     *
     * @param ids 主键列表
     * @return 数据列表
     * @see com.mybatisflex.core.provider.EntitySqlProvider#selectListByIds(Map, ProviderContext)
     */
    @SelectProvider(type = EntitySqlProvider.class, method = "selectListByIds")
    List<T> selectListByIds(@Param(FlexConsts.PRIMARY_VALUE) Collection<? extends Serializable> ids);


    /**
     * 根据 map 来构建查询条件，查询多条数据
     *
     * @param whereConditions 条件列表
     * @return 数据列表
     */
    default List<T> selectListByMap(Map<String, Object> whereConditions) {
        return selectListByQuery(QueryWrapper.create().where(whereConditions));
    }


    /**
     * 根据 query 来构建条件查询数据列表
     *
     * @param queryWrapper 查询条件
     * @return 数据列表
     * @see com.mybatisflex.core.provider.EntitySqlProvider#selectListByQuery(Map, ProviderContext)
     */
    @SelectProvider(type = EntitySqlProvider.class, method = "selectListByQuery")
    List<T> selectListByQuery(@Param(FlexConsts.QUERY) QueryWrapper queryWrapper);


    /**
     * 根据 queryWrapper 来查询数据量
     *
     * @param queryWrapper
     * @return 数据量
     * @see EntitySqlProvider#selectCountByQuery(Map, ProviderContext)
     */
    @SelectProvider(type = EntitySqlProvider.class, method = "selectCountByQuery")
    long selectCountByQuery(@Param(FlexConsts.QUERY) QueryWrapper queryWrapper);


    /**
     * 分页查询
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param queryWrapper 查询条件
     * @return 返回 Page 数据
     */
    default Page<T> paginate(int pageNumber, int pageSize, QueryWrapper queryWrapper) {
        Page<T> page = new Page<>(pageNumber, pageSize);
        return paginate(page, queryWrapper);
    }


    /**
     * 分页查询
     *
     * @param page         page，其包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 查询条件
     * @return page 数据
     */
    default Page<T> paginate(@Param("page") Page<T> page, @Param("query") QueryWrapper queryWrapper) {
        List<QueryColumn> groupByColumns = CPI.getGroupByColumns(queryWrapper);

        // 只有 totalRow 小于 0 的时候才会去查询总量
        // 这样方便用户做总数缓存，而非每次都要去查询总量
        // 一般的分页场景中，只有第一页的时候有必要去查询总量，第二页以后是不需要的
        if (page.getTotalRow() < 0) {
            //清除group by 去查询数据
            CPI.setGroupByColumns(queryWrapper, null);
            long count = selectCountByQuery(queryWrapper);
            page.setTotalRow(count);
        }

        if (page.getTotalRow() == 0 || page.getPageNumber() > page.getTotalPage()) {
            return page;
        }

        //恢复数量查询清除的 groupBy
        CPI.setGroupByColumns(queryWrapper, groupByColumns);
        int offset = page.getPageSize() * (page.getPageNumber() - 1);
        queryWrapper.limit(offset, page.getPageSize());
        List<T> rows = selectListByQuery(queryWrapper);
        page.setList(rows);
        return page;
    }
}
