package com.mybatisflex.core.dialect.impl;

import static com.mybatisflex.core.constant.FuncName.SUBSTRING;
import static com.mybatisflex.core.constant.SqlConsts.DELIMITER;
import static com.mybatisflex.core.constant.SqlConsts.NULLS_FIRST;
import static com.mybatisflex.core.constant.SqlConsts.NULLS_LAST;
import static com.mybatisflex.core.constant.SqlConsts.ORDER_BY;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryOrderBy;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;

public class DB2105Dialect extends CommonsDialectImpl {
      //TODO: 根据DatabaseMetaData获取数据库厂商名和版本号
    public static final String DB2_1005_PRODUCT_VERSION = "1005";
    public static final String DB2_PRODUCT_NAME = "DB2";
    private static final Pattern ORDERBY_PATTERN = Pattern.compile("(\\S+)\\s+(\\S*)\\s*("+NULLS_FIRST.trim()+"|"+NULLS_LAST.trim()+")");
    private static final Pattern SUBSTRING_PATTERN = Pattern.compile("((?i)"+SUBSTRING.trim()+")(\\s*)(\\(.*?\\))");

    public DB2105Dialect(KeywordWrap keywordWrap, LimitOffsetProcessor limitOffsetProcessor) {
        super(keywordWrap, limitOffsetProcessor);
    }

    @Override
    public String buildSelectSql(QueryWrapper queryWrapper){
        String sql = super.buildSelectSql(queryWrapper);
        if(sql!=null){
            Matcher matcher = SUBSTRING_PATTERN.matcher(sql);
            sql = matcher.replaceAll("SUBSTR$2$3");
        }
        return sql;
    }

    @Override
    protected void buildOrderBySql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables) {
        List<QueryOrderBy> orderBys = CPI.getOrderBys(queryWrapper);
        if (orderBys != null && !orderBys.isEmpty()) {
            sqlBuilder.append(ORDER_BY);
            int index = 0;
            for (QueryOrderBy orderBy : orderBys) {
                String orderBySql = orderBy.toSql(queryTables, this);
                orderBySql = convertOrderBySqlForDB2105(orderBySql);  // 转换SQL语句
                sqlBuilder.append(orderBySql);
                if (index != orderBys.size() - 1) {
                    sqlBuilder.append(DELIMITER);
                }
                index++;
            }
        }
    }

    private  String convertOrderBySqlForDB2105(String sql) {
        Matcher matcher = ORDERBY_PATTERN.matcher(sql);
        if (matcher.find()) {
            String column = matcher.group(1);
            String orderType = matcher.group(2);
            String nullOrder = matcher.group(3);
            if (NULLS_FIRST.trim().equals(nullOrder)) {
                sql = "CASE WHEN " + column + " IS NULL THEN 0 ELSE 1 END, " + column+" "+orderType;
            } else if (NULLS_LAST.trim().equals(nullOrder)) {
                sql = "CASE WHEN " + column + " IS NULL THEN 1 ELSE 0 END, " + column+" "+orderType;
            }
        }
        return sql;
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
