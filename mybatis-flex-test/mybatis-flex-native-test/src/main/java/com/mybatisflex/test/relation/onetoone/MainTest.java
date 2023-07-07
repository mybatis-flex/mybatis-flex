package com.mybatisflex.test.relation.onetoone;

import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;

public class MainTest {

    public static void main(String[] args) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(Book.class);
        tableInfo.buildResultMap(new FlexConfiguration());

        System.out.println(tableInfo);
    }
}
