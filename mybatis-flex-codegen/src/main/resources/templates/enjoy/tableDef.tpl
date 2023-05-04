package #(globalConfig.entityPackage).tables;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;


public class #(table.buildTableDefClassName()) extends TableDef {

    public static final #(table.buildTableDefClassName()) #(table.name) = new #(table.buildTableDefClassName())("#(table.name)");

#for(column: table.columns)
    public QueryColumn #(column.name) = new QueryColumn(this, "#(column.name)");
#end

    public QueryColumn[] default_columns = new QueryColumn[]{#for(column: table.columns) #if(!column.name.equals("del_flag"))#(column.name)#if(for.index + 1 != for.size),#end#end#end};
    public QueryColumn[] all_columns = new QueryColumn[]{#for(column: table.columns) #(column.name)#if(for.index + 1 != for.size),#end#end};

    public #(table.buildTableDefClassName())(String tableName) {
        super(tableName);
    }
}
