package #(packageConfig.serviceImplPackage);

import com.mybatisflex.solon.service.impl.ServiceImpl;
import #(packageConfig.entityPackage).#(table.buildEntityClassName());
import #(packageConfig.mapperPackage).#(table.buildMapperClassName());
import #(packageConfig.servicePackage).#(table.buildServiceClassName());
import org.noear.solon.annotation.Component;

/**
 * #(table.getComment()) 服务层实现。
 *
 * @author #(javadocConfig.getAuthor())
 * @since #(javadocConfig.getSince())
 */
@Component
public class #(table.buildServiceImplClassName()) extends ServiceImpl<#(table.buildMapperClassName()), #(table.buildEntityClassName())> implements #(table.buildServiceClassName()) {

}
