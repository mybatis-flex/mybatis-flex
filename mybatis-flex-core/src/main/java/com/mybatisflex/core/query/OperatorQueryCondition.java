package com.mybatisflex.core.query;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.util.StringUtil;

import java.util.List;

/**
 * 操作类型的操作
 * 示例1：and not ( id > 100 and name like %%)
 */
public class OperatorQueryCondition extends QueryCondition {
    
    private String operator;
    private QueryCondition child;

    public OperatorQueryCondition(String operator, QueryCondition child) {
        this.operator = operator;
        this.child = child;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();

        //检测是否生效
        if (checkEffective()) {
            String childSql = child.toSql(queryTables, dialect);
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
        return WrapperUtil.getValues(child);
    }
}
