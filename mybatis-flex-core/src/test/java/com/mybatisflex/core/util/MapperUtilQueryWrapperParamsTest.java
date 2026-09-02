/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.coretest.ConditionTypeHandlerTest;
import com.mybatisflex.coretest.SecretUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code xmlPaginate} 使用 {@code #{}} 表达式设置条件参数，此处验证列上配置的
 * 类型处理器会被写入表达式，且参数还原为未处理的原始值。
 */
public class MapperUtilQueryWrapperParamsTest {

    private static final QueryTable TABLE = new QueryTable("tb_secret_user");
    private static final QueryColumn CERT_NO = new QueryColumn(TABLE, "cert_no");
    private static final QueryColumn USER_NAME = new QueryColumn(TABLE, "user_name");

    @Before
    public void setUp() {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(SecretUser.class);
        tableInfo.getColumnInfo("cert_no").setTypeHandler(new ConditionTypeHandlerTest.CipherTypeHandler());
    }

    @Test
    public void appendTypeHandlerToPlaceholder() {
        QueryWrapper qw = QueryWrapper.create()
            .from(TABLE)
            .where(USER_NAME.eq("michael"))
            .and(CERT_NO.eq("110101199001011234"));

        Map<String, Object> params = new HashMap<>();
        MapperUtil.preparedQueryWrapper(params, qw);

        String sql = (String) params.get("qwSql");
        // 未配置类型处理器的列，表达式保持原样
        Assert.assertTrue(sql, sql.contains("#{qwParams_0}"));
        Assert.assertTrue(sql, sql.contains("#{qwParams_1, typeHandler="
            + ConditionTypeHandlerTest.CipherTypeHandler.class.getName() + "}"));
        // 参数还原为未处理的原始值，交由 typeHandler 处理
        Assert.assertEquals("michael", params.get("qwParams_0"));
        Assert.assertEquals("110101199001011234", params.get("qwParams_1"));
    }

}
