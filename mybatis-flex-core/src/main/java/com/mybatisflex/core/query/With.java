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

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

import static com.mybatisflex.core.constant.SqlConsts.*;

public class With implements CloneSupport<With> {

    private boolean recursive;
    private List<WithItem> withItems;

    public With() {
    }

    public With(boolean recursive) {
        this.recursive = recursive;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public List<WithItem> getWithItems() {
        return withItems;
    }

    public void setWithItems(List<WithItem> withItems) {
        this.withItems = withItems;
    }

    public void addWithItem(WithItem withItem) {
        if (withItems == null) {
            withItems = new ArrayList<>();
        }
        withItems.add(withItem);
    }

    public String toSql(IDialect dialect) {
        StringBuilder sql = new StringBuilder(WITH);
        if (recursive) {
            sql.append(RECURSIVE);
        }
        for (int i = 0; i < withItems.size(); i++) {
            sql.append(withItems.get(i).toSql(dialect));
            if (i != withItems.size() - 1) {
                sql.append(DELIMITER);
            }
        }
        return sql.append(BLANK).toString();
    }

    public Object[] getParamValues() {
        Object[] paramValues = FlexConsts.EMPTY_ARRAY;
        for (WithItem withItem : withItems) {
            paramValues = ArrayUtil.concat(paramValues, withItem.getParamValues());
        }
        return paramValues;
    }

    @Override
    public With clone() {
        try {
            With clone = (With) super.clone();
            // deep clone ...
            clone.withItems = CollectionUtil.cloneArrayList(this.withItems);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw FlexExceptions.wrap(e);
        }
    }

}
