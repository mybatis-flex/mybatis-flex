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
package com.mybatisflex.core.query;

import java.util.Collection;
import java.util.function.Predicate;

public class LambdaConditionBuilder {

    private QueryWrapper queryWrapper;
    private QueryColumn queryColumn;
    private SqlConnector connector;

    public LambdaConditionBuilder(QueryWrapper queryWrapper, QueryColumn queryColumn, SqlConnector connector) {
        this.queryWrapper = queryWrapper;
        this.queryColumn = queryColumn;
        this.connector = connector;
    }

    /**
     * equals
     *
     * @param value
     */
    public QueryWrapper eq(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.eq(value), connector);
        }
        return queryWrapper;
    }


    public <T> QueryWrapper eq(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.eq(value, when), connector);
        }
        return queryWrapper;
    }


    /**
     * not equals !=
     *
     * @param value
     */
    public QueryWrapper ne(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.ne(value), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper ne(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.ne(value, when), connector);
        }
        return queryWrapper;
    }


    /**
     * like %%
     *
     * @param value
     */
    public QueryWrapper like(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.like(value), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper like(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.like(value, when), connector);
        }
        return queryWrapper;
    }


    public QueryWrapper likeLeft(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.likeLeft(value), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper likeLeft(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.likeLeft(value, when), connector);
        }
        return queryWrapper;
    }


    public QueryWrapper likeRight(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.likeRight(value), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper likeRight(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.likeRight(value, when), connector);
        }
        return queryWrapper;
    }

    /**
     * 大于 greater than
     *
     * @param value
     */
    public QueryWrapper gt(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.gt(value), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper gt(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.gt(value, when), connector);
        }
        return queryWrapper;
    }

    /**
     * 大于等于 greater or equal
     *
     * @param value
     */
    public QueryWrapper ge(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.ge(value), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper ge(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.ge(value, when), connector);
        }
        return queryWrapper;
    }

    /**
     * 小于 less than
     *
     * @param value
     */
    public QueryWrapper lt(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.lt(value), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper lt(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.lt(value, when), connector);
        }
        return queryWrapper;
    }

    /**
     * 小于等于 less or equal
     *
     * @param value
     */
    public QueryWrapper le(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.le(value), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper le(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.le(value, when), connector);
        }
        return queryWrapper;
    }


    /**
     * IS NULL
     *
     * @return
     */
    public QueryWrapper isNull() {
        queryWrapper.addWhereQueryCondition(queryColumn.isNull(), connector);
        return queryWrapper;
    }

    public <T> QueryWrapper isNull(Predicate<T> when) {
        queryWrapper.addWhereQueryCondition(queryColumn.isNull(when), connector);
        return queryWrapper;
    }


    /**
     * IS NOT NULL
     *
     * @return
     */
    public QueryWrapper isNotNull() {
        queryWrapper.addWhereQueryCondition(queryColumn.isNotNull(), connector);
        return queryWrapper;
    }

    public <T> QueryWrapper isNotNull(Predicate<T> when) {
        queryWrapper.addWhereQueryCondition(queryColumn.isNotNull(when), connector);
        return queryWrapper;
    }


    /**
     * in arrays
     *
     * @param arrays
     * @return
     */
    public QueryWrapper in(Object... arrays) {
        if (arrays != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.in(arrays), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper in(Object[] arrays, Predicate<T> when) {
        //忽略 QueryWrapper.in("name", null) 的情况
        if (arrays != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.in(arrays, when), connector);
        }
        return queryWrapper;
    }

    /**
     * in child select
     *
     * @param queryWrapper
     * @return
     */
    public QueryWrapper in(QueryWrapper queryWrapper) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.in(queryWrapper), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper in(QueryWrapper queryWrapper, Predicate<T> when) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.in(queryWrapper, when), connector);
        }
        return queryWrapper;
    }


    /**
     * in Collection
     *
     * @param collection
     * @return
     */
    public QueryWrapper in(Collection<?> collection) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.in(collection), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper in(Collection<?> collection, Predicate<T> when) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.in(collection, when), connector);
        }
        return queryWrapper;
    }

    /**
     * not int arrays
     *
     * @param arrays
     * @return
     */
    public QueryWrapper notIn(Object... arrays) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.notIn(arrays), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper notIn(Object[] arrays, Predicate<T> when) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.notIn(arrays, when), connector);
        }
        return queryWrapper;
    }


    /**
     * not in Collection
     *
     * @param collection
     * @return
     */
    public QueryWrapper notIn(Collection<?> collection) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.notIn(collection), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper notIn(Collection<?> collection, Predicate<T> when) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.notIn(collection, when), connector);
        }
        return queryWrapper;
    }

    /**
     * not in child select
     *
     * @param queryWrapper
     */
    public QueryWrapper notIn(QueryWrapper queryWrapper) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.notIn(queryWrapper), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper notIn(QueryWrapper queryWrapper, Predicate<T> when) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.notIn(queryWrapper, when), connector);
        }
        return queryWrapper;
    }


    /**
     * between
     *
     * @param start
     * @param end
     */
    public QueryWrapper between(Object start, Object end) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.between(start, end), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper between(Object start, Object end, Predicate<T> when) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.between(start, end, when), connector);
        }
        return queryWrapper;
    }


    /**
     * not between
     *
     * @param start
     * @param end
     */
    public QueryWrapper notBetween(Object start, Object end) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.notBetween(start, end), connector);
        }
        return queryWrapper;
    }

    public <T> QueryWrapper notBetween(Object start, Object end, Predicate<T> when) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.notBetween(start, end, when), connector);
        }
        return queryWrapper;
    }


}
