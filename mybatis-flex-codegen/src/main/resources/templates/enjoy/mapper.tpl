package #(packageConfig.mapperPackage);

#if(mapperConfig.isMapperAnnotation())
import org.apache.ibatis.annotations.Mapper;
#end
import #(mapperConfig.buildSuperClassImport());
import #(packageConfig.entityPackage).#(table.buildEntityClassName());

/**
 * #(table.getComment()) 映射层。
 *
 * @author #(javadocConfig.getAuthor())
 * @since #(javadocConfig.getSince())
 */
#if(mapperConfig.isMapperAnnotation())
@Mapper
#end
public interface #(table.buildMapperClassName()) extends #(mapperConfig.buildSuperClassName())<#(table.buildEntityClassName())> {

}
