#set(tableDefClassName = table.buildTableDefClassName())
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

    public static final #(tableDefClassName) #(tableDefConfig.buildFieldName(table.buildEntityClassName() + tableDefConfig.instanceSuffix)) = new #(tableDefClassName)("#(table.name)");

#for(column: table.columns)
    public QueryColumn #(tableDefConfig.buildFieldName(column.property)) = new QueryColumn(this, "#(column.name)");
#end

    public QueryColumn #(tableDefConfig.buildFieldName("allColumns")) = new QueryColumn(this, "*");
    public QueryColumn[] #(tableDefConfig.buildFieldName("defaultColumns")) = new QueryColumn[]{#for(column: table.columns) #if(!column.isLarge())#(tableDefConfig.buildFieldName(column.property))#if(for.index + 1 != for.size),#end#end#end};

    public #(tableDefClassName)(String tableName) {
        super(tableName);
    }

}
