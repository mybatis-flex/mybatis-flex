package com.mybatisflex.core.dialect.impl;

import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;

public class DB2105Dialect extends CommonsDialectImpl {
      //TODO: 根据DatabaseMetaData获取数据库厂商名和版本号
    public static final String DB2_1005_PRODUCT_VERSION = "1005";
    public static final String DB2_PRODUCT_NAME = "DB2";


    public DB2105Dialect(KeywordWrap keywordWrap, LimitOffsetProcessor limitOffsetProcessor) {
        super(keywordWrap, limitOffsetProcessor);
    }

    public interface DB2105LimitOffsetProcessor {
        LimitOffsetProcessor DB2105 = (dialect, sql, queryWrapper, limitRows, limitOffset) -> {
            StringBuilder limitSqlFragment = new StringBuilder(
                    "select * from ( select u_.*,rownumber() over()  as rn from ( ");
            limitSqlFragment.append(sql);
            limitSqlFragment.append(" )u_  ) temp_ where temp_.rn between ");

            if (limitRows != null && limitOffset != null) {
                limitSqlFragment.append(limitOffset + 1);
                limitSqlFragment.append(" and ");
                limitSqlFragment.append(limitRows + limitOffset);
            } else if (limitRows != null) {
                limitSqlFragment.append("1 and ");
                limitSqlFragment.append(limitRows);
            } else {
                return sql;
            }
            return limitSqlFragment;
        };
    }
}
