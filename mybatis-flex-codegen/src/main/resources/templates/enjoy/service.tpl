package #(globalConfig.servicePackage);

import #(table.buildServiceImport());
import #(globalConfig.entityPackage).#(table.buildEntityClassName());

public interface #(table.buildServiceClassName()) extends #(table.buildServiceName())<#(table.buildEntityClassName())> {

}