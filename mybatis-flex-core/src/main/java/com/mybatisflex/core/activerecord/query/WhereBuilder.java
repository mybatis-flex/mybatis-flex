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
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.eq(value), connector);
        }
        return queryModel;
    }

    public <T> R eq(Object value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.eq(value, when), connector);
        }
        return queryModel;
    }

    public <T> R eq(LambdaGetter<T> value) {
        return eq(LambdaUtil.getQueryColumn(value));
    }

    public <T> R eq(LambdaGetter<T> value, Predicate<T> when) {
        return eq(LambdaUtil.getQueryColumn(value), when);
    }

    public R ne(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.ne(value), connector);
        }
        return queryModel;
    }

    public <T> R ne(Object value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.ne(value, when), connector);
        }
        return queryModel;
    }

    public <T> R ne(LambdaGetter<T> value) {
        return ne(LambdaUtil.getQueryColumn(value));
    }

    public <T> R ne(LambdaGetter<T> value, Predicate<T> when) {
        return ne(LambdaUtil.getQueryColumn(value), when);
    }

    public R like(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.like(value), connector);
        }
        return queryModel;
    }

    public <T> R like(Object value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.like(value, when), connector);
        }
        return queryModel;
    }

    public R likeLeft(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.likeLeft(value), connector);
        }
        return queryModel;
    }

    public <T> R likeLeft(Object value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.likeLeft(value, when), connector);
        }
        return queryModel;
    }

    public R likeRight(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.likeRight(value), connector);
        }
        return queryModel;
    }

    public <T> R likeRight(Object value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.likeRight(value, when), connector);
        }
        return queryModel;
    }

    public R gt(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.gt(value), connector);
        }
        return queryModel;
    }

    public <T> R gt(Object value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.gt(value, when), connector);
        }
        return queryModel;
    }

    public <T> R gt(LambdaGetter<T> value) {
        return gt(LambdaUtil.getQueryColumn(value));
    }

    public <T> R gt(LambdaGetter<T> value, Predicate<T> when) {
        return gt(LambdaUtil.getQueryColumn(value), when);
    }

    public R ge(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.ge(value), connector);
        }
        return queryModel;
    }

    public <T> R ge(Object value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.ge(value, when), connector);
        }
        return queryModel;
    }

    public <T> R ge(LambdaGetter<T> value) {
        return ge(LambdaUtil.getQueryColumn(value));
    }

    public <T> R ge(LambdaGetter<T> value, Predicate<T> when) {
        return ge(LambdaUtil.getQueryColumn(value), when);
    }

    public R lt(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.lt(value), connector);
        }
        return queryModel;
    }

    public <T> R lt(Object value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.lt(value, when), connector);
        }
        return queryModel;
    }

    public <T> R lt(LambdaGetter<T> value) {
        return lt(LambdaUtil.getQueryColumn(value));
    }

    public <T> R lt(LambdaGetter<T> value, Predicate<T> when) {
        return lt(LambdaUtil.getQueryColumn(value), when);
    }

    public R le(Object value) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.le(value), connector);
        }
        return queryModel;
    }

    public <T> R le(Object value, Predicate<T> when) {
        if (value != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.le(value, when), connector);
        }
        return queryModel;
    }

    public <T> R le(LambdaGetter<T> value) {
        return le(LambdaUtil.getQueryColumn(value));
    }

    public <T> R le(LambdaGetter<T> value, Predicate<T> when) {
        return le(LambdaUtil.getQueryColumn(value), when);
    }

    public R isNull() {
        CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.isNull(), connector);
        return queryModel;
    }

    public <T> R isNull(Predicate<T> when) {
        CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.isNull(when), connector);
        return queryModel;
    }

    public R isNotNull() {
        CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.isNotNull(), connector);
        return queryModel;
    }

    public <T> R isNotNull(Predicate<T> when) {
        CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.isNotNull(when), connector);
        return queryModel;
    }

    public R in(Object... arrays) {
        if (arrays != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.in(arrays), connector);
        }
        return queryModel;
    }

    public <T> R in(Object[] arrays, Predicate<T> when) {
        //忽略 QueryWrapper.in("name", null) 的情况
        if (arrays != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.in(arrays, when), connector);
        }
        return queryModel;
    }

    public R in(R queryModel) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.in(queryModel), connector);
        }
        return this.queryModel;
    }

    public <T> R in(R queryModel, Predicate<T> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.in(queryModel, when), connector);
        }
        return this.queryModel;
    }

    public R in(Collection<?> collection) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.in(collection), connector);
        }
        return queryModel;
    }

    public <T> R in(Collection<?> collection, Predicate<T> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.in(collection, when), connector);
        }
        return queryModel;
    }

    public R notIn(Object... arrays) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.notIn(arrays), connector);
        }
        return queryModel;
    }

    public <T> R notIn(Object[] arrays, Predicate<T> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.notIn(arrays, when), connector);
        }
        return queryModel;
    }

    public R notIn(Collection<?> collection) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.notIn(collection), connector);
        }
        return queryModel;
    }

    public <T> R notIn(Collection<?> collection, Predicate<T> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.notIn(collection, when), connector);
        }
        return queryModel;
    }

    public R notIn(R queryModel) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.notIn(queryModel), connector);
        }
        return this.queryModel;
    }

    public <T> R notIn(R queryModel, Predicate<T> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.notIn(queryModel, when), connector);
        }
        return this.queryModel;
    }

    public R between(Object start, Object end) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.between(start, end), connector);
        }
        return queryModel;
    }

    public <T> R between(Object start, Object end, Predicate<T> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.between(start, end, when), connector);
        }
        return queryModel;
    }

    public R notBetween(Object start, Object end) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.notBetween(start, end), connector);
        }
        return queryModel;
    }

    public <T> R notBetween(Object start, Object end, Predicate<T> when) {
        if (queryModel != null) {
            CPI.addWhereQueryCondition(queryModel.getQueryWrapper(), queryColumn.notBetween(start, end, when), connector);
        }
        return queryModel;
    }

}
