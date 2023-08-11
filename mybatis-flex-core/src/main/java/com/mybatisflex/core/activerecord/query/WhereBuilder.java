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
package com.mybatisflex.core.activerecord.query;

import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.SqlConnector;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Lambda 条件构建器。
 *
 * @author 王帅
 * @since 2023-07-24
 */
public class WhereBuilder<R extends QueryModel<R>> {

    private final R queryModel;
    private final QueryColumn queryColumn;
    private final SqlConnector connector;

    public WhereBuilder(R queryModel, QueryColumn queryColumn, SqlConnector connector) {
        this.queryModel = queryModel;
        this.queryColumn = queryColumn;
        this.connector = connector;
    }

    public R eq(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.eq(value), connector);
        }
        return queryModel;
    }

    public <T> R eq(T value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.eq(value, when), connector);
        }
        return queryModel;
    }

    public <T> R eq(LambdaGetter<T> value) {
        return eq(LambdaUtil.getQueryColumn(value));
    }

    /**
     * @deprecated {@link Predicate} 泛型参数无效
     */
    @Deprecated
    public <T> R eq(LambdaGetter<T> value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.eq(LambdaUtil.getQueryColumn(value)).when(when), connector);
        }
        return queryModel;
    }

    public R ne(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.ne(value), connector);
        }
        return queryModel;
    }

    public <T> R ne(T value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.ne(value, when), connector);
        }
        return queryModel;
    }

    public <T> R ne(LambdaGetter<T> value) {
        return ne(LambdaUtil.getQueryColumn(value));
    }

    /**
     * @deprecated {@link Predicate} 泛型参数无效
     */
    @Deprecated
    public <T> R ne(LambdaGetter<T> value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.ne(LambdaUtil.getQueryColumn(value)).when(when), connector);
        }
        return queryModel;
    }

    public R like(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.like(value), connector);
        }
        return queryModel;
    }

    public <T> R like(T value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.like(value, when), connector);
        }
        return queryModel;
    }

    public R likeLeft(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.likeLeft(value), connector);
        }
        return queryModel;
    }

    public <T> R likeLeft(T value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.likeLeft(value, when), connector);
        }
        return queryModel;
    }

    public R likeRight(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.likeRight(value), connector);
        }
        return queryModel;
    }

    public <T> R likeRight(T value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.likeRight(value, when), connector);
        }
        return queryModel;
    }

    public R gt(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.gt(value), connector);
        }
        return queryModel;
    }

    public <T> R gt(T value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.gt(value, when), connector);
        }
        return queryModel;
    }

    public <T> R gt(LambdaGetter<T> value) {
        return gt(LambdaUtil.getQueryColumn(value));
    }

    /**
     * @deprecated {@link Predicate} 泛型参数无效
     */
    @Deprecated
    public <T> R gt(LambdaGetter<T> value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.gt(LambdaUtil.getQueryColumn(value)).when(when), connector);
        }
        return queryModel;
    }

    public R ge(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.ge(value), connector);
        }
        return queryModel;
    }

    public <T> R ge(T value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.ge(value, when), connector);
        }
        return queryModel;
    }

    public <T> R ge(LambdaGetter<T> value) {
        return ge(LambdaUtil.getQueryColumn(value));
    }

    /**
     * @deprecated {@link Predicate} 泛型参数无效
     */
    @Deprecated
    public <T> R ge(LambdaGetter<T> value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.ge(LambdaUtil.getQueryColumn(value)).when(when), connector);
        }
        return queryModel;
    }

    public R lt(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.lt(value), connector);
        }
        return queryModel;
    }

    public <T> R lt(T value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.lt(value, when), connector);
        }
        return queryModel;
    }

    public <T> R lt(LambdaGetter<T> value) {
        return lt(LambdaUtil.getQueryColumn(value));
    }

    /**
     * @deprecated {@link Predicate} 泛型参数无效
     */
    @Deprecated
    public <T> R lt(LambdaGetter<T> value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.lt(LambdaUtil.getQueryColumn(value)).when(when), connector);
        }
        return queryModel;
    }

    public R le(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.le(value), connector);
        }
        return queryModel;
    }

    public <T> R le(T value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.le(value, when), connector);
        }
        return queryModel;
    }

    public <T> R le(LambdaGetter<T> value) {
        return le(LambdaUtil.getQueryColumn(value));
    }

    /**
     * @deprecated {@link Predicate} 泛型参数无效
     */
    @Deprecated
    public <T> R le(LambdaGetter<T> value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.le(LambdaUtil.getQueryColumn(value)).when(when), connector);
        }
        return queryModel;
    }

    public R isNull() {
        CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.isNull(), connector);
        return queryModel;
    }

    /**
     * @deprecated 无法推断泛型
     */
    @Deprecated
    public <T> R isNull(Predicate<T> when) {
        CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.isNull(when), connector);
        return queryModel;
    }

    public R isNotNull() {
        CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.isNotNull(), connector);
        return queryModel;
    }

    /**
     * @deprecated 无法推断泛型
     */
    @Deprecated
    public <T> R isNotNull(Predicate<T> when) {
        CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.isNotNull(when), connector);
        return queryModel;
    }

    public R in(Object... arrays) {
        if (arrays != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.in(arrays), connector);
        }
        return queryModel;
    }

    public <T> R in(T[] arrays, Predicate<T[]> when) {
        //忽略 QueryWrapper.in("name", null) 的情况
        if (arrays != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.in(arrays, when), connector);
        }
        return queryModel;
    }

    public R in(R queryModel) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.in(queryModel), connector);
        }
        return this.queryModel;
    }

    /**
     * @deprecated 无法推断泛型
     */
    @Deprecated
    public <T> R in(R queryModel, Predicate<T> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.in(queryModel, when), connector);
        }
        return this.queryModel;
    }

    public R in(Collection<?> collection) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.in(collection), connector);
        }
        return queryModel;
    }

    public <T extends Collection<?>> R in(T collection, Predicate<T> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.in(collection, when), connector);
        }
        return queryModel;
    }

    public R notIn(Object... arrays) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.notIn(arrays), connector);
        }
        return queryModel;
    }

    public <T> R notIn(T[] arrays, Predicate<T[]> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.notIn(arrays, when), connector);
        }
        return queryModel;
    }

    public R notIn(Collection<?> collection) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.notIn(collection), connector);
        }
        return queryModel;
    }

    public <T extends Collection<?>> R notIn(T collection, Predicate<T> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.notIn(collection, when), connector);
        }
        return queryModel;
    }

    public R notIn(R queryModel) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.notIn(queryModel), connector);
        }
        return this.queryModel;
    }

    /**
     * @deprecated 无法推断泛型
     */
    @Deprecated
    public <T> R notIn(R queryModel, Predicate<T> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.notIn(queryModel, when), connector);
        }
        return this.queryModel;
    }

    public R between(Object start, Object end) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.between(start, end), connector);
        }
        return queryModel;
    }

    public <S, E> R between(S start, E end, BiPredicate<S, E> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.between(start, end, when), connector);
        }
        return queryModel;
    }

    public R notBetween(Object start, Object end) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.notBetween(start, end), connector);
        }
        return queryModel;
    }

    public <S, E> R notBetween(S start, E end, BiPredicate<S, E> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.queryWrapper(), queryColumn.notBetween(start, end, when), connector);
        }
        return queryModel;
    }

}
