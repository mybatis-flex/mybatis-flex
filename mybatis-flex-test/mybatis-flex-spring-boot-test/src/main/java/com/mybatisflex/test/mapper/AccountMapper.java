package com.mybatisflex.test.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.test.model.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
