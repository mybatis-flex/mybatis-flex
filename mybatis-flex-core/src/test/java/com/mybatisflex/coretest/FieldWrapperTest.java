

package com.mybatisflex.coretest;

import com.mybatisflex.core.util.FieldWrapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangxin
 * @since 2023-12-06
 */
public class FieldWrapperTest {
    public static class Demo  {
        private Map<String,Object> map1;
        private Map<String, List<Long>> map2 ;

        public Map<String, Object> getMap1() {
            return map1;
        }

        public void setMap1(Map<String, Object> map1) {
            this.map1 = map1;
        }

        public Map<String, List<Long>> getMap2() {
            return map2;
        }

        public void setMap2(Map<String, List<Long>> map2) {
            this.map2 = map2;
        }

    }
    @Test
    public void test01() {
        try {
            Map<String, List<Long>> listMap = new HashMap<>(0);
            List<Long> list = new ArrayList<>();
            list.add(1L);
            list.add(2L);
            listMap.put("1", list);
            Demo demo = new Demo();
            FieldWrapper fieldWrapper1 = FieldWrapper.of(demo.getClass(), "map1");
            FieldWrapper fieldWrapper2 = FieldWrapper.of(demo.getClass(), "map2");
            fieldWrapper1.set(listMap, demo);
            fieldWrapper2.set(listMap, demo);
            Map<String, Object> map1 = (Map<String, Object>) fieldWrapper1.get(demo);
            Map<String, List<Long>> map2 = (Map<String, List<Long>>) fieldWrapper1.get(demo);
            System.out.println(map1.get("1"));
            System.out.println(map2.get("1").get(0));
        }catch (Exception e){
            assert false;
        }
    }



}
