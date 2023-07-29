#set(withLombok = entityConfig.isWithLombok())
#set(withSwagger = entityConfig.isWithSwagger())
#set(swaggerVersion = entityConfig.getSwaggerVersion())
#set(withActiveRecord = entityConfig.isWithActiveRecord())
#set(entityClassName = table.buildEntityClassName())
package #(packageConfig.entityPackage);

#for(importClass : table.buildImports())
import #(importClass);
#end
#if(withActiveRecord)
import com.mybatisflex.core.activerecord.Model;
#end
#if(withSwagger && swaggerVersion.getName() == "FOX")
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
#end
#if(withSwagger && swaggerVersion.getName() == "DOC")
import io.swagger.v3.oas.annotations.media.Schema;
#end
#if(withLombok)
#if(withActiveRecord)
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
#else
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
#end
#end

/**
 * #(table.getComment()) 实体类。
 *
 * @author #(javadocConfig.getAuthor())
 * @since #(javadocConfig.getSince())
 */
#if(withLombok)
#if(withActiveRecord)
@Accessors(chain = true)
@Data(staticConstructor = "create")
@EqualsAndHashCode(callSuper = true)
#else
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
#end
#end
#if(withSwagger && swaggerVersion.getName() == "FOX")
@ApiModel("#(table.getComment())")
#end
#if(withSwagger && swaggerVersion.getName() == "DOC")
@Schema(description = "#(table.getComment())")
#end
#(table.buildTableAnnotation())
public class #(entityClassName)#if(withActiveRecord) extends Model<#(entityClassName)>#else#(table.buildExtends())#(table.buildImplements())#end  {

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
    private #(column.propertySimpleType) #(column.property)#if(isNotBlank(column.propertyDefaultValue)) = #(column.propertyDefaultValue)#end;

#end
#if(!withLombok)
    #if(withActiveRecord)
    public static #(entityClassName) create() {
        return new #(entityClassName)();
    }

    #end
    #for(column: table.columns)
    public #(column.propertySimpleType) #(column.getterMethod())() {
        return #(column.property);
    }

    #if(withActiveRecord)
    public #(entityClassName) #(column.setterMethod())(#(column.propertySimpleType) #(column.property)) {
        this.#(column.property) = #(column.property);
        return this;
    }
    #else
    public void #(column.setterMethod())(#(column.propertySimpleType) #(column.property)) {
        this.#(column.property) = #(column.property);
    }
    #end

    #end
#end}
