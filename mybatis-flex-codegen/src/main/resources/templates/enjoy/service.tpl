package #(packageConfig.servicePackage);

import #(serviceConfig.buildSuperClassImport());
import #(packageConfig.entityPackage).#(table.buildEntityClassName());

public interface #(table.buildServiceClassName()) extends #(serviceConfig.buildSuperClassName())<#(table.buildEntityClassName())> {

}