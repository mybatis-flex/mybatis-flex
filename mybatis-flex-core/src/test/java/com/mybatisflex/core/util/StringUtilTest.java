package com.mybatisflex.core.util;

import com.mybatisflex.processor.util.StrUtil;
import org.junit.Assert;
import org.junit.Test;


public class StringUtilTest {

    @Test
    public void testCamelToUnderline() {
        String s1 = StringUtil.camelToUnderline("AAA");
        String s1_ = StrUtil.camelToUnderline("AAA");
        Assert.assertEquals(s1, "aaa");
        Assert.assertEquals(s1_, "aaa");

        String s2 = StringUtil.camelToUnderline("StudentIDRoom");
        String s2_ = StrUtil.camelToUnderline("StudentIDRoom");
        Assert.assertEquals(s2, "student_idroom");
        Assert.assertEquals(s2_, "student_idroom");

        String s3 = StringUtil.camelToUnderline("StudentIdRoom");
        String s3_ = StrUtil.camelToUnderline("StudentIdRoom");
        Assert.assertEquals(s3, "student_id_room");
        Assert.assertEquals(s3_, "student_id_room");

        String s4 = StringUtil.camelToUnderline("Student_ID");
        String s4_ = StrUtil.camelToUnderline("Student_ID");
        Assert.assertEquals(s4, "student_id");
        Assert.assertEquals(s4_, "student_id");

        String s5 = StringUtil.camelToUnderline("StudentID");
        String s5_ = StrUtil.camelToUnderline("StudentID");
        Assert.assertEquals(s5, "student_id");
        Assert.assertEquals(s5_, "student_id");

        String s6 = StringUtil.camelToUnderline("Student_Id");
        String s6_ = StrUtil.camelToUnderline("Student_Id");
        Assert.assertEquals(s6, "student_id");
        Assert.assertEquals(s6_, "student_id");

        String s7 = StringUtil.camelToUnderline("student_id");
        String s7_ = StrUtil.camelToUnderline("student_id");
        Assert.assertEquals(s7, "student_id");
        Assert.assertEquals(s7_, "student_id");

    }
}
