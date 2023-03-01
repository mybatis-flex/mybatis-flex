package com.mybatisflex.core.querywrapper;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.util.StringUtil;

import java.util.List;

/**
 * 操作类型的操作
 * 示例1：and exist (select 1 from ... where ....)
 * 示例2：and not exist (select ... from ... where ....)
 */
public class OperatorSelectCondition extends QueryCondition {
    //操作符，例如 exist, not exist
    private String operator;
    private QueryWrapper queryWrapper;

    public OperatorSelectCondition(String operator, QueryWrapper queryWrapper) {
        this.operator = operator;
        this.queryWrapper = queryWrapper;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();

        //检测是否生效
        if (checkEffective()) {
            String childSql = dialect.buildSelectSql(queryWrapper);
            if (StringUtil.isNotBlank(childSql)) {

                QueryCondition effectiveBefore = getEffectiveBefore();
                if (effectiveBefore != null) {
                    sql.append(effectiveBefore.connector);
                }
                sql.append(operator).append("(").append(childSql).append(")");
            }
        }

        if (this.next != null) {
            return sql + next.toSql(queryTables, dialect);
        }

        return sql.toString();
    }

    @Override
    public Object getValue() {
        return queryWrapper.getValueArray();
    }
}
