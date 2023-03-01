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
package com.mybatisflex.core.row;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.querywrapper.QueryWrapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.util.MapUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 针对 RowMapper 的静态方法进行封装
 */
public class Db {

    private static final Map<String, RowMapperInvoker> INVOKER_MAP = new ConcurrentHashMap<>();
    static RowMapperInvoker defaultRowMapperInvoker;

    public static RowMapperInvoker invoker() {
        if (defaultRowMapperInvoker == null) {
            SqlSessionFactory sqlSessionFactory = FlexGlobalConfig.getDefaultConfig().getSqlSessionFactory();
            defaultRowMapperInvoker = new RowMapperInvoker(sqlSessionFactory);
        }
        return defaultRowMapperInvoker;
    }

    public static RowMapperInvoker invoker(String environmentId) {
        return MapUtil.computeIfAbsent(INVOKER_MAP, environmentId, key -> {
            SqlSessionFactory sqlSessionFactory = FlexGlobalConfig.getConfig(key).getSqlSessionFactory();
            return new RowMapperInvoker(sqlSessionFactory);
        });
    }

    public static int insertBySql(String sql, Object... args) {
        return invoker().insertBySql(sql, args);
    }

    public static int insertRow(String tableName, Row row) {
        return invoker().insertRow(tableName, row);
    }

    public static int[] insertBatch(String tableName, Collection<Row> rows) {
        return insertBatch(tableName, rows, rows.size());
    }

    public static int[] insertBatch(String tableName, Collection<Row> rows, int batchSize) {
        return invoker().insertBatch(tableName, rows, batchSize);
    }

    public static int insertBatchWithFirstRowColumns(String tableName, List<Row> rows) {
        return invoker().insertBatchWithFirstRowColumns(tableName, rows);
    }

    public static int deleteBySql(String sql, Object... args) {
        return invoker().deleteBySql(sql, args);
    }

    public static int deleteById(String tableName, Row row) {
        return invoker().deleteById(tableName, row);
    }

    public static int deleteById(String tableName, String primaryKey, Object id) {
        return invoker().deleteById(tableName, primaryKey, id);
    }

    public static int deleteBatchByIds(String tableName, String primaryKey, Collection<?> ids) {
        return invoker().deleteBatchByIds(tableName, primaryKey, ids);
    }

    public static int deleteByByMap(String tableName, Map<String, Object> whereColumns) {
        return invoker().deleteByByMap(tableName, whereColumns);
    }

    public static int deleteByQuery(String tableName, QueryWrapper queryWrapper) {
        return invoker().deleteByQuery(tableName, queryWrapper);
    }

    public static int updateBySql(String sql, Object... args) {
        return invoker().updateBySql(sql, args);
    }

    public static int updateById(String tableName, Row row) {
        return invoker().updateById(tableName, row);
    }

    public static int updateByMap(String tableName, Row data, Map<String, Object> whereColumns) {
        return invoker().updateByMap(tableName, data, whereColumns);
    }

    public static int updateByQuery(String tableName, Row data, QueryWrapper queryWrapper) {
        return invoker().updateByQuery(tableName, data, queryWrapper);
    }

    public static int updateBatchById(String tableName, List<Row> rows) {
        return invoker().updateBatchById(tableName, rows);
    }

    public static Row selectOneBySql(String sql, Object... args) {
        return invoker().selectOneBySql(sql, args);
    }

    public static Row selectOneById(String tableName, Row row) {
        return invoker().selectOneById(tableName, row);
    }

    public static Row selectOneById(String tableName, String primaryKey, Object id) {
        return invoker().selectOneById(tableName, primaryKey, id);
    }

    public static Row selectOneByMap(String tableName, Map whereColumns) {
        return invoker().selectOneByMap(tableName, whereColumns);
    }

    public static Row selectOneByQuery(String tableName, QueryWrapper queryWrapper) {
        return invoker().selectOneByQuery(tableName, queryWrapper);
    }

    public static List<Row> selectListBySql(String sql, Object... args) {
        return invoker().selectListBySql(sql, args);
    }

    public static List<Row> selectListByMap(String tableName, Map<String, Object> whereColumns) {
        return invoker().selectListByMap(tableName, whereColumns);
    }

    public static List<Row> selectListByQuery(String tableName, QueryWrapper queryWrapper) {
        return invoker().selectListByQuery(tableName, queryWrapper);
    }

    public static List<Row> selectAll(String tableName) {
        return invoker().selectAll(tableName);
    }

    public static Object selectObject(String sql, Object... args) {
        return invoker().selectObject(sql, args);
    }

    public static List<Object> selectObjectList(String sql, Object... args) {
        return invoker().selectObjectList(sql, args);
    }

    public static long selectCount(String sql, Object... args) {
        return invoker().selectCount(sql, args);
    }

    public static long selectCountByQuery(String tableName, QueryWrapper queryWrapper) {
        return invoker().selectCountByQuery(tableName, queryWrapper);
    }

    public static Page<Row> paginate(String tableName, int pageNumber, int pageSize, QueryWrapper queryWrapper) {
        return invoker().paginate(tableName, pageNumber, pageSize, queryWrapper);
    }

    public static Page<Row> paginate(String tableName, Page<Row> page, QueryWrapper queryWrapper) {
        return invoker().paginate(tableName, page, queryWrapper);
    }
}
