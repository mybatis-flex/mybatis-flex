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
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ObjectUtil;
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
     * @param entity 实体类
     * @return 返回影响的行数
     */
    default int insert(T entity){
        return insert(entity,false);
    }


    /**
     * 插入 entity 数据，但是忽略 null 的数据，只对有值的内容进行插入
     * 这样的好处是数据库已经配置了一些默认值，这些默认值才会生效
     *
     * @param entity 实体类
     * @return 返回影响的行数
     */
    default int insertSelective(T entity){
        return insert(entity,true);
    }



    /**
     * 插入 entity 数据
     *
     * @param entity 实体类
     * @return 返回影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#insert(Map, ProviderContext)
     */
    @InsertProvider(type = EntitySqlProvider.class, method = "insert")
    int insert(@Param(FlexConsts.ENTITY) T entity, @Param(FlexConsts.IGNORE_NULLS) boolean ignoreNulls);



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
     * 新增 或者 更新，若主键有值，则更新，若没有主键值，则插入
     *
     * @param entity 实体类
     * @return 返回影响的行数
     */
    default int insertOrUpdate(T entity) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entity.getClass());
        Object[] pkArgs = tableInfo.buildPkSqlArgs(entity);
        if (pkArgs.length == 0 || pkArgs[0] == null) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

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
     * @param whereConditions 条件
     * @return 返回影响的行数
     */
    default int deleteByMap(Map<String, Object> whereConditions) {
        if (ObjectUtil.areNull(whereConditions) || whereConditions.isEmpty()) {
            throw FlexExceptions.wrap("deleteByMap is not allow empty map.");
        }
        return deleteByQuery(QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据条件来删除数据
     *
     * @param condition 条件
     * @return 返回影响的行数
     */
    default int deleteByCondition(QueryCondition condition) {
        return deleteByQuery(QueryWrapper.create().where(condition));
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
     * 根据 condition 来更新数据
     *
     * @param entity    数据
     * @param condition 条件
     * @return 返回影响的行数
     */
    default int updateByCondition(T entity, QueryCondition condition) {
        return updateByQuery(entity, QueryWrapper.create().where(condition));
    }

    /**
     * 根据 condition 来更新数据
     *
     * @param entity      数据
     * @param ignoreNulls 是否忽略 null 数据，默认为 true
     * @param condition   条件
     * @return 返回影响的行数
     */
    default int updateByCondition(T entity, boolean ignoreNulls, QueryCondition condition) {
        return updateByQuery(entity, ignoreNulls, QueryWrapper.create().where(condition));
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
     * @see com.mybatisflex.core.provider.EntitySqlProvider#updateByQuery(Map, ProviderContext)
     */
    @UpdateProvider(type = EntitySqlProvider.class, method = "updateByQuery")
    int updateByQuery(@Param(FlexConsts.ENTITY) T entity, @Param(FlexConsts.IGNORE_NULLS) boolean ignoreNulls, @Param(FlexConsts.QUERY) QueryWrapper queryWrapper);


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
     * 根据 condition 来查询数据
     *
     * @param condition 条件
     * @return 1 条数据
     */
    default T selectOneByCondition(QueryCondition condition) {
        return selectOneByQuery(QueryWrapper.create().where(condition));
    }


    /**
     * 根据 queryWrapper 构建的条件来查询 1 条数据
     *
     * @param queryWrapper query 条件
     * @return entity 数据
     */
    default T selectOneByQuery(@Param(FlexConsts.QUERY) QueryWrapper queryWrapper) {
        List<T> entities = selectListByQuery(queryWrapper.limit(1));
        return (entities == null || entities.isEmpty()) ? null : entities.get(0);
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
     * 根据 map 来构建查询条件，查询多条数据
     *
     * @param whereConditions 条件列表
     * @return 数据列表
     */
    default List<T> selectListByMap(Map<String, Object> whereConditions, int count) {
        return selectListByQuery(QueryWrapper.create().where(whereConditions).limit(count));
    }


    /**
     * 根据 condition 来查询数据
     *
     * @param condition condition 条件
     * @return 数据列表
     */
    default List<T> selectListByCondition(QueryCondition condition) {
        return selectListByQuery(QueryWrapper.create().where(condition));
    }


    /**
     * 根据 condition 来查询数据
     *
     * @param condition condition 条件
     * @param count     数据量
     * @return 数据列表
     */
    default List<T> selectListByCondition(QueryCondition condition, int count) {
        return selectListByQuery(QueryWrapper.create().where(condition).limit(count));
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
     * 查询全部数据
     *
     * @return 数据列表
     */
    default List<T> selectAll() {
        return selectListByQuery(new QueryWrapper());
    }


    /**
     * 根据条件查询数据总量
     *
     * @param condition 条件
     * @return 数据量
     */
    default long selectCountByCondition(QueryCondition condition) {
        return selectCountByQuery(QueryWrapper.create().where(condition));
    }


    /**
     * 根据 queryWrapper 来查询数据量
     *
     * @param queryWrapper 查询包装器
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
     * 根据条件分页查询
     *
     * @param pageNumber 当前页面
     * @param pageSize   每页的数据量
     * @param condition  查询条件
     * @return 返回 Page 数据
     */
    default Page<T> paginate(int pageNumber, int pageSize, QueryCondition condition) {
        Page<T> page = new Page<>(pageNumber, pageSize);
        return paginate(page, new QueryWrapper().where(condition));
    }

    /**
     * 分页查询
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param totalRow     数据总量
     * @param queryWrapper 查询条件
     * @return 返回 Page 数据
     */
    default Page<T> paginate(int pageNumber, int pageSize, int totalRow, QueryWrapper queryWrapper) {
        Page<T> page = new Page<>(pageNumber, pageSize, totalRow);
        return paginate(page, queryWrapper);
    }


    /**
     * 根据条件分页查询
     *
     * @param pageNumber 当前页面
     * @param pageSize   每页的数据量
     * @param totalRow   数据总量
     * @param condition  查询条件
     * @return 返回 Page 数据
     */
    default Page<T> paginate(int pageNumber, int pageSize, int totalRow, QueryCondition condition) {
        Page<T> page = new Page<>(pageNumber, pageSize, totalRow);
        return paginate(page, new QueryWrapper().where(condition));
    }


    /**
     * 分页查询
     *
     * @param page         page，其包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 查询条件
     * @return page 数据
     */
    default Page<T> paginate(@Param("page") Page<T> page, @Param("query") QueryWrapper queryWrapper) {

        List<QueryColumn> groupByColumns = null;

        // 只有 totalRow 小于 0 的时候才会去查询总量
        // 这样方便用户做总数缓存，而非每次都要去查询总量
        // 一般的分页场景中，只有第一页的时候有必要去查询总量，第二页以后是不需要的
        if (page.getTotalRow() < 0) {
            groupByColumns = CPI.getGroupByColumns(queryWrapper);
            //清除group by 去查询数据
            CPI.setGroupByColumns(queryWrapper, null);
            long count = selectCountByQuery(queryWrapper);
            page.setTotalRow(count);
        }

        if (page.getTotalRow() == 0 || page.getPageNumber() > page.getTotalPage()) {
            return page;
        }

        //恢复数量查询清除的 groupBy
        if (groupByColumns != null) {
            CPI.setGroupByColumns(queryWrapper, groupByColumns);
        }

        int offset = page.getPageSize() * (page.getPageNumber() - 1);
        queryWrapper.limit(offset, page.getPageSize());
        List<T> rows = selectListByQuery(queryWrapper);
        page.setRecords(rows);
        return page;
    }
}
