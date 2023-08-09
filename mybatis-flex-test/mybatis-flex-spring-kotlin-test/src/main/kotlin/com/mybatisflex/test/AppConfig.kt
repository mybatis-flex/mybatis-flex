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
package com.mybatisflex.test

import com.mybatisflex.core.mybatis.FlexConfiguration
import com.mybatisflex.spring.FlexSqlSessionFactoryBean
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource

@Configuration
@MapperScan("com.mybatisflex.test.mapper")
open class AppConfig {


	@Bean
	open fun dataSource(): DataSource? {
		return EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.addScript("schema.sql")
			.addScript("data-kt.sql")
			.build()
	}

	@Bean
	open fun sqlSessionFactory(dataSource: DataSource): SqlSessionFactory? {
		val factoryBean: SqlSessionFactoryBean = FlexSqlSessionFactoryBean()
		factoryBean.setDataSource(dataSource)
		val configuration = FlexConfiguration()
		configuration.logImpl = StdOutImpl::class.java
		factoryBean.setConfiguration(configuration)
		return factoryBean.getObject()
	}

	@EventListener(classes = [ContextStartedEvent::class])
	open fun handleContextStartedEvent() {
		println("handleContextStartedEvent listener invoked!")
	}



}


