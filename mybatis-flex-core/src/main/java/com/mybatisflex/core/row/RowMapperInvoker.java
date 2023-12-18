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
package com.mybatisflex.core.row;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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

    public int insert(String schema, String tableName, Row row) {
        return execute(mapper -> mapper.insert(schema, tableName, row));
    }


    public int insertBySql(String sql, Object... args) {
        return execute(mapper -> mapper.insertBySql(sql, args));
    }

    public int insertBatchWithFirstRowColumns(String schema, String tableName, List<Row> rows) {
        return execute(mapper -> mapper.insertBatchWithFirstRowColumns(schema, tableName, rows));
    }

    public int deleteBySql(String sql, Object... args) {
        return execute(mapper -> mapper.deleteBySql(sql, args));
    }

    public int deleteById(String schema, String tableName, Row row) {
        return execute(mapper -> mapper.deleteById(schema, tableName, row));
    }

    public int deleteById(String schema, String tableName, String primaryKey, Object id) {
        return execute(mapper -> mapper.deleteById(schema, tableName, primaryKey, id));
    }

    public int deleteBatchByIds(String schema, String tableName, String primaryKey, Collection<?> ids) {
        return execute(mapper -> mapper.deleteBatchByIds(schema, tableName, primaryKey, ids));
    }


    public int deleteByQuery(String schema, String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.deleteByQuery(schema, tableName, queryWrapper));
    }

    public int updateBySql(String sql, Object... args) {
        return execute(mapper -> mapper.updateBySql(sql, args));
    }


    public <M, E> int[] executeBatch(Collection<E> datas, int batchSize, Class<M> mapperClass, BiConsumer<M, E> consumer) {
        int[] results = new int[datas.size()];
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, true)) {
            M mapper = sqlSession.getMapper(mapperClass);
            int counter = 0;
            int resultsPos = 0;
            for (E data : datas) {
                consumer.accept(mapper, data);
                if (++counter == batchSize) {
                    counter = 0;
                    List<BatchResult> batchResults = sqlSession.flushStatements();
                    for (BatchResult batchResult : batchResults) {
                        int[] updateCounts = batchResult.getUpdateCounts();
                        for (int updateCount : updateCounts) {
                            results[resultsPos++] = updateCount;
                        }
                    }
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



    public <M> int[] executeBatch(int totalSize, int batchSize, Class<M> mapperClass, BiConsumer<M, Integer> consumer) {
        int[] results = new int[totalSize];
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, true)) {
            M mapper = sqlSession.getMapper(mapperClass);
            int counter = 0;
            int resultsPos = 0;
            for (int i = 0; i < totalSize; i++) {
                consumer.accept(mapper, i);
                if (++counter == batchSize) {
                    counter = 0;
                    List<BatchResult> batchResults = sqlSession.flushStatements();
                    for (BatchResult batchResult : batchResults) {
                        int[] updateCounts = batchResult.getUpdateCounts();
                        for (int updateCount : updateCounts) {
                            results[resultsPos++] = updateCount;
                        }
                    }
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

    public int updateById(String schema, String tableName, Row row) {
        return execute(mapper -> mapper.updateById(schema, tableName, row));
    }

    public int updateByQuery(String schema, String tableName, Row data, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.updateByQuery(schema, tableName, data, queryWrapper));
    }

    public int updateBatchById(String schema, String tableName, List<Row> rows) {
        return execute(mapper -> mapper.updateBatchById(schema, tableName, rows));
    }

    public Row selectOneBySql(String sql, Object... args) {
        return execute(mapper -> mapper.selectOneBySql(sql, args));
    }

    public Row selectOneById(String schema, String tableName, Row row) {
        return execute(mapper -> mapper.selectOneById(schema, tableName, row));
    }

    public Row selectOneById(String schema, String tableName, String primaryKey, Object id) {
        return execute(mapper -> mapper.selectOneById(schema, tableName, primaryKey, id));
    }

    public Row selectOneByQuery(String schema, String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.selectOneByQuery(schema, tableName, queryWrapper));
    }

    public List<Row> selectListBySql(String sql, Object... args) {
        return execute(mapper -> mapper.selectListBySql(sql, args));
    }

    public List<Row> selectListByQuery(String schema, String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.selectListByQuery(schema, tableName, queryWrapper));
    }

    public List<Row> selectAll(String schema, String tableName) {
        return execute(mapper -> mapper.selectAll(schema, tableName));
    }
    public Map selectFirstAndSecondColumnsAsMapByQuery(String schema, String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.selectFirstAndSecondColumnsAsMapByQuery(schema, tableName, queryWrapper));
    }
    public Map selectFirstAndSecondColumnsAsMap(String sql, Object... args) {
        return execute(mapper -> mapper.selectFirstAndSecondColumnsAsMap(sql, args));
    }
    public Object selectObjectByQuery(String schema, String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.selectObjectByQuery(schema, tableName, queryWrapper));
    }

    public List<Object> selectObjectListByQuery(String schema, String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.selectObjectListByQuery(schema, tableName, queryWrapper));
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


    public long selectCountByQuery(String schema, String tableName, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.selectCountByQuery(schema, tableName, queryWrapper));
    }

    public Page<Row> paginate(String schema, String tableName, Page<Row> page, QueryWrapper queryWrapper) {
        return execute(mapper -> mapper.paginate(schema, tableName, page, queryWrapper));
    }

}
