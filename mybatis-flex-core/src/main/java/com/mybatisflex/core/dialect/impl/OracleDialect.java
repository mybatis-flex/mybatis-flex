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
package com.mybatisflex.core.dialect.impl;

import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcesser;

public class OracleDialect extends CommonsDialectImpl {

    private boolean toUpperCase = true;

    public OracleDialect() {
    }

    public OracleDialect(LimitOffsetProcesser limitOffsetProcesser) {
        super(limitOffsetProcesser);
    }

    public OracleDialect(KeywordWrap keywordWrap, LimitOffsetProcesser limitOffsetProcesser) {
        super(keywordWrap, limitOffsetProcesser);
    }

    public boolean isToUpperCase() {
        return toUpperCase;
    }

    public void setToUpperCase(boolean toUpperCase) {
        this.toUpperCase = toUpperCase;
    }

    @Override
    public String wrap(String keyword) {
        return super.wrap(toUpperCase ? keyword.toUpperCase():keyword);
    }
}
