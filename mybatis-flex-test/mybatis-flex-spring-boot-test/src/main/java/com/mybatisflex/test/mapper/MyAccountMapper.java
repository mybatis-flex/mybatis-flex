package com.mybatisflex.test.mapper;

import com.mybatisflex.test.model.Account;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface MyAccountMapper extends AccountMapper {


    Account selectByName(@Param("name") String name);

    @Select("select * from tb_account where id = #{id} and id =#{id}")
    Account selectById(@Param("id") Object id);
}
