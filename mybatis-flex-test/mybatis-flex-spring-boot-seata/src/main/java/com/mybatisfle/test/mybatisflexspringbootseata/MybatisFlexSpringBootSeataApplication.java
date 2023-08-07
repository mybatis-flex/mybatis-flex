package com.mybatisfle.test.mybatisflexspringbootseata;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.mybatisfle.test.mybatisflexspringbootseata")
@SpringBootApplication
public class MybatisFlexSpringBootSeataApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisFlexSpringBootSeataApplication.class, args);
	}

}
