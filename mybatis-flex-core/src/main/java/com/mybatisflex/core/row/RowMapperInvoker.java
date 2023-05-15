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

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class RowMapperInvoker {

    private final SqlSessionFactory sqlSessionFactory;

    public RowMapperInvoker(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    private <R> R execute(Function<RowMapper, R> function) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            RowMapper mapper = sqlSession.getMapper(RowMapper.class);
            return function.apply(mapper);
        }
    }

    public int insert(String tableName, Row row) {
        return execute(mapper -> mapper.insert(tableName, row));
    }


    public int insertBySql(String sql, Object... args) {
        return execute(mapper -> mapper.insertBySql(sql, args));
    }


    public int[] insertBatch(String tableName, Collection<Row> rows, int batchSize) {
        int[] results = new int[rows.size()];
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, true)) {
            RowMapper mapper = sqlSession.getMapper(RowMapper.class);
            int counter = 0;
            int resultsPos = 0;
            for (Row row : rows) {
                if (++counter > batchSize) {
                    counter = 0;
                    List<BatchResult> batchResults = sqlSession.flushStatements();
                    for (BatchResult batchResult : batchResults) {
                        int[] updateCounts = batchResult.getUpdateCounts();
                        for (int updateCount : updateCounts) {
                            results[resultsPos++] = updateCount;
                        }
                    }
                } else {
                    mapper.insert(tableName, row);
                }
            }

            if (counter != 0) {
                List<BatchResult> batchResults = sqlSession.flushStatements();
                for (BatchResult batchResult : batchResults) {
                    int[] updateCounts = batchResult.getUpdateCounts();
                    for (int updateCount : updateCounts) {
                        results[resultsPos++] = updateCount;
                    }
                }
            }
        }
        return results;
    }

    public int insertBatchWithFirstRowColumns(String tableName, List<Row> rows) {
        return execute(mapper -> mapper.insertBatchWithFirstRowColumns(tableName, rows));
    }

    public int deleteBySql(String sql, Object... args) {
        return execute(mapper -> mapper.deleteBySql(sql, args));
    }

    public int deleteById(String tableName, Row row) {
        return execute(mapper -> mapper.deleteById(tableName, row));
    }

    public int deleteById(String tableName, String primaryKey, Object id) {
        return execute(mapper -> mapper.deleteById(tableName, primaryKey, id));
    }

    public int deleteBatchByIds(String tableName, String primaryKey, Collection<?> ids) {
        return execute(mapper -> mapper.deleteBatchByIds(tableName, primaryKey, ids));
    }


    public int deleteByQuery(String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.deleteByQuery(tableName, queryWrapper));
    }

    public int updateBySql(String sql, Object... args) {
        return execute(mapper -> mapper.updateBySql(sql, args));
    }

    public int updateById(String tableName, Row row) {
        return execute(mapper -> mapper.updateById(tableName, row));
    }

    public int updateByQuery(String tableName, Row data, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.updateByQuery(tableName, data, queryWrapper));
    }

    public int updateBatchById(String tableName, List<Row> rows) {
        return execute(mapper -> mapper.updateBatchById(tableName, rows));
    }

    public Row selectOneBySql(String sql, Object... args) {
        return execute(mapper -> mapper.selectOneBySql(sql, args));
    }

    public Row selectOneById(String tableName, Row row) {
        return execute(mapper -> mapper.selectOneById(tableName, row));
    }

    public Row selectOneById(String tableName, String primaryKey, Object id) {
        return execute(mapper -> mapper.selectOneById(tableName, primaryKey, id));
    }

    public Row selectOneByQuery(String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.selectOneByQuery(tableName, queryWrapper));
    }

    public List<Row> selectListBySql(String sql, Object... args) {
        return execute(mapper -> mapper.selectListBySql(sql, args));
    }

    public List<Row> selectListByQuery(String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.selectListByQuery(tableName, queryWrapper));
    }

    public List<Row> selectAll(String tableName) {
        return execute(mapper -> mapper.selectAll(tableName));
    }

    public Object selectObjectByQuery(String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.selectObjectByQuery(tableName, queryWrapper));
    }

    public List<Object> selectObjectListByQuery(String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.selectObjectListByQuery(tableName, queryWrapper));
    }

    public Object selectObject(String sql, Object... args) {
        return execute(mapper -> mapper.selectObject(sql, args));
    }

    public List<Object> selectObjectList(String sql, Object... args) {
        return execute(mapper -> mapper.selectObjectList(sql, args));
    }

    public long selectCount(String sql, Object... args) {
        return execute(mapper -> mapper.selectCount(sql, args));
    }


    public long selectCountByQuery(String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.selectCountByQuery(tableName, queryWrapper));
    }

    public Page<Row> paginate(String tableName, Page<Row> page, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.paginate(tableName, page, queryWrapper));
    }


}
