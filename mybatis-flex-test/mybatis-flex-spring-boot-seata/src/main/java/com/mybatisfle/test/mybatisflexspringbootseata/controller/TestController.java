package com.mybatisfle.test.mybatisflexspringbootseata.controller;

import com.mybatisfle.test.mybatisflexspringbootseata.service.TestService;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {


    @Autowired
    TestService testService;

    @RequestMapping("buy")
    public String buy(){
        //开启审计功能
        AuditManager.setAuditEnable(true);
//设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);
        String flag =String.valueOf(testService.buy());
        return flag;
    }
}
