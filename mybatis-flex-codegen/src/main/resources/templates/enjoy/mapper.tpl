package #(globalConfig.mapperPackage);

import #(table.buildMapperImport());
import #(globalConfig.entityPackage).#(table.buildEntityClassName());

public interface #(table.buildMapperClassName()) extends #(table.buildMapperName())<#(table.buildEntityClassName())> {

}
