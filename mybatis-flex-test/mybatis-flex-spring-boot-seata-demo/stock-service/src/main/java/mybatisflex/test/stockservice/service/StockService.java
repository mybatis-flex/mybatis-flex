/*
 *  Copyright 1999-2021 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package mybatisflex.test.stockservice.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import io.seata.spring.annotation.GlobalLock;
import io.seata.spring.annotation.GlobalTransactional;
import mybatisflex.test.stockservice.persistence.Stock;
import mybatisflex.test.stockservice.persistence.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private DataSource dataSource;

    public void deduct(String commodityCode, int count) {
        //There is a latent isolation problem here.
        //I hope that users can solve it and deepen their understanding of seata isolation.
        //At the bottom I will put a reference solution.
        Stock stock = stockMapper.findByCommodityCode(commodityCode);
        stock.setCount(stock.getCount() - count);
        stockMapper.updateById(stock);
    }

    @GlobalLock
    public Stock get(Integer id) {
        return stockMapper.selectById(id);
    }

    /**
     * 0.8.0 release
     *
     * @throws SQLException
     */
    @GlobalTransactional
    public void batchUpdate() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            String sql = "update stock_tbl set count = ?" + "    where id = ? and commodity_code = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 100);
            preparedStatement.setLong(2, 1);
            preparedStatement.setString(3, "2001");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 200);
            preparedStatement.setLong(2, 2);
            preparedStatement.setString(3, "2002");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 300);
            preparedStatement.setLong(2, 3);
            preparedStatement.setString(3, "2003");
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            connection.commit();
            System.out.println(1 / 0);
        } catch (Exception e) {
            throw e;
        } finally {
            connection.close();
            preparedStatement.close();
        }
    }

    /**
     * 0.8.0 release
     *
     * @throws SQLException
     */
    @GlobalTransactional
    public void batchDelete() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            String sql = "delete from stock_tbl where  count = ? and commodity_code = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 11);
            preparedStatement.setString(2, "2001");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 22);
            preparedStatement.setString(2, "2002");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 33);
            preparedStatement.setString(2, "2003");
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            connection.commit();
            System.out.println(1 / 0);
        } catch (Exception e) {
            throw e;
        } finally {
            connection.close();
            preparedStatement.close();
        }
    }
}

/*
reference solution:
    @Transactional
    public void deduct(String commodityCode, int count) {
        //select + for update
        Stock stock = stockMapper.findByCommodityCode(commodityCode);
        stock.setCount(stock.getCount() - count);
        stockMapper.updateById(stock);
    }
    1.select for update,refer https://seata.io/zh-cn/docs/overview/faq.html#4
    2.(optional)use @Transactional,keep X locks held until connection submission
*/
