package com.mybatisflex.test.issue;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.test.AppConfig;
import com.mybatisflex.test.issue.vo.UserVO;
import com.mybatisflex.test.mapper.TbClassMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.mybatisflex.test.issue.def.TClassTableDef.TB_CLASS;
import static com.mybatisflex.test.issue.def.TUserTableDef.TB_USER;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class Issue80Test {
    @Autowired
    private TbClassMapper tbClassMapper;

    @Test
    public void testQuery() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(TB_USER.ID, TB_USER.NAME)
            .from(TB_CLASS)
            .leftJoin(TB_USER).on(TB_USER.ID.eq(TB_CLASS.USER_ID));

        Page<UserVO> page = new Page<>();
        page.setPageNumber(1);
        page.setPageSize(10);
        Page<UserVO> result = tbClassMapper.paginateAs(page, queryWrapper, UserVO.class);
        System.out.println(">>> result = " + result.toString());
    }
}
