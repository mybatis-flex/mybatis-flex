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
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.transaction.TransactionContext;
import com.mybatisflex.core.transaction.TransactionalManager;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.util.MapUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 针对 RowMapper 的静态方法进行封装
 */
public class Db {

    private static final Map<String, RowMapperInvoker> INVOKER_MAP = new ConcurrentHashMap<>();
    static RowMapperInvoker defaultRowMapperInvoker;

    public static RowMapperInvoker invoker() {
        if (defaultRowMapperInvoker == null) {
            FlexGlobalConfig defaultConfig = FlexGlobalConfig.getDefaultConfig();
            SqlSessionFactory sqlSessionFactory = defaultConfig.getSqlSessionFactory();
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


    /**
     * 网 tableName 插入一条 row 数据
     *
     * @param tableName 表名
     * @param row       数据
     */
    public static int insert(String tableName, Row row) {
        return invoker().insert(tableName, row);
    }


    /**
     * 直接编写 sql 插入数据
     *
     * @param sql  sql 内容
     * @param args sql 参数
     */
    public static int insertBySql(String sql, Object... args) {
        return invoker().insertBySql(sql, args);
    }


    /**
     * 批量插入数据
     *
     * @param tableName 表名
     * @param rows      数据
     */
    public static int[] insertBatch(String tableName, Collection<Row> rows) {
        return insertBatch(tableName, rows, rows.size());
    }

    /**
     * 批量插入数据
     *
     * @param tableName 表名
     * @param rows      数据
     * @param batchSize 每次提交的数据量
     */
    public static int[] insertBatch(String tableName, Collection<Row> rows, int batchSize) {
        return invoker().insertBatch(tableName, rows, batchSize);
    }

    /**
     * 批量插入数据，根据第一条内容来构建插入的字段，效率比 {@link #insertBatch(String, Collection, int)} 高
     *
     * @param tableName 表名
     * @param rows      数据
     */
    public static int insertBatchWithFirstRowColumns(String tableName, List<Row> rows) {
        return invoker().insertBatchWithFirstRowColumns(tableName, rows);
    }

    /**
     * 编写 delete sql 来删除数据
     *
     * @param sql  sql 内容
     * @param args 参数
     */
    public static int deleteBySql(String sql, Object... args) {
        return invoker().deleteBySql(sql, args);
    }

    /**
     * 根据主键来删除数据，其中 row 是通过 {@link Row#ofKey(RowKey, Object)} 来进行构建的
     *
     * @param tableName 表名
     * @param row       主键 和 id值
     */
    public static int deleteById(String tableName, Row row) {
        return invoker().deleteById(tableName, row);
    }

    /**
     * 根据主键来删除 1 条数据
     *
     * @param tableName  表名
     * @param primaryKey 主键字段名称
     * @param id         主键值
     */
    public static int deleteById(String tableName, String primaryKey, Object id) {
        return invoker().deleteById(tableName, primaryKey, id);
    }

    /**
     * 根据 id 集合来批量删除数据
     *
     * @param tableName  表名
     * @param primaryKey 主键字段名称
     * @param ids        id 集合
     */
    public static int deleteBatchByIds(String tableName, String primaryKey, Collection<?> ids) {
        return invoker().deleteBatchByIds(tableName, primaryKey, ids);
    }

    /**
     * 根据 map 构建的 where 条件来删除数据
     *
     * @param tableName    表名
     * @param whereColumns where 条件
     */
    public static int deleteByMap(String tableName, Map<String, Object> whereColumns) {
        return invoker().deleteByMap(tableName, whereColumns);
    }

    /**
     * 根据 query 构建的条件来删除数据
     *
     * @param tableName    表名
     * @param queryWrapper query
     */
    public static int deleteByQuery(String tableName, QueryWrapper queryWrapper) {
        return invoker().deleteByQuery(tableName, queryWrapper);
    }


    /**
     * 根据原生 sql 来更新数据
     *
     * @param sql  sql 内容
     * @param args sql 参数
     */
    public static int updateBySql(String sql, Object... args) {
        return invoker().updateBySql(sql, args);
    }


    /**
     * 根据 id 来更新数据
     *
     * @param tableName 表情
     * @param row       id 及其内容
     */
    public static int updateById(String tableName, Row row) {
        return invoker().updateById(tableName, row);
    }

    /**
     * 根据 map 构建的条件来更新数据
     *
     * @param tableName    表名
     * @param data         数据内容
     * @param whereColumns where 条件
     */
    public static int updateByMap(String tableName, Row data, Map<String, Object> whereColumns) {
        return invoker().updateByMap(tableName, data, whereColumns);
    }


    /**
     * 根据 query 构建的条件来更新数据
     *
     * @param tableName    表名
     * @param data         数据内容
     * @param queryWrapper queryWrapper 条件
     */
    public static int updateByQuery(String tableName, Row data, QueryWrapper queryWrapper) {
        return invoker().updateByQuery(tableName, data, queryWrapper);
    }


    /**
     * 根据主键来批量更新数据
     *
     * @param tableName 表名
     * @param rows      还有主键的数据
     */
    public static int updateBatchById(String tableName, List<Row> rows) {
        return invoker().updateBatchById(tableName, rows);
    }


    /**
     * 根据 sql 来查询 1 条数据
     *
     * @param sql  sql 内容
     * @param args sql 参数
     */
    public static Row selectOneBySql(String sql, Object... args) {
        return invoker().selectOneBySql(sql, args);
    }


    /**
     * 根据 id 来查询 1 条数据
     *
     * @param tableName 表名
     * @param row       主键和 id 值
     */
    public static Row selectOneById(String tableName, Row row) {
        return invoker().selectOneById(tableName, row);
    }


    /**
     * 根据主键来查询 1 条数据
     *
     * @param tableName  表名
     * @param primaryKey 主键字段名称
     * @param id         主键值
     */
    public static Row selectOneById(String tableName, String primaryKey, Object id) {
        return invoker().selectOneById(tableName, primaryKey, id);
    }


    /**
     * 根据 map 来查询 1 条数据
     *
     * @param tableName    表名
     * @param whereColumns where条件
     */
    public static Row selectOneByMap(String tableName, Map whereColumns) {
        return invoker().selectOneByMap(tableName, whereColumns);
    }


    /**
     * 根据 queryWrapper 来查询 1 条数据
     *
     * @param tableName    表名
     * @param queryWrapper queryWrapper
     */
    public static Row selectOneByQuery(String tableName, QueryWrapper queryWrapper) {
        return invoker().selectOneByQuery(tableName, queryWrapper);
    }


    /**
     * 通过 sql 来查询多条数据
     *
     * @param sql  sql 内容
     * @param args sql 参数
     */
    public static List<Row> selectListBySql(String sql, Object... args) {
        return invoker().selectListBySql(sql, args);
    }


    /**
     * 通过 map 构建的条件来查询数据列表
     *
     * @param tableName    表名
     * @param whereColumns where 条件
     */
    public static List<Row> selectListByMap(String tableName, Map<String, Object> whereColumns) {
        return invoker().selectListByMap(tableName, whereColumns);
    }


    /**
     * 通过 query 来查询数据列表
     *
     * @param tableName    表名
     * @param queryWrapper query 条件
     */
    public static List<Row> selectListByQuery(String tableName, QueryWrapper queryWrapper) {
        return invoker().selectListByQuery(tableName, queryWrapper);
    }

    /**
     * 查询某张表的所有数据
     *
     * @param tableName 表名
     */
    public static List<Row> selectAll(String tableName) {
        return invoker().selectAll(tableName);
    }

    /**
     * 查询某个内容，数据返回的应该只有 1 行 1 列
     *
     * @param sql  sql 内容
     * @param args sql 参数
     */
    public static Object selectObject(String sql, Object... args) {
        return invoker().selectObject(sql, args);
    }


    /**
     * 查询某列内容，数据返回应该有 多行 1 列
     *
     * @param sql  sql 内容
     * @param args sql 参数
     */
    public static List<Object> selectObjectList(String sql, Object... args) {
        return invoker().selectObjectList(sql, args);
    }


    /**
     * 查收 count 数据，一般用于 select count(*)...
     * 或者返回的内容是一行1列，且是数值类型的也可以用此方法
     *
     * @param sql  sql 内容
     * @param args sql 参数
     */
    public static long selectCount(String sql, Object... args) {
        return invoker().selectCount(sql, args);
    }


    /**
     * 根据 query 构建的条件来查询数据量
     *
     * @param tableName    表名
     * @param queryWrapper query 条件
     */
    public static long selectCountByQuery(String tableName, QueryWrapper queryWrapper) {
        return invoker().selectCountByQuery(tableName, queryWrapper);
    }


    /**
     * 分页查询
     *
     * @param tableName    表名
     * @param pageNumber   当前的页码
     * @param pageSize     每页的数据量
     * @param queryWrapper 条件
     */
    public static Page<Row> paginate(String tableName, int pageNumber, int pageSize, QueryWrapper queryWrapper) {
        return invoker().paginate(tableName, pageNumber, pageSize, queryWrapper);
    }


    /**
     * 分页查询
     *
     * @param tableName    表名
     * @param page         page 对象，若 page 有 totalCount 值，则不会再去查询分类的数据总量
     * @param queryWrapper 条件
     */
    public static Page<Row> paginate(String tableName, Page<Row> page, QueryWrapper queryWrapper) {
        return invoker().paginate(tableName, page, queryWrapper);
    }


    public static boolean tx(Supplier<Boolean> supplier) {
        //上一级事务的id，支持事务嵌套
        String higherXID = TransactionContext.getXID();
        try {
            String xid = TransactionalManager.startTransactional();
            Boolean success = false;
            boolean rollbacked = false;
            try {
                success = supplier.get();
            } catch (Exception e) {
                rollbacked = true;
                TransactionalManager.rollback(xid);
                e.printStackTrace();
            } finally {
                if (success != null && success) {
                    TransactionalManager.commit(xid);
                } else if (!rollbacked) {
                    TransactionalManager.rollback(xid);
                }
            }
            return success != null && success;
        } finally {
            //恢复上一级事务
            if (higherXID != null) {
                TransactionContext.hold(higherXID);
            }
        }
    }
}
