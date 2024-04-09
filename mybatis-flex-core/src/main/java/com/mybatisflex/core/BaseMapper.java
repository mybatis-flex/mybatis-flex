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
package com.mybatisflex.core;

import com.mybatisflex.core.constant.FuncName;
import com.mybatisflex.core.exception.FlexAssert;
import com.mybatisflex.core.field.FieldQueryBuilder;
import com.mybatisflex.core.mybatis.MappedStatementTypes;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.provider.EntitySqlProvider;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.FunctionQueryColumn;
import com.mybatisflex.core.query.Join;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.ConvertUtil;
import com.mybatisflex.core.util.MapperUtil;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.mybatisflex.core.query.QueryMethods.count;

/**
 * 通用 Mapper 接口。
 *
 * @param <T> 实体类类型
 * @author 开源海哥
 * @author 庄佳彬
 * @author 闵柳华
 * @author 王帅
 * @author yangs
 * @author lhzsdnu
 * @author 王超
 */
@SuppressWarnings({"varargs", "unchecked", "unused"})
public interface BaseMapper<T> {

    /**
     * 默认批量处理切片数量。
     */
    int DEFAULT_BATCH_SIZE = 1000;

    // === 增（insert） ===

    /**
     * 插入实体类数据，不忽略 {@code null} 值。
     *
     * @param entity 实体类
     * @return 受影响的行数
     */
    default int insert(T entity) {
        return insert(entity, false);
    }

    /**
     * 插入实体类数据，但是忽略 {@code null} 的数据，只对有值的内容进行插入。
     * 这样的好处是数据库已经配置了一些默认值，这些默认值才会生效。
     *
     * @param entity 实体类
     * @return 受影响的行数
     */
    default int insertSelective(T entity) {
        return insert(entity, true);
    }

    /**
     * 插入实体类数据。
     *
     * @param entity      实体类
     * @param ignoreNulls 是否忽略 {@code null} 值
     * @return 受影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#insert(Map, ProviderContext)
     */
    @InsertProvider(type = EntitySqlProvider.class, method = "insert")
    int insert(@Param(FlexConsts.ENTITY) T entity, @Param(FlexConsts.IGNORE_NULLS) boolean ignoreNulls);

    /**
     * 插入带有主键的实体类，不忽略 {@code null} 值。
     *
     * @param entity 实体类
     * @return 受影响的行数
     */
    default int insertWithPk(T entity) {
        return insertWithPk(entity, false);
    }

    /**
     * 插入带有主键的实体类，忽略 {@code null} 值。
     *
     * @param entity 实体类
     * @return 受影响的行数
     */
    default int insertSelectiveWithPk(T entity) {
        return insertWithPk(entity, true);
    }

    /**
     * 带有主键的插入，此时实体类不会经过主键生成器生成主键。
     *
     * @param entity      带有主键的实体类
     * @param ignoreNulls 是否忽略 {@code null} 值
     * @return 受影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#insertWithPk(Map, ProviderContext)
     */
    @InsertProvider(type = EntitySqlProvider.class, method = "insertWithPk")
    int insertWithPk(@Param(FlexConsts.ENTITY) T entity, @Param(FlexConsts.IGNORE_NULLS) boolean ignoreNulls);

    /**
     * 批量插入实体类数据，只会根据第一条数据来构建插入的字段内容。
     *
     * @param entities 插入的数据列表
     * @return 受影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#insertBatch(Map, ProviderContext)
     * @see com.mybatisflex.core.FlexConsts#METHOD_INSERT_BATCH
     */
    @InsertProvider(type = EntitySqlProvider.class, method = FlexConsts.METHOD_INSERT_BATCH)
    int insertBatch(@Param(FlexConsts.ENTITIES) List<T> entities);

    /**
     * 批量插入实体类数据，按 size 切分。
     *
     * @param entities 插入的数据列表
     * @param size     切分大小
     * @return 受影响的行数
     */
    default int insertBatch(List<T> entities, int size) {

        // 让 insertBatch(List<T> entities, int size) 和 insertBatch(List<T> entities) 保持一样的验证行为
        // https://gitee.com/mybatis-flex/mybatis-flex/issues/I9EGWA
        FlexAssert.notEmpty(entities, "entities");

        if (size <= 0) {
            size = DEFAULT_BATCH_SIZE;
        }
        int sum = 0;
        int entitiesSize = entities.size();
        int maxIndex = entitiesSize / size + (entitiesSize % size == 0 ? 0 : 1);
        for (int i = 0; i < maxIndex; i++) {
            List<T> list = entities.subList(i * size, Math.min(i * size + size, entitiesSize));
            sum += insertBatch(list);
        }
        return sum;
    }

    /**
     * 插入或者更新，若主键有值，则更新，若没有主键值，则插入，插入或者更新都不会忽略 {@code null} 值。
     *
     * @param entity 实体类
     * @return 受影响的行数
     */
    default int insertOrUpdate(T entity) {
        return insertOrUpdate(entity, false);
    }

    /**
     * 插入或者更新，若主键有值，则更新，若没有主键值，则插入，插入或者更新都会忽略 {@code null} 值。
     *
     * @param entity 实体类
     * @return 受影响的行数
     */
    default int insertOrUpdateSelective(T entity) {
        return insertOrUpdate(entity, true);
    }

    /**
     * 插入或者更新，若主键有值，则更新，若没有主键值，则插入。
     *
     * @param entity      实体类
     * @param ignoreNulls 是否忽略 {@code null} 值
     * @return 受影响的行数
     */
    default int insertOrUpdate(T entity, boolean ignoreNulls) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entity.getClass());
        Object[] pkArgs = tableInfo.buildPkSqlArgs(entity);
        if (pkArgs.length == 0 || pkArgs[0] == null) {
            return insert(entity, ignoreNulls);
        } else {
            return update(entity, ignoreNulls);
        }
    }

    // === 删（delete） ===

    /**
     * 根据实体主键来删除数据。
     *
     * @param entity 实体对象，必须包含有主键
     * @return 受影响的行数
     */
    default int delete(T entity) {
        FlexAssert.notNull(entity, "entity can not be null");
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entity.getClass());
        Object[] pkArgs = tableInfo.buildPkSqlArgs(entity);
        return deleteById(pkArgs);
    }

    /**
     * 根据主键删除数据。如果是多个主键的情况下，需要传入数组，例如：{@code new Integer[]{100,101}}。
     *
     * @param id 主键数据
     * @return 受影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#deleteById(Map, ProviderContext)
     */
    @DeleteProvider(type = EntitySqlProvider.class, method = "deleteById")
    int deleteById(@Param(FlexConsts.PRIMARY_VALUE) Serializable id);

    /**
     * 根据多个主键批量删除数据。
     *
     * @param ids 主键列表
     * @return 受影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#deleteBatchByIds(Map, ProviderContext)
     */
    @DeleteProvider(type = EntitySqlProvider.class, method = "deleteBatchByIds")
    int deleteBatchByIds(@Param(FlexConsts.PRIMARY_VALUE) Collection<? extends Serializable> ids);

    /**
     * 根据多个主键批量删除数据。
     *
     * @param ids  主键列表
     * @param size 切分大小
     * @return 受影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#deleteBatchByIds(Map, ProviderContext)
     */
    default int deleteBatchByIds(List<? extends Serializable> ids, int size) {
        if (size <= 0) {
            size = DEFAULT_BATCH_SIZE;
        }
        int sum = 0;
        int entitiesSize = ids.size();
        int maxIndex = entitiesSize / size + (entitiesSize % size == 0 ? 0 : 1);
        for (int i = 0; i < maxIndex; i++) {
            List<? extends Serializable> list = ids.subList(i * size, Math.min(i * size + size, entitiesSize));
            sum += deleteBatchByIds(list);
        }
        return sum;
    }

    /**
     * 根据 Map 构建的条件来删除数据。
     *
     * @param whereConditions 条件
     * @return 受影响的行数
     */
    default int deleteByMap(Map<String, Object> whereConditions) {
        FlexAssert.notEmpty(whereConditions, "whereConditions");
        return deleteByQuery(QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据查询条件来删除数据。
     *
     * @param whereConditions 条件
     * @return 受影响的行数
     */
    default int deleteByCondition(QueryCondition whereConditions) {
        FlexAssert.notNull(whereConditions, "whereConditions");
        return deleteByQuery(QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据查询条件来删除数据。
     *
     * @param queryWrapper 条件
     * @return 受影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#deleteByQuery(Map, ProviderContext)
     */
    @DeleteProvider(type = EntitySqlProvider.class, method = "deleteByQuery")
    int deleteByQuery(@Param(FlexConsts.QUERY) QueryWrapper queryWrapper);

    // === 改（update） ===

    /**
     * 根据主键来更新数据，若实体类属性数据为 {@code null}，该属性不会更新到数据库。
     *
     * @param entity 数据内容，必须包含有主键
     * @return 受影响的行数
     */
    default int update(T entity) {
        return update(entity, true);
    }

    /**
     * 根据主键来更新数据到数据库。
     *
     * @param entity      数据内容，必须包含有主键
     * @param ignoreNulls 是否忽略空内容字段
     * @return 受影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#update(Map, ProviderContext)
     */
    @UpdateProvider(type = EntitySqlProvider.class, method = "update")
    int update(@Param(FlexConsts.ENTITY) T entity, @Param(FlexConsts.IGNORE_NULLS) boolean ignoreNulls);

    /**
     * 根据 Map 构建的条件来更新数据。
     *
     * @param entity          实体类
     * @param whereConditions 条件
     * @return 受影响的行数
     */
    default int updateByMap(T entity, Map<String, Object> whereConditions) {
        FlexAssert.notEmpty(whereConditions, "whereConditions");
        return updateByQuery(entity, QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据 Map 构建的条件来更新数据。
     *
     * @param entity          实体类
     * @param ignoreNulls     是否忽略 {@code null} 数据
     * @param whereConditions 条件
     * @return 受影响的行数
     */
    default int updateByMap(T entity, boolean ignoreNulls, Map<String, Object> whereConditions) {
        FlexAssert.notEmpty(whereConditions, "whereConditions");
        return updateByQuery(entity, ignoreNulls, QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据查询条件来更新数据。
     *
     * @param entity          实体类
     * @param whereConditions 条件
     * @return 受影响的行数
     */
    default int updateByCondition(T entity, QueryCondition whereConditions) {
        FlexAssert.notNull(whereConditions, "whereConditions");
        return updateByQuery(entity, QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据查询条件来更新数据。
     *
     * @param entity          实体类
     * @param ignoreNulls     是否忽略 {@code null} 数据
     * @param whereConditions 条件
     * @return 受影响的行数
     */
    default int updateByCondition(T entity, boolean ignoreNulls, QueryCondition whereConditions) {
        FlexAssert.notNull(whereConditions, "whereConditions");
        return updateByQuery(entity, ignoreNulls, QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据查询条件来更新数据。
     *
     * @param entity       实体类
     * @param queryWrapper 条件
     * @return 受影响的行数
     */
    default int updateByQuery(T entity, QueryWrapper queryWrapper) {
        return updateByQuery(entity, true, queryWrapper);
    }

    /**
     * 根据查询条件来更新数据。
     *
     * @param entity       实体类
     * @param ignoreNulls  是否忽略空值
     * @param queryWrapper 条件
     * @return 受影响的行数
     * @see com.mybatisflex.core.provider.EntitySqlProvider#updateByQuery(Map, ProviderContext)
     */
    @UpdateProvider(type = EntitySqlProvider.class, method = "updateByQuery")
    int updateByQuery(@Param(FlexConsts.ENTITY) T entity, @Param(FlexConsts.IGNORE_NULLS) boolean ignoreNulls, @Param(FlexConsts.QUERY) QueryWrapper queryWrapper);


    // === 查（select） ===

    /**
     * 根据实体主键查询数据。
     *
     * @param entity 实体对象，必须包含有主键
     * @return 实体类数据
     */
    default T selectOneByEntityId(T entity) {
        FlexAssert.notNull(entity, "entity can not be null");
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entity.getClass());
        Object[] pkArgs = tableInfo.buildPkSqlArgs(entity);
        return selectOneById(pkArgs);
    }

    /**
     * 根据主键查询数据。
     *
     * @param id 主键
     * @return 实体类数据
     * @see com.mybatisflex.core.provider.EntitySqlProvider#selectOneById(Map, ProviderContext)
     */
    @SelectProvider(type = EntitySqlProvider.class, method = "selectOneById")
    T selectOneById(@Param(FlexConsts.PRIMARY_VALUE) Serializable id);

    /**
     * 根据 Map 构建的条件来查询数据。
     *
     * @param whereConditions 条件
     * @return 实体类数据
     */
    default T selectOneByMap(Map<String, Object> whereConditions) {
        FlexAssert.notEmpty(whereConditions, "whereConditions");
        return selectOneByQuery(QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据查询条件查询数据。
     *
     * @param whereConditions 条件
     * @return 实体类数据
     */
    default T selectOneByCondition(QueryCondition whereConditions) {
        FlexAssert.notNull(whereConditions, "whereConditions");
        return selectOneByQuery(QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据查询条件来查询 1 条数据。
     *
     * @param queryWrapper 条件
     * @return 实体类数据
     */
    default T selectOneByQuery(QueryWrapper queryWrapper) {
        List<Join> joins = CPI.getJoins(queryWrapper);
        if (CollectionUtil.isNotEmpty(joins)) {
            return MapperUtil.getSelectOneResult(selectListByQuery(queryWrapper));
        }
        Long limitRows = CPI.getLimitRows(queryWrapper);
        try {
            queryWrapper.limit(1);
            return MapperUtil.getSelectOneResult(selectListByQuery(queryWrapper));
        } finally {
            CPI.setLimitRows(queryWrapper, limitRows);
        }
    }

    /**
     * 根据查询条件来查询 1 条数据。
     *
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 实体类数据
     */
    default <R> R selectOneByQueryAs(QueryWrapper queryWrapper, Class<R> asType) {
        List<Join> joins = CPI.getJoins(queryWrapper);
        if (CollectionUtil.isNotEmpty(joins)) {
            return MapperUtil.getSelectOneResult(selectListByQueryAs(queryWrapper, asType));
        }
        Long limitRows = CPI.getLimitRows(queryWrapper);
        try {
            queryWrapper.limit(1);
            return MapperUtil.getSelectOneResult(selectListByQueryAs(queryWrapper, asType));
        } finally {
            CPI.setLimitRows(queryWrapper, limitRows);
        }
    }

    /**
     * 根据 Map 构建的条件来查询 1 条数据。
     *
     * @param whereConditions 条件
     * @return 实体类数据
     */
    default T selectOneWithRelationsByMap(Map<String, Object> whereConditions) {
        FlexAssert.notEmpty(whereConditions, "whereConditions");
        return selectOneWithRelationsByQuery(QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据查询条件查询 1 条数据。
     *
     * @param whereConditions 条件
     * @return 实体类数据
     */
    default T selectOneWithRelationsByCondition(QueryCondition whereConditions) {
        FlexAssert.notNull(whereConditions, "whereConditions");
        return selectOneWithRelationsByQuery(QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据查询条件来查询 1 条数据。
     *
     * @param queryWrapper 条件
     * @return 实体类数据
     */
    default T selectOneWithRelationsByQuery(QueryWrapper queryWrapper) {
        List<Join> joins = CPI.getJoins(queryWrapper);
        if (CollectionUtil.isNotEmpty(joins)) {
            return MapperUtil.queryRelations(this, MapperUtil.getSelectOneResult(selectListByQuery(queryWrapper)));
        }
        Long limitRows = CPI.getLimitRows(queryWrapper);
        try {
            queryWrapper.limit(1);
            return MapperUtil.queryRelations(this, MapperUtil.getSelectOneResult(selectListByQuery(queryWrapper)));
        } finally {
            CPI.setLimitRows(queryWrapper, limitRows);
        }
    }

    /**
     * 根据主表主键来查询 1 条数据。
     *
     * @param id 主表主键
     * @return 实体类数据
     */
    default T selectOneWithRelationsById(Serializable id) {
        return MapperUtil.queryRelations(this, selectOneById(id));
    }

    /**
     * 根据主表主键来查询 1 条数据。
     *
     * @param id     表主键
     * @param asType 接收数据类型
     * @return 实体类数据
     */
    default <R> R selectOneWithRelationsByIdAs(Serializable id, Class<R> asType) {
        try {
            MappedStatementTypes.setCurrentType(asType);
            return (R) selectOneWithRelationsById(id);
        } finally {
            MappedStatementTypes.clear();
        }
    }

    /**
     * 根据查询条件来查询 1 条数据。
     *
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 实体类数据
     */
    default <R> R selectOneWithRelationsByQueryAs(QueryWrapper queryWrapper, Class<R> asType) {
        List<Join> joins = CPI.getJoins(queryWrapper);
        if (CollectionUtil.isNotEmpty(joins)) {
            return MapperUtil.queryRelations(this, MapperUtil.getSelectOneResult(selectListByQueryAs(queryWrapper, asType)));
        }
        Long limitRows = CPI.getLimitRows(queryWrapper);
        try {
            queryWrapper.limit(1);
            return MapperUtil.queryRelations(this, MapperUtil.getSelectOneResult(selectListByQueryAs(queryWrapper, asType)));
        } finally {
            CPI.setLimitRows(queryWrapper, limitRows);
        }
    }

    /**
     * 根据多个主键来查询多条数据。
     *
     * @param ids 主键列表
     * @return 数据列表
     * @see com.mybatisflex.core.provider.EntitySqlProvider#selectListByIds(Map, ProviderContext)
     */
    @SelectProvider(type = EntitySqlProvider.class, method = "selectListByIds")
    List<T> selectListByIds(@Param(FlexConsts.PRIMARY_VALUE) Collection<? extends Serializable> ids);

    /**
     * 根据 Map 来构建查询条件，查询多条数据。
     *
     * @param whereConditions 条件
     * @return 数据列表
     */
    default List<T> selectListByMap(Map<String, Object> whereConditions) {
        FlexAssert.notEmpty(whereConditions, "whereConditions");
        return selectListByQuery(QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据 Map 来构建查询条件，查询多条数据。
     *
     * @param whereConditions 条件
     * @param count           数据量
     * @return 数据列表
     */
    default List<T> selectListByMap(Map<String, Object> whereConditions, Long count) {
        FlexAssert.notEmpty(whereConditions, "whereConditions");
        return selectListByQuery(QueryWrapper.create().where(whereConditions).limit(count));
    }

    /**
     * 根据查询条件查询多条数据。
     *
     * @param whereConditions 条件
     * @return 数据列表
     */
    default List<T> selectListByCondition(QueryCondition whereConditions) {
        FlexAssert.notNull(whereConditions, "whereConditions");
        return selectListByQuery(QueryWrapper.create().where(whereConditions));
    }

    /**
     * 根据查询条件查询多条数据。
     *
     * @param whereConditions 条件
     * @param count           数据量
     * @return 数据列表
     */
    default List<T> selectListByCondition(QueryCondition whereConditions, Long count) {
        FlexAssert.notNull(whereConditions, "whereConditions");
        return selectListByQuery(QueryWrapper.create().where(whereConditions).limit(count));
    }

    /**
     * 根据查询条件查询数据列表。
     *
     * @param queryWrapper 条件
     * @return 数据列表
     * @see com.mybatisflex.core.provider.EntitySqlProvider#selectListByQuery(Map, ProviderContext)
     */
    @SelectProvider(type = EntitySqlProvider.class, method = "selectListByQuery")
    List<T> selectListByQuery(@Param(FlexConsts.QUERY) QueryWrapper queryWrapper);

    /**
     * 根据查询条件查询数据列表。
     *
     * @param queryWrapper 条件
     * @param consumers    字段查询
     * @return 数据列表
     */
    default List<T> selectListByQuery(QueryWrapper queryWrapper, Consumer<FieldQueryBuilder<T>>... consumers) {
        List<T> list = selectListByQuery(queryWrapper);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        MapperUtil.queryFields(this, list, consumers);
        return list;
    }

    /**
     * 根据查询条件查询游标数据，该方法必须在事务中才能正常使用，非事务下无法获取数据。
     *
     * @param queryWrapper 条件
     * @return 游标数据
     */
    @SelectProvider(type = EntitySqlProvider.class, method = "selectListByQuery")
    Cursor<T> selectCursorByQuery(@Param(FlexConsts.QUERY) QueryWrapper queryWrapper);

    /**
     * 根据查询条件查询游标数据，要求返回的数据为 asType 类型。该方法必须在事务中才能正常使用，非事务下无法获取数据。
     *
     * @param queryWrapper 条件
     * @param asType       接收的数据类型
     * @return 游标数据
     */
    default <R> Cursor<R> selectCursorByQueryAs(QueryWrapper queryWrapper, Class<R> asType) {
        try {
            MappedStatementTypes.setCurrentType(asType);
            return (Cursor<R>) selectCursorByQuery(queryWrapper);
        } finally {
            MappedStatementTypes.clear();
        }
    }

    /**
     * 根据查询条件查询 Row 数据。
     *
     * @param queryWrapper 条件
     * @return 行数据
     */
    @SelectProvider(type = EntitySqlProvider.class, method = "selectListByQuery")
    List<Row> selectRowsByQuery(@Param(FlexConsts.QUERY) QueryWrapper queryWrapper);

    /**
     * 根据查询条件查询数据列表，要求返回的数据为 asType。这种场景一般用在 left join 时，
     * 有多出了实体类本身的字段内容，可以转换为 dto、vo 等场景。
     *
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 数据列表
     */
    default <R> List<R> selectListByQueryAs(QueryWrapper queryWrapper, Class<R> asType) {
        if (Number.class.isAssignableFrom(asType)
            || String.class == asType) {
            return selectObjectListByQueryAs(queryWrapper, asType);
        }

        if (Map.class.isAssignableFrom(asType)) {
            return (List<R>) selectRowsByQuery(queryWrapper);
        }

        try {
            MappedStatementTypes.setCurrentType(asType);
            return (List<R>) selectListByQuery(queryWrapper);
        } finally {
            MappedStatementTypes.clear();
        }
    }

    /**
     * 根据查询条件查询数据列表，要求返回的数据为 asType 类型。
     *
     * @param queryWrapper 条件
     * @param asType       接收的数据类型
     * @param consumers    字段查询
     * @return 数据列表
     */
    default <R> List<R> selectListByQueryAs(QueryWrapper queryWrapper, Class<R> asType, Consumer<FieldQueryBuilder<R>>... consumers) {
        List<R> list = selectListByQueryAs(queryWrapper, asType);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        } else {
            MapperUtil.queryFields(this, list, consumers);
            return list;
        }
    }

    /**
     * 查询实体类及其 Relation 注解字段。
     *
     * @param queryWrapper 条件
     */
    default List<T> selectListWithRelationsByQuery(QueryWrapper queryWrapper) {
        return MapperUtil.queryRelations(this, selectListByQuery(queryWrapper));
    }

    /**
     * 查询实体类及其 Relation 注解字段。
     *
     * @param queryWrapper 条件
     * @param asType       要求返回的数据类型
     * @return 数据列表
     */
    default <R> List<R> selectListWithRelationsByQueryAs(QueryWrapper queryWrapper, Class<R> asType) {
        if (Number.class.isAssignableFrom(asType)
            || String.class == asType) {
            return selectObjectListByQueryAs(queryWrapper, asType);
        }

        if (Map.class.isAssignableFrom(asType)) {
            return (List<R>) selectRowsByQuery(queryWrapper);
        }

        List<T> result;
        try {
            MappedStatementTypes.setCurrentType(asType);
            result = selectListByQuery(queryWrapper);
        } finally {
            MappedStatementTypes.clear();
        }
        return MapperUtil.queryRelations(this, (List<R>) result);
    }

    /**
     * 查询实体类及其 Relation 注解字段。
     *
     * @param queryWrapper 条件
     * @param asType       返回的类型
     * @param consumers    字段查询
     * @return 数据列表
     */
    default <R> List<R> selectListWithRelationsByQueryAs(QueryWrapper queryWrapper, Class<R> asType, Consumer<FieldQueryBuilder<R>>... consumers) {
        List<R> list = selectListByQueryAs(queryWrapper, asType);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        } else {
            MapperUtil.queryRelations(this, list);
            MapperUtil.queryFields(this, list, consumers);
            return list;
        }
    }

    /**
     * 查询全部数据。
     *
     * @return 数据列表
     */
    default List<T> selectAll() {
        return selectListByQuery(new QueryWrapper());
    }

    /**
     * 查询全部数据，及其 Relation 字段内容。
     *
     * @return 数据列表
     */
    default List<T> selectAllWithRelations() {
        return MapperUtil.queryRelations(this, selectListByQuery(new QueryWrapper()));
    }

    /**
     * 查询第一列返回的数据，QueryWrapper 执行的结果应该只有 1 列，例如：<br>
     * {@code QueryWrapper.create().select(ACCOUNT.id).where(...);}
     *
     * @param queryWrapper 查询包装器
     * @return 数据量
     */
    default Object selectObjectByQuery(QueryWrapper queryWrapper) {
        return MapperUtil.getSelectOneResult(selectObjectListByQuery(queryWrapper));
    }

    /**
     * 查询第一列返回的数据，QueryWrapper 执行的结果应该只有 1 列，例如：<br>
     * {@code QueryWrapper.create().select(ACCOUNT.id).where(...);}
     *
     * @param queryWrapper 查询包装器
     * @param asType       转换成的数据类型
     * @return 数据量
     */
    default <R> R selectObjectByQueryAs(QueryWrapper queryWrapper, Class<R> asType) {
        return MapperUtil.getSelectOneResult(selectObjectListByQueryAs(queryWrapper, asType));
    }

    /**
     * 查询第一列返回的数据集合，QueryWrapper 执行的结果应该只有 1 列，例如：<br>
     * {@code QueryWrapper.create().select(ACCOUNT.id).where(...);}
     *
     * @param queryWrapper 查询包装器
     * @return 数据列表
     * @see EntitySqlProvider#selectObjectByQuery(Map, ProviderContext)
     */
    @SelectProvider(type = EntitySqlProvider.class, method = "selectObjectByQuery")
    List<Object> selectObjectListByQuery(@Param(FlexConsts.QUERY) QueryWrapper queryWrapper);

    /**
     * 查询第一列返回的数据集合，QueryWrapper 执行的结果应该只有 1 列，例如：<br>
     * {@code QueryWrapper.create().select(ACCOUNT.id).where(...);}
     *
     * @param queryWrapper 查询包装器
     * @param asType       转换成的数据类型
     * @return 数据列表
     */
    default <R> List<R> selectObjectListByQueryAs(QueryWrapper queryWrapper, Class<R> asType) {
        List<Object> queryResults = selectObjectListByQuery(queryWrapper);
        if (queryResults == null || queryResults.isEmpty()) {
            return Collections.emptyList();
        }
        List<R> results = new ArrayList<>(queryResults.size());
        for (Object queryResult : queryResults) {
            results.add((R) ConvertUtil.convert(queryResult, asType));
        }
        return results;
    }

    /**
     * 查询数据量。
     *
     * @param queryWrapper 条件
     * @return 数据量
     */
    default long selectCountByQuery(QueryWrapper queryWrapper) {
        List<QueryColumn> selectColumns = CPI.getSelectColumns(queryWrapper);
        try {
            List<Object> objects;
            if (CollectionUtil.isEmpty(selectColumns)) {
                // 未设置 COUNT(...) 列，默认使用 COUNT(*) 查询
                queryWrapper.select(count());
                objects = selectObjectListByQuery(queryWrapper);
            } else if (selectColumns.get(0) instanceof FunctionQueryColumn) {
                // COUNT 函数必须在第一列
                if (!FuncName.COUNT.equalsIgnoreCase(
                    ((FunctionQueryColumn) selectColumns.get(0)).getFnName()
                )) {
                    // 第一个查询列不是 COUNT 函数，使用 COUNT(*) 替换所有的查询列
                    CPI.setSelectColumns(queryWrapper, Collections.singletonList(count()));
                }
                // 第一个查询列是 COUNT 函数，可以使用 COUNT(1)、COUNT(列名) 代替默认的 COUNT(*)
                objects = selectObjectListByQuery(queryWrapper);
            } else {
                // 查询列中的第一列不是 COUNT 函数
                if (MapperUtil.hasDistinct(selectColumns)) {
                    // 查询列中包含 DISTINCT 去重
                    // 使用子查询 SELECT COUNT(*) FROM (SELECT DISTINCT ...) AS `t`
                    objects = selectObjectListByQuery(MapperUtil.rawCountQueryWrapper(queryWrapper));
                } else {
                    // 使用 COUNT(*) 替换所有的查询列
                    CPI.setSelectColumns(queryWrapper, Collections.singletonList(count()));
                    objects = selectObjectListByQuery(queryWrapper);
                }
            }
            return MapperUtil.getLongNumber(objects);
        } finally {
            // fixed https://github.com/mybatis-flex/mybatis-flex/issues/49
            CPI.setSelectColumns(queryWrapper, selectColumns);
        }
    }

    /**
     * 根据条件查询数据总量。
     *
     * @param whereConditions 条件
     * @return 数据量
     */
    default long selectCountByCondition(QueryCondition whereConditions) {
        FlexAssert.notNull(whereConditions, "whereConditions");
        return selectCountByQuery(QueryWrapper.create().where(whereConditions));
    }

    /**
     * 分页查询。
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param queryWrapper 条件
     * @return 分页数据
     */
    default Page<T> paginate(Number pageNumber, Number pageSize, QueryWrapper queryWrapper) {
        Page<T> page = new Page<>(pageNumber, pageSize);
        return paginate(page, queryWrapper);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param queryWrapper 条件
     * @return 分页数据
     */
    default Page<T> paginateWithRelations(Number pageNumber, Number pageSize, QueryWrapper queryWrapper) {
        Page<T> page = new Page<>(pageNumber, pageSize);
        return paginateWithRelations(page, queryWrapper);
    }

    /**
     * 分页查询。
     *
     * @param pageNumber      当前页码
     * @param pageSize        每页的数据量
     * @param whereConditions 条件
     * @return 分页数据
     */
    default Page<T> paginate(Number pageNumber, Number pageSize, QueryCondition whereConditions) {
        Page<T> page = new Page<>(pageNumber, pageSize);
        return paginate(page, new QueryWrapper().where(whereConditions));
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param pageNumber      当前页码
     * @param pageSize        每页的数据量
     * @param whereConditions 条件
     * @return 分页数据
     */
    default Page<T> paginateWithRelations(Number pageNumber, Number pageSize, QueryCondition whereConditions) {
        Page<T> page = new Page<>(pageNumber, pageSize);
        return paginateWithRelations(page, new QueryWrapper().where(whereConditions));
    }

    /**
     * 分页查询。
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param totalRow     数据总量
     * @param queryWrapper 条件
     * @return 分页数据
     */
    default Page<T> paginate(Number pageNumber, Number pageSize, Number totalRow, QueryWrapper queryWrapper) {
        Page<T> page = new Page<>(pageNumber, pageSize, totalRow);
        return paginate(page, queryWrapper);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param totalRow     数据总量
     * @param queryWrapper 条件
     * @return 分页数据
     */
    default Page<T> paginateWithRelations(Number pageNumber, Number pageSize, Number totalRow, QueryWrapper queryWrapper) {
        Page<T> page = new Page<>(pageNumber, pageSize, totalRow);
        return paginateWithRelations(page, queryWrapper);
    }

    /**
     * 分页查询。
     *
     * @param pageNumber      当前页码
     * @param pageSize        每页的数据量
     * @param totalRow        数据总量
     * @param whereConditions 条件
     * @return 分页数据
     */
    default Page<T> paginate(Number pageNumber, Number pageSize, Number totalRow, QueryCondition whereConditions) {
        FlexAssert.notNull(whereConditions, "whereConditions");
        Page<T> page = new Page<>(pageNumber, pageSize, totalRow);
        return paginate(page, new QueryWrapper().where(whereConditions));
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param pageNumber      当前页码
     * @param pageSize        每页的数据量
     * @param totalRow        数据总量
     * @param whereConditions 条件
     * @return 分页数据
     */
    default Page<T> paginateWithRelations(Number pageNumber, Number pageSize, Number totalRow, QueryCondition whereConditions) {
        FlexAssert.notNull(whereConditions, "whereConditions");
        Page<T> page = new Page<>(pageNumber, pageSize, totalRow);
        return paginateWithRelations(page, new QueryWrapper().where(whereConditions));
    }

    /**
     * 分页查询。
     *
     * @param page         包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 条件
     * @return page 数据
     */
    default Page<T> paginate(Page<T> page, QueryWrapper queryWrapper) {
        return paginateAs(page, queryWrapper, null);
    }

    /**
     * 分页查询。
     *
     * @param page         包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 条件
     * @param consumers    字段查询
     * @return page 数据
     */
    default Page<T> paginate(Page<T> page, QueryWrapper queryWrapper, Consumer<FieldQueryBuilder<T>>... consumers) {
        return paginateAs(page, queryWrapper, null, consumers);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param page         包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 条件
     * @return 分页数据
     */
    default Page<T> paginateWithRelations(Page<T> page, QueryWrapper queryWrapper) {
        return paginateWithRelationsAs(page, queryWrapper, null);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param page         包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 条件
     * @param consumers    字段查询
     * @return 分页数据
     */
    default Page<T> paginateWithRelations(Page<T> page, QueryWrapper queryWrapper, Consumer<FieldQueryBuilder<T>>... consumers) {
        return paginateWithRelationsAs(page, queryWrapper, null, consumers);
    }

    /**
     * 分页查询。
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 分页数据
     */
    default <R> Page<R> paginateAs(Number pageNumber, Number pageSize, QueryWrapper queryWrapper, Class<R> asType) {
        Page<R> page = new Page<>(pageNumber, pageSize);
        return MapperUtil.doPaginate(this, page, queryWrapper, asType, false);
    }

    /**
     * 分页查询。
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param totalRow     数据总量
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 分页数据
     */
    default <R> Page<R> paginateAs(Number pageNumber, Number pageSize, Number totalRow, QueryWrapper queryWrapper, Class<R> asType) {
        Page<R> page = new Page<>(pageNumber, pageSize, totalRow);
        return MapperUtil.doPaginate(this, page, queryWrapper, asType, false);
    }

    /**
     * 分页查询。
     *
     * @param page         包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 分页数据
     */
    default <R> Page<R> paginateAs(Page<R> page, QueryWrapper queryWrapper, Class<R> asType) {
        return MapperUtil.doPaginate(this, page, queryWrapper, asType, false);
    }

    /**
     * 分页查询。
     *
     * @param page         包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @param consumers    字段查询
     * @return 分页数据
     */
    default <R> Page<R> paginateAs(Page<R> page, QueryWrapper queryWrapper, Class<R> asType, Consumer<FieldQueryBuilder<R>>... consumers) {
        return MapperUtil.doPaginate(this, page, queryWrapper, asType, false, consumers);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 分页数据
     */
    default <R> Page<R> paginateWithRelationsAs(Number pageNumber, Number pageSize, QueryWrapper queryWrapper, Class<R> asType) {
        Page<R> page = new Page<>(pageNumber, pageSize);
        return MapperUtil.doPaginate(this, page, queryWrapper, asType, true);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param totalRow     数据总量
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 分页数据
     */
    default <R> Page<R> paginateWithRelationsAs(Number pageNumber, Number pageSize, Number totalRow, QueryWrapper queryWrapper, Class<R> asType) {
        Page<R> page = new Page<>(pageNumber, pageSize, totalRow);
        return MapperUtil.doPaginate(this, page, queryWrapper, asType, true);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param page         包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 分页数据
     */
    default <R> Page<R> paginateWithRelationsAs(Page<R> page, QueryWrapper queryWrapper, Class<R> asType) {
        return MapperUtil.doPaginate(this, page, queryWrapper, asType, true);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param page         包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @param consumers    字段查询
     * @return 分页数据
     */
    default <R> Page<R> paginateWithRelationsAs(Page<R> page, QueryWrapper queryWrapper, Class<R> asType, Consumer<FieldQueryBuilder<R>>... consumers) {
        return MapperUtil.doPaginate(this, page, queryWrapper, asType, true, consumers);
    }


    default <E> Page<E> xmlPaginate(String dataSelectId, Page<E> page, QueryWrapper queryWrapper) {
        return xmlPaginate(dataSelectId, dataSelectId + "_COUNT", page, queryWrapper, null);
    }

    default <E> Page<E> xmlPaginate(String dataSelectId, Page<E> page, Map<String, Object> otherParams) {
        return xmlPaginate(dataSelectId, dataSelectId + "_COUNT", page, null, otherParams);
    }

    default <E> Page<E> xmlPaginate(String dataSelectId, Page<E> page, QueryWrapper queryWrapper, Map<String, Object> otherParams) {
        return xmlPaginate(dataSelectId, dataSelectId + "_COUNT", page, queryWrapper, otherParams);
    }

    default <E> Page<E> xmlPaginate(String dataSelectId, String countSelectId, Page<E> page, QueryWrapper queryWrapper, Map<String, Object> otherParams) {
        SqlSessionFactory sqlSessionFactory = FlexGlobalConfig.getDefaultConfig().getSqlSessionFactory();
        ExecutorType executorType = FlexGlobalConfig.getDefaultConfig().getConfiguration().getDefaultExecutorType();
        String mapperClassName = ClassUtil.getUsefulClass(this.getClass()).getName();

        Map<String, Object> preparedParams = MapperUtil.preparedParams(this, page, queryWrapper, otherParams);
        if (!dataSelectId.contains(".")) {
            dataSelectId = mapperClassName + "." + dataSelectId;
        }

        try (SqlSession sqlSession = sqlSessionFactory.openSession(executorType, false)) {
            if (page.getTotalRow() < 0) {
                if (!countSelectId.contains(".")) {
                    countSelectId = mapperClassName + "." + countSelectId;
                }
                Number number = sqlSession.selectOne(countSelectId, preparedParams);
                page.setTotalRow(number == null ? Page.INIT_VALUE : number.longValue());
            }

            if (page.hasRecords()) {
                List<E> entities = sqlSession.selectList(dataSelectId, preparedParams);
                page.setRecords(entities);
            }
        }
        return page;
    }

}
