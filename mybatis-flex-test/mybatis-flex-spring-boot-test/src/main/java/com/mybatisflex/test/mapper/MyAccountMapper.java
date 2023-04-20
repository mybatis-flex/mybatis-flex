package com.mybatisflex.test.mapper;

import com.mybatisflex.test.model.Account;
import org.apache.ibatis.annotations.Param;

public interface MyAccountMapper extends AccountMapper {


    Account selectByName(@Param("name") String name);
}
