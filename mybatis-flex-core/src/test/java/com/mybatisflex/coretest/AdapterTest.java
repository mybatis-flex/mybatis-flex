package com.mybatisflex.coretest;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryColumnAdapter;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryConditionAdapter;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.Assert;
import org.junit.Test;

import static com.mybatisflex.core.query.QueryMethods.pow;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;

/**
 * 适配器测试。
 *
 * @author 王帅
 * @since 2024-09-29
 */
public class AdapterTest {

    @Test
    public void testAdapter() {
        QueryColumn column = new QueryColumnAdapter(ACCOUNT.AGE.lt(18));
        QueryCondition condition = new QueryConditionAdapter(pow(ACCOUNT.IS_DELETE, 2));

        /*
         * SELECT
         *   ` age ` < 18 AS ` underAge `
         * FROM
         *   ` tb_account `
         * WHERE
         *   POW(` is_delete `, 2)
         */

        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(column.as("underAge"))
            .from(ACCOUNT)
            .where(condition);

        System.out.println(SqlFormatter.format(queryWrapper.toSQL()));

        Assert.assertTrue(true);
    }

}
