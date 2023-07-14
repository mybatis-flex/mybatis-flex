/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mybatisflex.core.mask;

/**
 * 内置的数据脱敏方式
 */
public class Masks {

    private Masks() {
    }

    /**
     * 手机号脱敏
     */
    public static final String MOBILE = "mobile";

    /**
     * 固定电话脱敏
     */
    public static final String FIXED_PHONE = "fixed_phone";

    /**
     * 身份证号脱敏
     */
    public static final String ID_CARD_NUMBER = "id_card_number";

    /**
     * 中文名脱敏
     */
    public static final String CHINESE_NAME = "chinese_name";

    /**
     * 地址脱敏
     */
    public static final String ADDRESS = "address";

    /**
     * 邮件脱敏
     */
    public static final String EMAIL = "email";

    /**
     * 密码脱敏
     */
    public static final String PASSWORD = "password";

    /**
     * 车牌号脱敏
     */
    public static final String CAR_LICENSE = "car_license";

    /**
     * 银行卡号脱敏
     */
    public static final String BANK_CARD_NUMBER = "bank_card_number";


    private static String createMask(int count) {
        StringBuilder mask = new StringBuilder();
        for (int i = 0; i < count; i++) {
            mask.append("*");
        }
        return mask.toString();
    }


    private static String mask(String needToMaskString, int keepFirstCount, int keepLastCount, int maskCount) {
        return needToMaskString.substring(0, keepFirstCount)
            + createMask(maskCount)
            + needToMaskString.substring(needToMaskString.length() - keepLastCount);
    }


    /**
     * 手机号脱敏处理器
     * 保留前三后四，中间的为星号  "*"
     */
    static MaskProcessor MOBILE_PROCESSOR = data -> {
        if (data instanceof String && ((String) data).startsWith("1") && ((String) data).length() == 11) {
            return mask((String) data, 3, 4, 4);
        }
        return data;
    };


    /**
     * 固定电话脱敏
     * 保留前三后四，中间的为星号  "*"
     */
    static MaskProcessor FIXED_PHONE_PROCESSOR = data -> {
        if (data instanceof String && ((String) data).length() > 5) {
            return mask((String) data, 3, 2, ((String) data).length() - 5);
        }
        return data;
    };


    /**
     * 身份证号脱敏处理器
     * 身份证号的保留前三后四，中间的数为星号  "*"
     */
    static MaskProcessor ID_CARD_NUMBER_PROCESSOR = data -> {
        if (data instanceof String && ((String) data).length() >= 15) {
            return mask((String) data, 3, 4, ((String) data).length() - 7);
        }
        return data;
    };


    /**
     * 姓名脱敏
     */
    static MaskProcessor CHINESE_NAME_PROCESSOR = data -> {
        if (data instanceof String) {
            String name = (String) data;
            if (name.length() == 2) {
                return name.charAt(0) + "*";
            } else if (name.length() == 3) {
                return name.charAt(0) + "*" + name.charAt(2);
            } else if (name.length() == 4) {
                return "**" + name.substring(2, 4);
            } else if (name.length() > 4) {
                return mask(name, 2, 1, name.length() - 3);
            }
        }
        return data;
    };


    /**
     * 地址脱敏
     */
    static MaskProcessor ADDRESS_PROCESSOR = data -> {
        if (data instanceof String) {
            String address = (String) data;
            if (address.length() > 6) {
                return mask(address, 6, 0, 3);
            } else if (address.length() > 3) {
                return mask(address, 3, 0, 3);
            }
        }
        return data;
    };


    /**
     * email 脱敏
     */
    static MaskProcessor EMAIL_PROCESSOR = data -> {
        if (data instanceof String && ((String) data).contains("@")) {
            String fullEmail = (String) data;
            int indexOf = fullEmail.lastIndexOf("@");
            String email = fullEmail.substring(0, indexOf);

            if (email.length() == 1) {
                return "*" + fullEmail.substring(indexOf);
            } else if (email.length() == 2) {
                return "**" + fullEmail.substring(indexOf);
            } else if (email.length() < 5) {
                return mask(email, 2, 0, email.length() - 2) + fullEmail.substring(indexOf);
            } else {
                return mask(email, 3, 0, email.length() - 3) + fullEmail.substring(indexOf);
            }
        }
        return data;
    };


    /**
     * 密码 脱敏
     */
    static MaskProcessor PASSWORD_PROCESSOR = data -> {
        if (data instanceof String) {
            return mask((String) data, 0, 0, ((String) data).length());
        }
        return data;
    };


    /**
     * 车牌号 脱敏
     */
    static MaskProcessor CAR_LICENSE_PROCESSOR = data -> {
        if (data instanceof String) {
            return mask((String) data, 3, 1, ((String) data).length() - 4);
        }
        return data;
    };


    /**
     * 银行卡号 脱敏
     */
    static MaskProcessor BANK_CARD_PROCESSOR = data -> {
        if (data instanceof String && ((String) data).length() >= 8) {
            return mask((String) data, 4, 4, 4);
        }
        return data;
    };


}
