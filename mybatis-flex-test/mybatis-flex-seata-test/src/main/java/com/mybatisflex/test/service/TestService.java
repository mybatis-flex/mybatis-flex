/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
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

package com.mybatisflex.test.service;


import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.test.entity.AccountTbl;
import com.mybatisflex.test.entity.OrderTbl;
import com.mybatisflex.test.entity.StockTbl;
import com.mybatisflex.test.entity.table.AccountTblTableDef;
import com.mybatisflex.test.mapper.AccountTblMapper;
import com.mybatisflex.test.mapper.OrderTblMapper;
import com.mybatisflex.test.mapper.StockTblMapper;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestService.class);

    @Autowired
    AccountTblMapper accountTblMapper;

    @Autowired
    OrderTblMapper orderTblMapper;

    @Autowired
    StockTblMapper stockTblMapper;

    //    @Transactional
    @GlobalTransactional
    public boolean buy() {
//        DataSourceKey.use("accountdb");
        LOGGER.warn(() -> "xid:" + RootContext.getXID());
        QueryWrapper account = new QueryWrapper();
        account.where(AccountTblTableDef.ACCOUNT_TBL.USER_ID.eq("1001"));
        AccountTbl accountTbl = accountTblMapper.selectOneByQuery(account);
        accountTbl.setMoney(accountTbl.getMoney() - 5);
        accountTblMapper.update(accountTbl);
        DataSourceKey.use("stockdb");
        QueryWrapper stock = new QueryWrapper();
        stock.where("id=1");
        StockTbl stockTbl = stockTblMapper.selectOneByQuery(stock);
        stockTbl.setCount(stockTbl.getCount() - 1);
        stockTblMapper.update(stockTbl);
        DataSourceKey.use("orderdb");
        OrderTbl orderTbl = new OrderTbl();
        orderTbl.setCount(5);
        orderTbl.setMoney(5);
        orderTbl.setUserId(accountTbl.getUserId());
        orderTbl.setCount(1);
        orderTbl.setCommodityCode(stockTbl.getCommodityCode());
        orderTblMapper.insert(orderTbl);
        return true;
    }

}
