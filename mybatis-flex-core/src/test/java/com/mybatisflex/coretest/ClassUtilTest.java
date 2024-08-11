package com.mybatisflex.coretest;

import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ClassUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ClassUtilTest {
    public static class A extends E implements B, D{
        public String name1;
    }
    interface B extends C{
        default void test1() {
        }
    }
    interface C {
        default void test2() {
        }
    }
    interface D {
        default void test3() {
        }
    }

    public static class E extends F{
        public String name1;
    }

    public static class F {
        public String name3;
    }

    @Test
    public void testMethods() {
        System.out.println(ClassUtil.getAnyMethod(A.class, "test2", "test3"));
//        List<Method> methods = new ArrayList<>();
//        ClassUtil.doGetMethods(A.class,methods,null,false);
//        methods.forEach(method -> System.out.println("all:"+method.getDeclaringClass()+ ":" +method.getName()));
//        methods.clear();
//        ClassUtil.doGetMethods(A.class,methods,null,true);
//        methods.forEach(method -> System.out.println("firstOnly:"+method.getDeclaringClass()+ ":" +method.getName()));
        assertEquals("public default void com.mybatisflex.coretest.ClassUtilTest$C.test2()",ClassUtil.getAnyMethod(A.class, "test2", "test3").toString());
        System.out.println(ClassUtil.getAllMethods(A.class));
        assertEquals(3,ClassUtil.getAllMethods(A.class).size());
        System.out.println(ClassUtil.getAllMethods(A.class, method -> method.getName().equals("test2")));
        assertEquals(1,ClassUtil.getAllMethods(A.class, method -> method.getName().equals("test2")).size());
        System.out.println(ClassUtil.getFirstMethod(A.class, method -> method.getName().equals("test3")));
        assertEquals("D",ClassUtil.getFirstMethod(A.class, method -> method.getName().equals("test3")).getDeclaringClass().getSimpleName());
    }

    @Test
    public void testFields() {
        List<Field> fields = ClassUtil.getAllFields(A.class);
        fields.forEach(field -> System.out.println(field.getDeclaringClass() + ":" + field.getName()));
        assertEquals(3, ClassUtil.getAllFields(A.class).size());
        System.out.println(ClassUtil.getAllFields(A.class, field -> field.getDeclaringClass().getSimpleName().startsWith("E")));
        assertEquals(1, ClassUtil.getAllFields(A.class, field -> field.getDeclaringClass().getSimpleName().startsWith("E")).size());
    }
    @Test
    public void testGetColumnFields() {
        TableInfoFactory.getColumnFields(A.class).forEach(field -> System.out.println(field.getDeclaringClass() + ":" + field.getName()));
        assertEquals(2, TableInfoFactory.getColumnFields(A.class).size());
    }
}
