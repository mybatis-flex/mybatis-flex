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
package com.mybatisflex.core.query;

import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

import java.util.Collection;
import java.util.function.Predicate;

public class QueryConditionBuilder<Wrapper extends QueryWrapper> {

    private final Wrapper queryWrapper;
    private final QueryColumn queryColumn;
    private final SqlConnector connector;


    public QueryConditionBuilder(Wrapper queryWrapper, QueryColumn queryColumn, SqlConnector connector) {
        this.queryWrapper = queryWrapper;
        this.queryColumn = queryColumn;
        this.connector = connector;
    }

    /**
     * equals
     *
     * @param value
     */
    public Wrapper eq(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.eq(value), connector);
        }
        return queryWrapper;
    }


    public <T> Wrapper eq(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.eq(value, when), connector);
        }
        return queryWrapper;
    }


    public <T> Wrapper eq(LambdaGetter<T> value) {
        return eq(LambdaUtil.getQueryColumn(value));
    }


    public <T> Wrapper eq(LambdaGetter<T> value, Predicate<T> when) {
        return eq(LambdaUtil.getQueryColumn(value), when);
    }


    /**
     * not equals !=
     *
     * @param value
     */
    public Wrapper ne(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.ne(value), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper ne(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.ne(value, when), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper ne(LambdaGetter<T> value) {
        return ne(LambdaUtil.getQueryColumn(value));
    }


    public <T> Wrapper ne(LambdaGetter<T> value, Predicate<T> when) {
        return ne(LambdaUtil.getQueryColumn(value), when);
    }


    /**
     * like %%
     *
     * @param value
     */
    public Wrapper like(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.like(value), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper like(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.like(value, when), connector);
        }
        return queryWrapper;
    }


    public Wrapper likeLeft(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.likeLeft(value), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper likeLeft(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.likeLeft(value, when), connector);
        }
        return queryWrapper;
    }


    public Wrapper likeRight(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.likeRight(value), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper likeRight(Object value, Predicate<T> when) {
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
    public Wrapper gt(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.gt(value), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper gt(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.gt(value, when), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper gt(LambdaGetter<T> value) {
        return gt(LambdaUtil.getQueryColumn(value));
    }


    public <T> Wrapper gt(LambdaGetter<T> value, Predicate<T> when) {
        return gt(LambdaUtil.getQueryColumn(value), when);
    }


    /**
     * 大于等于 greater or equal
     *
     * @param value
     */
    public Wrapper ge(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.ge(value), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper ge(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.ge(value, when), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper ge(LambdaGetter<T> value) {
        return ge(LambdaUtil.getQueryColumn(value));
    }


    public <T> Wrapper ge(LambdaGetter<T> value, Predicate<T> when) {
        return ge(LambdaUtil.getQueryColumn(value), when);
    }

    /**
     * 小于 less than
     *
     * @param value
     */
    public Wrapper lt(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.lt(value), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper lt(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.lt(value, when), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper lt(LambdaGetter<T> value) {
        return lt(LambdaUtil.getQueryColumn(value));
    }


    public <T> Wrapper lt(LambdaGetter<T> value, Predicate<T> when) {
        return lt(LambdaUtil.getQueryColumn(value), when);
    }

    /**
     * 小于等于 less or equal
     *
     * @param value
     */
    public Wrapper le(Object value) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.le(value), connector);
        }
        return queryWrapper;
    }


    public <T> Wrapper le(Object value, Predicate<T> when) {
        if (value != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.le(value, when), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper le(LambdaGetter<T> value) {
        return le(LambdaUtil.getQueryColumn(value));
    }


    public <T> Wrapper le(LambdaGetter<T> value, Predicate<T> when) {
        return le(LambdaUtil.getQueryColumn(value), when);
    }


    /**
     * IS NULL
     *
     * @return
     */
    public Wrapper isNull() {
        queryWrapper.addWhereQueryCondition(queryColumn.isNull(), connector);
        return queryWrapper;
    }

    public <T> Wrapper isNull(Predicate<T> when) {
        queryWrapper.addWhereQueryCondition(queryColumn.isNull(when), connector);
        return queryWrapper;
    }


    /**
     * IS NOT NULL
     *
     * @return
     */
    public Wrapper isNotNull() {
        queryWrapper.addWhereQueryCondition(queryColumn.isNotNull(), connector);
        return queryWrapper;
    }

    public <T> Wrapper isNotNull(Predicate<T> when) {
        queryWrapper.addWhereQueryCondition(queryColumn.isNotNull(when), connector);
        return queryWrapper;
    }


    /**
     * in arrays
     *
     * @param arrays
     * @return
     */
    public Wrapper in(Object... arrays) {
        if (arrays != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.in(arrays), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper in(Object[] arrays, Predicate<T> when) {
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
    public Wrapper in(QueryWrapper queryWrapper) {
        if (queryWrapper != null) {
            this.queryWrapper.addWhereQueryCondition(queryColumn.in(queryWrapper), connector);
        }
        return this.queryWrapper;
    }

    public <T> Wrapper in(QueryWrapper queryWrapper, Predicate<T> when) {
        if (queryWrapper != null) {
            this.queryWrapper.addWhereQueryCondition(queryColumn.in(queryWrapper, when), connector);
        }
        return this.queryWrapper;
    }


    /**
     * in Collection
     *
     * @param collection
     * @return
     */
    public Wrapper in(Collection<?> collection) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.in(collection), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper in(Collection<?> collection, Predicate<T> when) {
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
    public Wrapper notIn(Object... arrays) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.notIn(arrays), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper notIn(Object[] arrays, Predicate<T> when) {
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
    public Wrapper notIn(Collection<?> collection) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.notIn(collection), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper notIn(Collection<?> collection, Predicate<T> when) {
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
    public Wrapper notIn(QueryWrapper queryWrapper) {
        if (queryWrapper != null) {
            this.queryWrapper.addWhereQueryCondition(queryColumn.notIn(queryWrapper), connector);
        }
        return this.queryWrapper;
    }

    public <T> Wrapper notIn(QueryWrapper queryWrapper, Predicate<T> when) {
        if (queryWrapper != null) {
            this.queryWrapper.addWhereQueryCondition(queryColumn.notIn(queryWrapper, when), connector);
        }
        return this.queryWrapper;
    }


    /**
     * between
     *
     * @param start
     * @param end
     */
    public Wrapper between(Object start, Object end) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.between(start, end), connector);
        }
        return queryWrapper;
    }


    public <T> Wrapper between(Object start, Object end, Predicate<T> when) {
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
    public Wrapper notBetween(Object start, Object end) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.notBetween(start, end), connector);
        }
        return queryWrapper;
    }

    public <T> Wrapper notBetween(Object start, Object end, Predicate<T> when) {
        if (queryWrapper != null) {
            queryWrapper.addWhereQueryCondition(queryColumn.notBetween(start, end, when), connector);
        }
        return queryWrapper;
    }


}
