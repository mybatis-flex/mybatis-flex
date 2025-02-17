package #(entityPackageName);
#set(withLombok = entityConfig.isWithLombok())
#set(withSwagger = entityConfig.isWithSwagger())
#set(swaggerVersion = entityConfig.getSwaggerVersion())
#set(withActiveRecord = entityConfig.isWithActiveRecord())
#set(jdkVersion = entityConfig.getJdkVersion())

#for(importClass : table.buildImports(false))
import #(importClass);
#end
import #(baseClassPackage).#(baseClassName);

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
#if(entityConfig.isLombokAllArgsConstructorEnable())
import lombok.AllArgsConstructor;
#end
import lombok.Builder;
import lombok.Data;
#if(entityConfig.isLombokNoArgsConstructorEnable())
import lombok.NoArgsConstructor;
#end
#end
#end
#if(jdkVersion >= 14)
import java.io.Serial;
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
#end
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
#end
#end
#if(withSwagger && swaggerVersion.getName() == "FOX")
@ApiModel("#(table.getComment())")
#end
#if(withSwagger && swaggerVersion.getName() == "DOC")
@Schema(description = "#(table.getComment())")
#end
#if(!isBase)#(table.buildTableAnnotation())#end
public class #(entityClassName) extends #(baseClassName) {
}
