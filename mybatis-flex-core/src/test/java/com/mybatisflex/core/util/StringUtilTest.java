package com.mybatisflex.core.util;

import org.junit.Assert;
import org.junit.Test;


public class StringUtilTest {

    @Test
    public void testCamelToUnderline(){
        String s1 = StringUtil.camelToUnderline("AAA");
        Assert.assertEquals(s1,"aaa");

        String s2 = StringUtil.camelToUnderline("StudentIDRoom");
        Assert.assertEquals(s2,"student_idroom");

        String s3 =StringUtil. camelToUnderline("StudentIdRoom");
        Assert.assertEquals(s3,"student_id_room");

        String s4 = StringUtil.camelToUnderline("Student_ID");
        Assert.assertEquals(s4,"student_id");

        String s44 = StringUtil.camelToUnderline("StudentID");
        Assert.assertEquals(s44,"student_id");

        String s5 = StringUtil.camelToUnderline("Student_Id");
        Assert.assertEquals(s5,"student_id");

        String s6 = StringUtil.camelToUnderline("student_id");
        Assert.assertEquals(s6,"student_id");

    }
}
