package com.mybatisflex.coretest;

import com.mybatisflex.core.util.StringUtil;
import org.junit.Test;

public class StringUtilTest {

    @Test
    public void underlineToCamel() {
        String testString1 = "aa_bb_";
        String underlineToCamel = StringUtil.underlineToCamel(testString1);
        System.out.println(underlineToCamel);

        String underline = StringUtil.camelToUnderline(underlineToCamel);
        System.out.println(underline);
    }
}
