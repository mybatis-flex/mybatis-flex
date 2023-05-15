package com.mybatisflex.codegen.config;

import com.mybatisflex.core.util.StringUtil;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 生成软件包的配置。
 *
 * @author 王帅
 * @since 2023-05-15
 */
@Data
@Accessors(chain = true)
public class PackageConfig {

    /**
     * 代码生成目录。
     */
    private String sourceDir;

    /**
     * 根包。
     */
    private String basePackage = "com.mybatisflex";

    /**
     * Entity 所在包。
     */
    private String entityPackage;

    /**
     * Mapper 所在包。
     */
    private String mapperPackage;

    /**
     * Service 所在包。
     */
    private String servicePackage;

    /**
     * ServiceImpl 所在包。
     */
    private String serviceImplPackage;

    /**
     * Controller 所在包。
     */
    private String controllerPackage;

    /**
     * TableDef 所在包。
     */
    private String tableDefPackage;

    public String getSourceDir() {
        if (sourceDir == null || StringUtil.isBlank(sourceDir)) {
            return System.getProperty("user.dir") + "/src/main/java";
        }
        return sourceDir;
    }

    public String getEntityPackage() {
        if (StringUtil.isBlank(entityPackage)) {
            return basePackage.concat(".entity");
        }
        return entityPackage;
    }

    public String getMapperPackage() {
        if (StringUtil.isBlank(mapperPackage)) {
            return basePackage.concat(".mapper");
        }
        return mapperPackage;
    }

    public String getServicePackage() {
        if (StringUtil.isBlank(servicePackage)) {
            return basePackage.concat(".service");
        }
        return servicePackage;
    }

    public String getServiceImplPackage() {
        if (StringUtil.isBlank(serviceImplPackage)) {
            return basePackage.concat(".service.impl");
        }
        return serviceImplPackage;
    }

    public String getControllerPackage() {
        if (StringUtil.isBlank(controllerPackage)) {
            return basePackage.concat(".controller");
        }
        return controllerPackage;
    }

    public String getTableDefPackage() {
        if (StringUtil.isBlank(tableDefPackage)) {
            return getEntityPackage().concat(".tables");
        }
        return tableDefPackage;
    }

}