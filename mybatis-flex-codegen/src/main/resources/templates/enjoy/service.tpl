package #(packageConfig.servicePackage);

import #(serviceConfig.buildSuperClassImport());
import #(packageConfig.entityPackage).#(table.buildEntityClassName());

/**
 * #(table.getComment()) 服务层。
 *
#if(javadocConfig.getAuthor())
 * @author #(javadocConfig.getAuthor())
#end
#if(javadocConfig.getSince())
 * @since #(javadocConfig.getSince())
#end
 */
public interface #(table.buildServiceClassName()) extends #(serviceConfig.buildSuperClassName())<#(table.buildEntityClassName())> {

}
