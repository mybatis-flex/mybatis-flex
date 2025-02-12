package #(packageConfig.serviceImplPackage);

import com.mybatisflex.solon.service.impl.ServiceImpl;
import #(packageConfig.entityPackage).#(table.buildEntityClassName());
import #(packageConfig.mapperPackage).#(table.buildMapperClassName());
#if(table.getGlobalConfig().isServiceGenerateEnable())
import #(packageConfig.servicePackage).#(table.buildServiceClassName());
#end
import org.noear.solon.annotation.Component;

/**
 * #(table.getComment()) 服务层实现。
 *
#if(javadocConfig.getAuthor())
 * @author #(javadocConfig.getAuthor())
#end
#if(javadocConfig.getSince())
 * @since #(javadocConfig.getSince())
#end
 */
@Component
public class #(table.buildServiceImplClassName()) extends ServiceImpl<#(table.buildMapperClassName()), #(table.buildEntityClassName())> #if(table.getGlobalConfig().isServiceGenerateEnable()) implements #(table.buildServiceClassName()) #end{

}
