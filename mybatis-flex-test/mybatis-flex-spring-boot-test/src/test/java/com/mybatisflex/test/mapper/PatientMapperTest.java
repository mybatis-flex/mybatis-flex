package com.mybatisflex.test.mapper;

import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.test.model.PatientVO1;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 患者相关测试
 *
 * @author Ice 2023/09/26
 * @version 1.0
 */
@SpringBootTest
@SuppressWarnings("all")
public class PatientMapperTest {

    @Autowired
    private PatientMapper patientMapper;

    @Test
    public void testRelationOneToManySplitBy() {
        PatientVO1 patientVO1 = patientMapper.selectOneWithRelationsByQueryAs(QueryWrapper.create().orderBy(PatientVO1::getPatientId, false).limit(1), PatientVO1.class);
        System.out.println(JSON.toJSONString(patientVO1));
    }
}
