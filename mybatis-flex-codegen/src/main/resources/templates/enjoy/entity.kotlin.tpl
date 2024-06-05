#set(withSwagger = entityConfig.isWithSwagger())
#set(withActiveRecord = entityConfig.isWithActiveRecord())

package #(entityPackageName)

#for(importClass : table.buildImports(isBase))
import #(importClass)
#end

#if(withActiveRecord)
import com.mybatisflex.core.activerecord.Model
#end

#if(withSwagger && swaggerVersion.getName() == "FOX")
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
#end
#if(withSwagger && swaggerVersion.getName() == "DOC")
import io.swagger.v3.oas.annotations.media.Schema
#end
#if(!isBase)
/**
 * #(table.getComment()) 实体类。
 *
 * @author #(javadocConfig.getAuthor())
 * @since #(javadocConfig.getSince())
 */
#(table.buildTableAnnotation())
#end
#if(isBase)open #end class #(entityClassName) #if(withActiveRecord) : Model<#(entityClassName)>()#else#(table.buildKtExtends(isBase))#end  {
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
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiModelProperty("#(column.comment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Schema(description = "#(column.comment)")
    #end
    #if(isBase)open #end var #(column.property): #(column.propertySimpleType)? = #if(isNotBlank(column.propertyDefaultValue)) = #(column.propertyDefaultValue)#else null#end

#end
}
