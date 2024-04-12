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

import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/14
 */
public class Joiner<M extends QueryWrapper> {

    private final M queryWrapper;
    private final Join join;

    public Joiner(M queryWrapper, Join join) {
        this.queryWrapper = queryWrapper;
        this.join = join;
    }

    /**
     * <p>推荐写法：
     * <pre>
     * {@code leftJoin(ACCOUNT.as("a")).on(...);}
     * </pre>
     * <p>或者：
     * <pre>{@code
     * AccountTableDef a = ACCOUNT.as("a");
     * leftJoin(a).on(...);
     * }</pre>
     */
    public Joiner<M> as(String alias) {
        join.queryTable = join.getQueryTable().as(alias);
        ListIterator<QueryTable> itr = queryWrapper.joinTables.listIterator();
        while (itr.hasNext()) {
            if (itr.next().isSameTable(join.queryTable)) {
                itr.set(join.queryTable);
                break;
            }
        }
        return this;
    }

    public M on(String on) {
        join.on(new RawQueryCondition(on));
        return queryWrapper;
    }

    public M on(QueryCondition on) {
        join.on(on);
        return queryWrapper;
    }

    public M on(Consumer<QueryWrapper> consumer) {
        QueryWrapper newWrapper = new QueryWrapper();
        consumer.accept(newWrapper);
        join.on(newWrapper.whereQueryCondition);
        return queryWrapper;
    }

    public <T, K> M on(LambdaGetter<T> column1, LambdaGetter<K> column2) {
        QueryCondition queryCondition = LambdaUtil.getQueryColumn(column1).eq(LambdaUtil.getQueryColumn(column2));
        join.on(queryCondition);
        return queryWrapper;
    }


}

