package #(globalConfig.mapperPackage);

import com.mybatisflex.core.BaseMapper;
import #(globalConfig.entityPackage).#(table.buildEntityClassName());

public interface #(globalConfig.mapperClassPrefix ??)#(table.buildEntityClassName())#(globalConfig.mapperClassSuffix ??) extends BaseMapper<#(table.buildEntityClassName())> {

}
