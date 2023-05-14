package #(globalConfig.serviceImplPackage);

import #(table.buildServiceImplImport());
import #(globalConfig.entityPackage).#(table.buildEntityClassName());
import #(globalConfig.mapperPackage).#(table.buildMapperClassName());
import #(globalConfig.servicePackage).#(table.buildServiceClassName());
import org.springframework.stereotype.Service;

@Service
public class #(table.buildServiceImplClassName()) extends #(table.buildServiceImplName())<#(table.buildMapperClassName()), #(table.buildEntityClassName())> implements #(table.buildServiceClassName()) {

}