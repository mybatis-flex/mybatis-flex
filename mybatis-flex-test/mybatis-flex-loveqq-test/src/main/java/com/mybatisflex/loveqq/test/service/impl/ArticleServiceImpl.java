/*
 *  Copyright (c) 2022-2024, Mybatis-Flex (fuhai999@gmail.com).
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

package com.mybatisflex.loveqq.test.service.impl;

import com.kfyty.loveqq.framework.core.autoconfig.annotation.Service;
import com.mybatisflex.annotation.UseDataSource;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.loveqq.framework.boot.autoconfig.service.ServiceImpl;
import com.mybatisflex.loveqq.test.mapper.ArticleMapper;
import com.mybatisflex.loveqq.test.model.Article;
import com.mybatisflex.loveqq.test.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 王帅
 * @since 2023-07-22
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Override
    @UseDataSource("annotation ds")
    public void changeDataSource() {
        LOGGER.info("start1: {}", DataSourceKey.get());
        DataSourceKey.use("ds outer", () -> {
            LOGGER.info("start2: {}", DataSourceKey.get());
            DataSourceKey.use("ds inner", () -> {
                LOGGER.info("start3: {}", DataSourceKey.get());
                LOGGER.info("end3: {}", DataSourceKey.get());
            });
            LOGGER.info("end2: {}", DataSourceKey.get());
        });
        LOGGER.info("end1: {}", DataSourceKey.get());
    }
}
