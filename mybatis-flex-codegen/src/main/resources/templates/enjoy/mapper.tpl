package #(packageConfig.mapperPackage);

#if(mapperConfig.isMapperAnnotation())
import org.apache.ibatis.annotations.Mapper;
#end
import #(mapperConfig.buildSuperClassImport());
import #(packageConfig.entityPackage).#(table.buildEntityClassName());

/**
 * #(table.getComment()) 映射层。
 *
#if(javadocConfig.getAuthor())
 * @author #(javadocConfig.getAuthor())
#end
#if(javadocConfig.getSince())
 * @since #(javadocConfig.getSince())
#end
 */
#if(mapperConfig.isMapperAnnotation())
@Mapper
#end
public interface #(table.buildMapperClassName()) extends #(mapperConfig.buildSuperClassName())<#(table.buildEntityClassName())> {

}
