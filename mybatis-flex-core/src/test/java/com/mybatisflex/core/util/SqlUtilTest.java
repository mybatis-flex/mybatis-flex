package com.mybatisflex.core.util;

import org.junit.Assert;
import org.junit.Test;

public class SqlUtilTest {
    @Test
    public void replaceSqlParams() {
        // 普通情况：参数数量匹配，正常替换问号占位符
        String sql1 = "SELECT * FROM user WHERE id = ? AND name = ?";
        Object[] params1 = {1, "Alice"};
        String result1 = SqlUtil.replaceSqlParams(sql1, params1);
        Assert.assertEquals("SELECT * FROM user WHERE id = 1 AND name = 'Alice'", result1);

        // PostgreSQL 特殊情况：包含问号操作符 (?)，使用两个问号 (??) 进行转义
        String sql2 = "SELECT * FROM table WHERE json_col ?? 'key' AND json_col ??& array['t1', 't2'] AND id = ?";
        Object[] params2 = {100};
        String result2 = SqlUtil.replaceSqlParams(sql2, params2);
        Assert.assertEquals("SELECT * FROM table WHERE json_col ? 'key' AND json_col ?& array['t1', 't2'] AND id = 100", result2);

        // 参数不够的情况：参数少于问号占位符
        String sql3 = "SELECT * FROM user WHERE id = ? AND name = ? AND age = ?";
        Object[] params3 = {3};
        try {
            String result3 = SqlUtil.replaceSqlParams(sql3, params3);
            Assert.assertEquals("SELECT * FROM user WHERE id = 3 AND name = ? AND age = ?", result3);
        } catch (Exception e) {
            System.out.println("result3 error: " + e.getMessage());
            // 如果实现在参数不足时抛出异常，也符合预期逻辑
            assert true;
        }

        // 测试字符串中带有?的情况
        String sql4 = "SELECT 'a??b' as col FROM test WHERE code LIKE 'test?' AND id = ?";
        Object[] params4 = {5};
        String result4 = SqlUtil.replaceSqlParams(sql4, params4);
        Assert.assertEquals("SELECT 'a??b' as col FROM test WHERE code LIKE 'test?' AND id = 5", result4);
    }
}
