package com.mybatisflex.coretest;

import com.mybatisflex.core.query.If;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.Assert;
import org.junit.Test;

public class PlusCompatibleTest {

    @Test
    public void testPlusCompatible1() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.from("tb_account")
            .eq("column1", "value1")
            .ge(Account::getAge, 18)
            .or(q1 -> {
                q1.eq("column2", "value2")
                    .ge(Account::getSex, 0);
            });

        Assert.assertEquals("SELECT * FROM `tb_account` " +
                "WHERE column1 = 'value1' " +
                "AND `age` >= 18 " +
                "OR (column2 = 'value2' AND `sex` >= 0)"
            , queryWrapper.toSQL());

        System.out.println(queryWrapper.toSQL());
    }

    @Test
    public void testPlusCompatible2() {
        String value1 = null;
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.from("user")
            .eq("column1", value1, If::hasText)
            .ge(Account::getAge, 18)
            .or(qw -> {
                qw.likeLeft("column2", "value2");
            })
            .or(q1 -> {
                q1.eq("column3", "value3")
                    .ge(Account::getSex, 0);
            });

        Assert.assertEquals("SELECT * FROM `user` " +
                "WHERE `tb_account`.`age` >= 18 " +
                "OR (column2 LIKE 'value2%') " +
                "OR (column3 = 'value3' AND `tb_account`.`sex` >= 0)"
            , queryWrapper.toSQL());

        System.out.println(queryWrapper.toSQL());
    }
}
