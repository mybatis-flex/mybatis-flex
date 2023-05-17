package com.mybatisflex.codegen.config;

/**
 * 生成 MapperXml 的配置。
 *
 * @author 王帅
 * @since 2023-05-17
 */
public class MapperXmlConfig {

    /**
     * MapperXml 文件的前缀。
     */
    private String filePrefix = "";

    /**
     * MapperXml 文件的后缀。
     */
    private String fileSuffix = "Mapper";

    public String getFilePrefix() {
        return filePrefix;
    }

    public MapperXmlConfig setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
        return this;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public MapperXmlConfig setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
        return this;
    }

}