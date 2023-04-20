package com.mybatisflex.test;

import com.mybatisflex.annotation.SetListener;

public class AccountOnSetListener implements SetListener {
    @Override
    public Object onSet(Object entity, String property, Object value) {
//        System.out.println(">>>>>>> property: " + property +" value:" + value);
        return value;
    }
}
