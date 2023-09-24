package com.mybatisflex.coretest;



import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangxin
 * @since 2023-09-24
 */
public class Fastjson2TypeHandlerTest {
    public interface ConfigData {

    }

    public static class FtpFileClientConfig implements ConfigData {


        private String basePath;


        private String domain;

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }
    }

    public static class MyFastjson2TypeHandler extends Fastjson2TypeHandler {


        public MyFastjson2TypeHandler(Class<?> propertyType) {
            super(propertyType);
        }


        public MyFastjson2TypeHandler(Class<?> propertyType, Class<?> genericType) {
            super(propertyType, genericType);
        }

        @Override
        public Object parseJson(String json) {
            return super.parseJson(json);
        }

        @Override
        public String toJson(Object object) {
            return super.toJson(object);
        }
    }

    @Test
    public void Test() {
        List<ConfigData> configDataList = new ArrayList<>(0);
        MyFastjson2TypeHandler interfaceHandler = new MyFastjson2TypeHandler(ConfigData.class);
        MyFastjson2TypeHandler classHandler = new MyFastjson2TypeHandler(FtpFileClientConfig.class);
        MyFastjson2TypeHandler interfaceListHandler = new MyFastjson2TypeHandler(configDataList.getClass(), ConfigData.class);
        MyFastjson2TypeHandler classListHandler = new MyFastjson2TypeHandler(configDataList.getClass(), FtpFileClientConfig.class);
        FtpFileClientConfig ftpFileClientConfig = new FtpFileClientConfig();
        ftpFileClientConfig.setDomain("http://test.com");
        ftpFileClientConfig.setBasePath("/var/upload");
        List<FtpFileClientConfig> ftpFileClientConfigList = new ArrayList<>(1);
        ftpFileClientConfigList.add(ftpFileClientConfig);
        String interfaceJson = interfaceHandler.toJson(ftpFileClientConfig);
        System.out.println("interface       :" + interfaceJson);
        String classJson = classHandler.toJson(ftpFileClientConfig);
        System.out.println("class           :" + classJson);
        String interfaceListJson = interfaceHandler.toJson(ftpFileClientConfigList);
        System.out.println("interfaceList   :" + interfaceListJson);
        String classListJson = classHandler.toJson(ftpFileClientConfigList);
        System.out.println("classList       :" + classListJson);

        FtpFileClientConfig ftpFileClientConfig1 = (FtpFileClientConfig) interfaceHandler.parseJson(interfaceJson);
        assert (ftpFileClientConfig.getDomain().equals(ftpFileClientConfig1.getDomain()));

        FtpFileClientConfig ftpFileClientConfig2 = (FtpFileClientConfig) classHandler.parseJson(interfaceJson);
        assert (ftpFileClientConfig.getDomain().equals(ftpFileClientConfig2.getDomain()));

        ftpFileClientConfigList = (List<FtpFileClientConfig>) interfaceListHandler.parseJson(interfaceListJson);
        assert (ftpFileClientConfig.getDomain().equals(ftpFileClientConfigList.get(0).getDomain()));

        ftpFileClientConfigList = (List<FtpFileClientConfig>) classListHandler.parseJson(interfaceListJson);
        assert (ftpFileClientConfig.getDomain().equals(ftpFileClientConfigList.get(0).getDomain()));

    }
}
