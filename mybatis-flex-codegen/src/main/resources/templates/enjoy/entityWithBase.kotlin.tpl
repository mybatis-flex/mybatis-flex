#set(withSwagger = entityConfig.isWithSwagger())
#set(withActiveRecord = entityConfig.isWithActiveRecord())

package #(entityPackageName)

#for(importClass : table.buildImports(isBase))
import #(importClass)
#end
import #(baseClassPackage).#(baseClassName);

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
/**
 * #(table.getComment()) 实体类。
 *
 * @author #(javadocConfig.getAuthor())
 * @since #(javadocConfig.getSince())
 */
#if(withSwagger && swaggerVersion.getName() == "FOX")
@ApiModel("#(table.getComment())")
#end
#if(withSwagger && swaggerVersion.getName() == "DOC")
@Schema(description = "#(table.getComment())")
#end
#if(!isBase)#(table.buildTableAnnotation())#end
class #(entityClassName) : #(baseClassName)()
