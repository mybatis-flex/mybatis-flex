package com.mybatisflex.test;

import com.mybatisflex.test.mapper.AccountMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mybatisflex.test.mapper")
public class SampleApplication implements CommandLineRunner {


    @Autowired
    private AccountMapper accountMapper;



    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>>>>>> run");
        System.out.println(this.accountMapper.selectOneById(1));
    }


    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }


}
