package #(globalConfig.entityPackage);

#for(importClass:table.buildImports())
import #(importClass);
#end


#(table.buildTableAnnotation())
public class #(table.buildEntityClassName()) {

#for(column: table.columns)    #(column.buildAnnotations())
    private #(column.propertySimpleType) #(column.property);

#end

    #if(!globalConfig.isEntityWithLombok())
    #for(column: table.columns)
    public #(column.propertySimpleType) #(column.buildGetter())() {
        return #(column.property);
    }

    public void  #(column.buildSetter())(#(column.propertySimpleType) #(column.property)) {
        this.#(column.property) = #(column.property);
    }

    #end
    #end
}
