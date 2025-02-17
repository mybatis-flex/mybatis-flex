#set(withLombok = entityConfig.isWithLombok())
#set(withSwagger = entityConfig.isWithSwagger())
#set(swaggerVersion = entityConfig.getSwaggerVersion())
#set(withActiveRecord = entityConfig.isWithActiveRecord())
#set(jdkVersion = entityConfig.getJdkVersion())
package #(entityPackageName);

#for(importClass : table.buildImports(isBase))
import #(importClass);
#end
#if(withActiveRecord)
import com.mybatisflex.core.activerecord.Model;
#end

#if(jdkVersion >= 14)
import java.io.Serial;
#end

#if(!isBase)
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
#if(entityConfig.isLombokAllArgsConstructorEnable())
import lombok.AllArgsConstructor;
#end
import lombok.Builder;
import lombok.Data;
#if(entityConfig.isLombokNoArgsConstructorEnable())
import lombok.NoArgsConstructor;
#end
#if(entityConfig.getSuperClass(table))
import lombok.EqualsAndHashCode;
#end
#end
#end

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
#if(withLombok)
#if(withActiveRecord)
@Accessors(chain = true)
@Data(staticConstructor = "create")
@EqualsAndHashCode(callSuper = true)
#else
@Data
@Builder
#if(entityConfig.isLombokNoArgsConstructorEnable())
@NoArgsConstructor
#end
#if(entityConfig.isLombokAllArgsConstructorEnable())
@AllArgsConstructor
#end
#if(entityConfig.getSuperClass(table))
@EqualsAndHashCode(callSuper = true)
#end
#end
#end
#if(withSwagger && swaggerVersion.getName() == "FOX")
@ApiModel("#(table.getComment())")
#end
#if(withSwagger && swaggerVersion.getName() == "DOC")
@Schema(description = "#(table.getComment())")
#end
#(table.buildTableAnnotation()) #end
public class #(entityClassName)#if(withActiveRecord) extends Model<#(entityClassName)>#else#(table.buildExtends(isBase))#(table.buildImplements())#end  {

    #if(jdkVersion >= 14)
    @Serial
    #end
    private static final long serialVersionUID = 1L;

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
    private #(column.propertySimpleType) #(column.property)#if(hasText(column.propertyDefaultValue)) = #(column.propertyDefaultValue)#end;

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
