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
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.field.FieldQuery;
import com.mybatisflex.core.field.FieldQueryBuilder;
import com.mybatisflex.core.query.*;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.util.*;
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

    private static boolean hasDistinct(List<QueryColumn> selectColumns) {
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
            if (joinQueryTable != null && StringUtil.isNotBlank(joinQueryTable.getName())) {
                joinTables.add(joinQueryTable.getName());
            }
        });

        // 获取 where 语句中的条件
        QueryCondition where = CPI.getWhereQueryCondition(queryWrapper);

        // 最后判断一下 where 中是否用到了 join 的表
        return !CPI.containsTable(where, CollectionUtil.toArrayString(joinTables));
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <R> void queryFields(BaseMapper<?> mapper, List<R> list, Consumer<FieldQueryBuilder<R>>[] consumers) {
        if (CollectionUtil.isEmpty(list) || ArrayUtil.isEmpty(consumers) || consumers[0] == null) {
            return;
        }
        list.forEach(entity -> {
            for (Consumer<FieldQueryBuilder<R>> consumer : consumers) {
                FieldQueryBuilder<R> fieldQueryBuilder = new FieldQueryBuilder<>(entity);
                consumer.accept(fieldQueryBuilder);
                FieldQuery fieldQuery = fieldQueryBuilder.build();
                QueryWrapper childQuery = fieldQuery.getQueryWrapper();

                FieldWrapper fieldWrapper = FieldWrapper.of(entity.getClass(), fieldQuery.getField());

                Class<?> fieldType = fieldWrapper.getFieldType();
                Class<?> mappingType = fieldWrapper.getMappingType();

                Object value;
                if (Collection.class.isAssignableFrom(fieldType)) {
                    value = mapper.selectListByQueryAs(childQuery, mappingType);
                    if (!fieldType.isAssignableFrom(value.getClass())) {
                        fieldType = getWrapType(fieldType);
                        Collection newValue = (Collection) ClassUtil.newInstance(fieldType);
                        newValue.addAll((Collection) value);
                        value = newValue;
                    }
                } else if (fieldType.isArray()) {
                    value = mapper.selectListByQueryAs(childQuery, mappingType);
                    value = ((List<?>) value).toArray();
                } else {
                    value = mapper.selectOneByQueryAs(childQuery, mappingType);
                }
                fieldWrapper.set(value, entity);
            }
        });
    }


    private static Class<?> getWrapType(Class<?> type) {
        if (ClassUtil.canInstance(type.getModifiers())) {
            return type;
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
}
