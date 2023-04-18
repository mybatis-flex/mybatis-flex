package #(globalConfig.entityPackage);

#for(importClass:table.buildImports())
import #(importClass);
#end


#(table.buildTableAnnotation())
public class #(table.buildEntityClassName())#(table.buildExtends())#(table.buildImplements()) {
#for(column: table.columns)

    #(column.buildAnnotations())private #(column.propertySimpleType) #(column.property);
#end

    #if(!globalConfig.isEntityWithLombok())
    #for(column: table.columns)
    public #(column.propertySimpleType) #(column.getterMethod())() {
        return #(column.property);
    }

    public void #(column.setterMethod())(#(column.propertySimpleType) #(column.property)) {
        this.#(column.property) = #(column.property);
    }

    #end
    #end
}
