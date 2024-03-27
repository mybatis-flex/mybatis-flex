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

package com.mybatisflex.test.issue113.def;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryTable;

public class TClassTableDef extends QueryTable {
    public static final TClassTableDef TB_CLASS = new TClassTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    public final QueryColumn CLASS_NAME = new QueryColumn(this, "class_name");

    public final QueryColumn SEX = new QueryColumn(this, "sex");

    public TClassTableDef() {
        super("", "tb_class");
    }
}
