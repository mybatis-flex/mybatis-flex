#set(tableDefClassName = table.buildTableDefClassName())
#set(schema = table.schema == null ? "" : table.schema)
package #(packageConfig.tableDefPackage);

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * #(table.getComment()) 表定义层。
 *
 * @author #(javadocConfig.getAuthor())
 * @since #(javadocConfig.getSince())
 */
public class #(tableDefClassName) extends TableDef {

    public static final #(tableDefClassName) #(tableDefConfig.buildFieldName(table.buildEntityClassName() + tableDefConfig.instanceSuffix)) = new #(tableDefClassName)();

#for(column: table.columns)
    public final QueryColumn #(tableDefConfig.buildFieldName(column.property)) = new QueryColumn(this, "#(column.name)");
#end

    public final QueryColumn #(tableDefConfig.buildFieldName("allColumns")) = new QueryColumn(this, "*");
    public final QueryColumn[] #(tableDefConfig.buildFieldName("defaultColumns")) = new QueryColumn[]{#for(column: table.columns)#if(column.isDefaultColumn())#(tableDefConfig.buildFieldName(column.property))#if(for.index + 1 != for.size), #end#end#end};

    public #(tableDefClassName)() {
        super("#(schema)", "#(table.name)");
    }

}
