package com.mybatisflex.test.issue113;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.test.AppConfig;
import com.mybatisflex.test.mapper.TbClassMapper;
import com.mybatisflex.test.model.TbClass;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.mybatisflex.test.issue113.def.TClassTableDef.TB_CLASS;
import static com.mybatisflex.test.issue113.def.TUserTableDef.TB_USER;

/**
 * <p>Title: Issue113Test. </p>
 * <p>Date: 2023/8/2 12:02 </p>
 *
 * @author loong0306
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class Issue113Test implements WithAssertions {
    @Autowired
    private TbClassMapper tbClassMapper;

    @Test
    public void testQuery() {
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(TB_CLASS);
        System.out.println(query.toSQL());
        List<TbClass> list = tbClassMapper.selectListByQuery(query);
        System.out.println(">>> result = " + list.toString());
    }

    @Test
    public void issue113() {
        QueryWrapper query = new QueryWrapper()
            .select()
            .from(TB_CLASS)
            .innerJoin(TB_USER)
            .on(TB_CLASS.USER_ID.eq(TB_USER.ID))
            .where(TB_CLASS.USER_ID.eq(1));
        System.out.println(query.toSQL());
        List<TbClass> list = tbClassMapper.selectListByQuery(query);
        System.out.println(">>> result = " + list.toString());
    }
}
