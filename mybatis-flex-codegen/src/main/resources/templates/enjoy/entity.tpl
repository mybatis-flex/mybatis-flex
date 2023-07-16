#set(withLombok = entityConfig.isWithLombok())
#set(withSwagger = entityConfig.isWithSwagger())
package #(packageConfig.entityPackage);

#for(importClass : table.buildImports())
import #(importClass);
#end
#if(withSwagger)
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
#end
#if(withLombok)
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
#end

/**
 * #(table.getComment()) 实体类。
 *
 * @author #(javadocConfig.getAuthor())
 * @since #(javadocConfig.getSince())
 */
#if(withLombok)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
#end
#if(withSwagger)
@ApiModel("#(table.getComment())")
#end
#(table.buildTableAnnotation())
public class #(table.buildEntityClassName())#(table.buildExtends())#(table.buildImplements()) {
#for(column : table.columns)

    #set(comment = javadocConfig.formatColumnComment(column.comment))
    #if(isNotBlank(comment))
    /**
     * #(comment)
     */
    #end
    #set(annotations = column.buildAnnotations())
    #if(isNotBlank(annotations))
    #(annotations)
    #end
    #if(withSwagger)
    @ApiModelProperty("#(column.comment)")
    #end
    private #(column.propertySimpleType) #(column.property);
#end

#if(!withLombok)
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
