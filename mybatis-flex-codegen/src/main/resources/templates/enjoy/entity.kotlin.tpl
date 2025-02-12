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
#if(javadocConfig.getAuthor())
 * @author #(javadocConfig.getAuthor())
#end
#if(javadocConfig.getSince())
 * @since #(javadocConfig.getSince())
#end
 */
#(table.buildTableAnnotation())
#end
open class #(entityClassName) #if(withActiveRecord) : Model<#(entityClassName)>()#else#(table.buildKtExtends(isBase))#end  {
#for(column : table.columns)
    #set(comment = javadocConfig.formatColumnComment(column.comment))
    #if(hasText(comment))
    /**
     * #(comment)
     */
    #end
    #set(annotations = column.buildAnnotations())
    #if(hasText(annotations))
    #(annotations)
    #end
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiModelProperty("#(column.comment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Schema(description = "#(column.comment)")
    #end
    open var #(column.property): #(column.propertySimpleType)? = #if(hasText(column.propertyDefaultValue)) = #(column.propertyDefaultValue)#else null#end

#end
}
