package #(globalConfig.mapperPackage);

import com.mybatisflex.core.BaseMapper;
import #(globalConfig.entityPackage).#(table.buildEntityClassName());

public interface #(table.buildMapperClassName()) extends BaseMapper<#(table.buildEntityClassName())> {

}
