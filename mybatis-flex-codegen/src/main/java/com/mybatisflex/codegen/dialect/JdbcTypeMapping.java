package com.mybatisflex.codegen.dialect;

import com.mybatisflex.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class JdbcTypeMapping {

    private static Map<String, String> mapping = new HashMap<>();

    static {
        mapping.put("[B", "byte[]");
    }

    public void registerMapping(Class<?> from, Class<?> to) {
        mapping.put(from.getName(), to.getName());
    }

    public void registerMapping(String from, String to) {
        mapping.put(from, to);
    }

    static String getType(String jdbcType) {
        String registered = mapping.get(jdbcType);
        return StringUtil.isNotBlank(registered) ? registered : jdbcType;
    }
}
