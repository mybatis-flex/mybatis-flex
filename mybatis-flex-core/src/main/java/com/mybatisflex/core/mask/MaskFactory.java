/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.mask;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据脱敏工厂类
 */
public class MaskFactory {


    /**
     * 脱敏处理器
     */
    private static Map<String, MaskProcesser> processerMap = new HashMap<>();


    static {
        registerMaskProcesser(Masks.MOBILE, Masks.MOBILE_PROCESSER);
        registerMaskProcesser(Masks.FIXED_PHONE, Masks.FIXED_PHONE_PROCESSER);
        registerMaskProcesser(Masks.ID_CARD_NUMBER, Masks.ID_CARD_NUMBER_PROCESSER);
        registerMaskProcesser(Masks.CHINESE_NAME, Masks.CHINESE_NAME_PROCESSER);
        registerMaskProcesser(Masks.ADDRESS, Masks.ADDRESS_PROCESSER);
        registerMaskProcesser(Masks.EMAIL, Masks.EMAIL_PROCESSER);
        registerMaskProcesser(Masks.PASSWORD, Masks.PASSWORD_PROCESSER);
        registerMaskProcesser(Masks.CAR_LICENSE, Masks.CAR_LICENSE_PROCESSER);
        registerMaskProcesser(Masks.BANK_CARD_NUMBER, Masks.BANK_CARD_PROCESSER);
    }


    public static void registerMaskProcesser(String type, MaskProcesser processer) {
        processerMap.put(type, processer);
    }


    public static Object mask(String type, Object data) {
        MaskProcesser maskProcesser = processerMap.get(type);
        if (maskProcesser == null) {
            throw new IllegalStateException("Can not get mask processer for by type: " + type);
        }
        return maskProcesser.mask(data);
    }

}
