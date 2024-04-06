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
package com.mybatisflex.core.util;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.field.FieldQuery;
import com.mybatisflex.core.field.FieldQueryBuilder;
import com.mybatisflex.core.field.FieldQueryManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.DistinctQueryColumn;
import com.mybatisflex.core.query.Join;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.mybatisflex.core.query.QueryMethods.count;

public class MapperUtil {

    private MapperUtil() {
    }


    /**
     * <p>原生的、未经过优化的 COUNT 查询。抛开效率问题不谈，只关注结果的准确性，
     * 这个 COUNT 查询查出来的分页总数据是 100% 正确的，不接受任何反驳。
     *
     * <p>为什么这么说，因为是用子查询实现的，生成的 SQL 如下：
     *
     * <p><pre>
     * {@code
     * SELECT COUNT(*) AS `total` FROM ( ...用户构建的 SQL 语句... ) AS `t`;
     * }
     * </pre>
     *
     * <p>不进行 SQL 优化的时候，返回的就是这样的 COUNT 查询语句。
     */
    public static QueryWrapper rawCountQueryWrapper(QueryWrapper queryWrapper) {
        return QueryWrapper.create()
            .select(count().as("total"))
            .from(queryWrapper).as("t");
    }

    /**
     * 优化 COUNT 查询语句。
     */
    public static QueryWrapper optimizeCountQueryWrapper(QueryWrapper queryWrapper) {
        // 对克隆对象进行操作，不影响原来的 QueryWrapper 对象
        QueryWrapper clone = queryWrapper.clone();
        // 将最后面的 order by 移除掉
        CPI.setOrderBys(clone, null);
        // 获取查询列和分组列，用于判断是否进行优化
        List<QueryColumn> selectColumns = CPI.getSelectColumns(clone);
        List<QueryColumn> groupByColumns = CPI.getGroupByColumns(clone);
        // 如果有 distinct 语句或者 group by 语句则不优化
        // 这种一旦优化了就会造成 count 语句查询出来的值不对
        if (hasDistinct(selectColumns) || hasGroupBy(groupByColumns)) {
            return rawCountQueryWrapper(clone);
        }
        // 判断能不能清除 join 语句
        if (canClearJoins(clone)) {
            CPI.setJoins(clone, null);
        }
        // 将 select 里面的列换成 COUNT(*) AS `total`
        CPI.setSelectColumns(clone, Collections.singletonList(count().as("total")));
        return clone;
    }

    public static boolean hasDistinct(List<QueryColumn> selectColumns) {
        if (CollectionUtil.isEmpty(selectColumns)) {
            return false;
        }
        for (QueryColumn selectColumn : selectColumns) {
            if (selectColumn instanceof DistinctQueryColumn) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasGroupBy(List<QueryColumn> groupByColumns) {
        return CollectionUtil.isNotEmpty(groupByColumns);
    }

    private static boolean canClearJoins(QueryWrapper queryWrapper) {
        List<Join> joins = CPI.getJoins(queryWrapper);
        if (CollectionUtil.isEmpty(joins)) {
            return false;
        }

        // 只有全是 left join 语句才会清除 join
        // 因为如果是 inner join 或 right join 往往都会放大记录数
        for (Join join : joins) {
            if (!SqlConsts.LEFT_JOIN.equals(CPI.getJoinType(join))) {
                return false;
            }
        }

        // 获取 join 语句中使用到的表名
        List<String> joinTables = new ArrayList<>();
        joins.forEach(join -> {
            QueryTable joinQueryTable = CPI.getJoinQueryTable(join);
            if (joinQueryTable != null) {
                String tableName = joinQueryTable.getName();
                if (StringUtil.isNotBlank(joinQueryTable.getAlias())) {
                    joinTables.add(tableName + "." + joinQueryTable.getAlias());
                } else {
                    joinTables.add(tableName);
                }
            }
        });

        // 获取 where 语句中的条件
        QueryCondition where = CPI.getWhereQueryCondition(queryWrapper);

        // 最后判断一下 where 中是否用到了 join 的表
        return !CPI.containsTable(where, CollectionUtil.toArrayString(joinTables));
    }

    @SafeVarargs
    public static <T, R> Page<R> doPaginate(
        BaseMapper<T> mapper,
        Page<R> page,
        QueryWrapper queryWrapper,
        Class<R> asType,
        boolean withRelations,
        Consumer<FieldQueryBuilder<R>>... consumers
    ) {
        Long limitRows = CPI.getLimitRows(queryWrapper);
        Long limitOffset = CPI.getLimitOffset(queryWrapper);
        try {
            // 只有 totalRow 小于 0 的时候才会去查询总量
            // 这样方便用户做总数缓存，而非每次都要去查询总量
            // 一般的分页场景中，只有第一页的时候有必要去查询总量，第二页以后是不需要的

            if (page.getTotalRow() < 0) {

                QueryWrapper countQueryWrapper;

                if (page.needOptimizeCountQuery()) {
                    countQueryWrapper = MapperUtil.optimizeCountQueryWrapper(queryWrapper);
                } else {
                    countQueryWrapper = MapperUtil.rawCountQueryWrapper(queryWrapper);
                }

                // optimize: 在 count 之前先去掉 limit 参数，避免 count 查询错误
                CPI.setLimitRows(countQueryWrapper, null);
                CPI.setLimitOffset(countQueryWrapper, null);

                page.setTotalRow(mapper.selectCountByQuery(countQueryWrapper));
            }

            if (!page.hasRecords()) {
                if (withRelations) {
                    RelationManager.clearConfigIfNecessary();
                }
                return page;
            }

            queryWrapper.limit(page.offset(), page.getPageSize());

            List<R> records;
            if (asType != null) {
                records = mapper.selectListByQueryAs(queryWrapper, asType);
            } else {
                // noinspection unchecked
                records = (List<R>) mapper.selectListByQuery(queryWrapper);
            }

            if (withRelations) {
                queryRelations(mapper, records);
            }

            queryFields(mapper, records, consumers);
            page.setRecords(records);

            return page;

        } finally {
            // 将之前设置的 limit 清除掉
            // 保险起见把重置代码放到 finally 代码块中
            CPI.setLimitRows(queryWrapper, limitRows);
            CPI.setLimitOffset(queryWrapper, limitOffset);
        }
    }


    public static <R> void queryFields(BaseMapper<?> mapper, List<R> list, Consumer<FieldQueryBuilder<R>>[] consumers) {
        if (CollectionUtil.isEmpty(list) || ArrayUtil.isEmpty(consumers) || consumers[0] == null) {
            return;
        }

        Map<String, FieldQuery> fieldQueryMap = new HashMap<>();
        for (Consumer<FieldQueryBuilder<R>> consumer : consumers) {
            FieldQueryBuilder<R> fieldQueryBuilder = new FieldQueryBuilder<>();
            consumer.accept(fieldQueryBuilder);

            FieldQuery fieldQuery = fieldQueryBuilder.build();

            String className = fieldQuery.getEntityClass().getName();
            String fieldName = fieldQuery.getFieldName();
            String mapKey = className + '#' + fieldName;

            fieldQueryMap.put(mapKey, fieldQuery);
        }

        FieldQueryManager.queryFields(mapper, list, fieldQueryMap);
    }


    public static <E> E queryRelations(BaseMapper<?> mapper, E entity) {
        if (entity != null) {
            queryRelations(mapper, Collections.singletonList(entity));
        } else {
            RelationManager.clearConfigIfNecessary();
        }
        return entity;
    }

    public static <E> List<E> queryRelations(BaseMapper<?> mapper, List<E> entities) {
        RelationManager.queryRelations(mapper, entities);
        return entities;
    }


    public static Class<? extends Collection> getCollectionWrapType(Class<?> type) {
        if (ClassUtil.canInstance(type.getModifiers())) {
            return (Class<? extends Collection>) type;
        }

        if (List.class.isAssignableFrom(type)) {
            return ArrayList.class;
        }

        if (Set.class.isAssignableFrom(type)) {
            return HashSet.class;
        }

        throw new IllegalStateException("Field query can not support type: " + type.getName());
    }


    /**
     * 搬运加改造 {@link DefaultSqlSession#selectOne(String, Object)}
     */
    public static <T> T getSelectOneResult(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int size = list.size();
        if (size == 1) {
            return list.get(0);
        }
        throw new TooManyResultsException(
            "Expected one result (or null) to be returned by selectOne(), but found: " + size);
    }

    public static long getLongNumber(List<Object> objects) {
        Object object = objects == null || objects.isEmpty() ? null : objects.get(0);
        if (object == null) {
            return 0;
        } else if (object instanceof Number) {
            return ((Number) object).longValue();
        } else {
            throw FlexExceptions.wrap("selectCountByQuery error, can not get number value of result: \"" + object + "\"");
        }
    }


    public static Map<String, Object> preparedParams(BaseMapper<?> baseMapper, Page<?> page, QueryWrapper queryWrapper, Map<String, Object> params) {
        Map<String, Object> newParams = new HashMap<>();

        if (params != null) {
            newParams.putAll(params);
        }

        newParams.put("pageOffset", page.offset());
        newParams.put("pageNumber", page.getPageNumber());
        newParams.put("pageSize", page.getPageSize());

        DbType dbType = DialectFactory.getHintDbType();
        newParams.put("dbType", dbType != null ? dbType : FlexGlobalConfig.getDefaultConfig().getDbType());

        if (queryWrapper != null) {
            TableInfo tableInfo = TableInfoFactory.ofMapperClass(baseMapper.getClass());
            tableInfo.appendConditions(null, queryWrapper);
            preparedQueryWrapper(newParams, queryWrapper);
        }

        return newParams;
    }


    private static void preparedQueryWrapper(Map<String, Object> params, QueryWrapper queryWrapper) {
        String sql = DialectFactory.getDialect().buildNoSelectSql(queryWrapper);
        StringBuilder sqlBuilder = new StringBuilder();
        char quote = 0;
        int index = 0;
        for (int i = 0; i < sql.length(); ++i) {
            char ch = sql.charAt(i);
            if (ch == '\'') {
                if (quote == 0) {
                    quote = ch;
                } else if (quote == '\'') {
                    quote = 0;
                }
            } else if (ch == '"') {
                if (quote == 0) {
                    quote = ch;
                } else if (quote == '"') {
                    quote = 0;
                }
            }
            if (quote == 0 && ch == '?') {
                sqlBuilder.append("#{qwParams_").append(index++).append("}");
            } else {
                sqlBuilder.append(ch);
            }
        }
        params.put("qwSql", sqlBuilder.toString());
        Object[] valueArray = CPI.getValueArray(queryWrapper);
        for (int i = 0; i < valueArray.length; i++) {
            params.put("qwParams_" + i, valueArray[i]);
        }
    }

}
