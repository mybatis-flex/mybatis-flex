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
package com.mybatisflex.core.exception;

import com.mybatisflex.core.exception.locale.Localizable;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * @author michael
 * @author 王帅
 */
public class MybatisFlexException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Localizable pattern;
    private Object[] arguments;

    public MybatisFlexException(Throwable cause, Localizable pattern, Object[] arguments) {
        super(cause);
        this.pattern = pattern;
        this.arguments = arguments;
    }

    public MybatisFlexException(Localizable pattern, Object... arguments) {
        this.pattern = pattern;
        this.arguments = arguments;
    }

    public MybatisFlexException(String message) {
        super(message);
    }

    public MybatisFlexException(String message, Throwable cause) {
        super(message, cause);
    }

    public MybatisFlexException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return getMessage(Locale.CHINESE);
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage(Locale.getDefault());
    }

    private String getMessage(Locale locale) {
        if (pattern == null) {
            return super.getMessage();
        }
        String localizedString = pattern.getLocalizedString(locale);
        return MessageFormat.format(localizedString, arguments);
    }

}
