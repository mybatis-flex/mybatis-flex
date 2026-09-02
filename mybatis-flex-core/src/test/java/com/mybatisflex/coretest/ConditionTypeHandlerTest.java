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
package com.mybatisflex.coretest;

import com.mybatisflex.core.mybatis.TypeHandlerObject;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * 条件参数应用 {@code @Column(typeHandler = ...)} 的测试。
 *
 * <p>验证 where 条件的参数会被包装为 {@link TypeHandlerObject}，从而与 insert、update
 * 的行为保持一致，避免字段加密一类的场景下以明文查询密文列时静默查不到数据。
 */
public class ConditionTypeHandlerTest {

    private static final QueryTable TABLE = new QueryTable("tb_secret_user");
    private static final QueryColumn ID = new QueryColumn(TABLE, "id");
    private static final QueryColumn CERT_NO = new QueryColumn(TABLE, "cert_no");
    private static final QueryColumn USER_NAME = new QueryColumn(TABLE, "user_name");

    /**
     * 模拟字段加密的类型处理器，写入时对内容做一次简单的偏移。
     */
    public static class CipherTypeHandler extends BaseTypeHandler<String> {

        static String encrypt(String plainText) {
            return "enc(" + plainText + ")";
        }

        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
            ps.setString(i, encrypt(parameter));
        }

        @Override
        public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
            return rs.getString(columnName);
        }

        @Override
        public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getString(columnIndex);
        }

        @Override
        public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
            return cs.getString(columnIndex);
        }

    }

    @Before
    public void setUp() {
        // 触发实体类解析，使 TableInfoFactory 中存在 tb_secret_user 的表信息
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(SecretUser.class);
        // 单元测试中没有 MyBatis 的 Configuration，@Column(typeHandler = ...) 无法被解析为实例，
        // 此处手动补上，等价于实际使用时由 TypeHandlerRegistry 创建
        tableInfo.getColumnInfo("cert_no").setTypeHandler(new CipherTypeHandler());
    }

    @After
    public void tearDown() {
        QueryColumnBehavior.setApplyConditionTypeHandler(true);
    }

    @Test
    public void applyTypeHandlerOnEqualsCondition() {
        QueryWrapper qw = QueryWrapper.create()
            .from(TABLE)
            .where(CERT_NO.eq("110101199001011234"));

        Object[] values = CPI.getValueArray(qw);
        Assert.assertEquals(1, values.length);
        assertHandledValue("enc(110101199001011234)", values[0]);
    }

    @Test
    public void applyTypeHandlerOnInCondition() {
        QueryWrapper qw = QueryWrapper.create()
            .from(TABLE)
            .where(CERT_NO.in(Arrays.asList("aaa", "bbb")));

        Object[] values = CPI.getValueArray(qw);
        Assert.assertEquals(2, values.length);
        assertHandledValue("enc(aaa)", values[0]);
        assertHandledValue("enc(bbb)", values[1]);
    }

    @Test
    public void applyTypeHandlerOnUpdateWrapperCondition() {
        QueryWrapper qw = QueryWrapper.create()
            .from(TABLE)
            .where(CERT_NO.eq("aaa"))
            .and(USER_NAME.eq("michael"));

        Object[] values = CPI.getValueArray(qw);
        Assert.assertEquals(2, values.length);
        assertHandledValue("enc(aaa)", values[0]);
        // 未配置 typeHandler 的列保持原始参数
        Assert.assertEquals("michael", values[1]);
    }

    @Test
    public void keepRawValueOnLikeCondition() {
        // LIKE 的参数是拼接了 % 的片段，而非该列的完整值，不应套用列上的类型处理器
        QueryWrapper qw = QueryWrapper.create()
            .from(TABLE)
            .where(CERT_NO.like("1101"));

        Object[] values = CPI.getValueArray(qw);
        Assert.assertEquals(1, values.length);
        Assert.assertEquals("%1101%", values[0]);
    }

    @Test
    public void keepRawValueOnFunctionColumn() {
        // 函数列没有明确的实体类字段与之对应
        QueryWrapper qw = QueryWrapper.create()
            .from(TABLE)
            .where(QueryMethods.length(CERT_NO).eq(18));

        Object[] values = CPI.getValueArray(qw);
        Assert.assertEquals(1, values.length);
        Assert.assertEquals(18, values[0]);
    }

    @Test
    public void keepRawValueOnUnknownTable() {
        // 列所属的表没有对应的实体类
        QueryColumn column = new QueryColumn(new QueryTable("tb_not_an_entity"), "cert_no");
        QueryWrapper qw = QueryWrapper.create()
            .from("tb_not_an_entity")
            .where(column.eq("aaa"));

        Object[] values = CPI.getValueArray(qw);
        Assert.assertEquals(1, values.length);
        Assert.assertEquals("aaa", values[0]);
    }

    @Test
    public void keepRawValueOnMismatchedValueType() {
        // 参数类型与实体类属性类型不匹配时，说明传入的并非该属性的值
        QueryWrapper qw = QueryWrapper.create()
            .from(TABLE)
            .where(CERT_NO.eq(123));

        Object[] values = CPI.getValueArray(qw);
        Assert.assertEquals(1, values.length);
        Assert.assertEquals(123, values[0]);
    }

    @Test
    public void keepRawValueOnPrimaryKeyColumn() {
        // 未配置 typeHandler 的主键列保持原始参数
        QueryWrapper qw = QueryWrapper.create()
            .from(TABLE)
            .where(ID.eq(1L));

        Object[] values = CPI.getValueArray(qw);
        Assert.assertEquals(1, values.length);
        Assert.assertEquals(1L, values[0]);
    }

    @Test
    public void disableApplyConditionTypeHandler() {
        QueryColumnBehavior.setApplyConditionTypeHandler(false);

        QueryWrapper qw = QueryWrapper.create()
            .from(TABLE)
            .where(CERT_NO.eq("aaa"));

        Object[] values = CPI.getValueArray(qw);
        Assert.assertEquals(1, values.length);
        Assert.assertEquals("aaa", values[0]);
    }

    @Test
    public void toSqlWithTypeHandledValue() {
        QueryWrapper qw = QueryWrapper.create()
            .from(TABLE)
            .where(CERT_NO.eq("aaa"));

        Assert.assertTrue(qw.toSQL(), qw.toSQL().contains("enc(aaa)"));
    }

    private void assertHandledValue(String expected, Object value) {
        Assert.assertTrue("参数应被包装为 TypeHandlerObject，实际为：" + value,
            value instanceof TypeHandlerObject);
        Assert.assertEquals(expected, resolveParameter((TypeHandlerObject) value));
    }

    /**
     * 借助 {@link TypeHandlerObject#setParameter} 取出类型处理器实际写入的值。
     */
    private String resolveParameter(TypeHandlerObject value) {
        String[] holder = new String[1];
        PreparedStatement ps = (PreparedStatement) java.lang.reflect.Proxy.newProxyInstance(
            ConditionTypeHandlerTest.class.getClassLoader(),
            new Class[]{PreparedStatement.class}, (proxy, method, args) -> {
                if (args != null && args.length >= 2) {
                    holder[0] = String.valueOf(args[1]);
                }
                return null;
            });
        try {
            value.setParameter(ps, 0);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return holder[0];
    }

}
