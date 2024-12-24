package com.mybatisflex.test.mapper;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;

/**
 * 患者相关测试
 *
 * @author Ice 2023/09/26
 * @version 1.0
 */
@SolonTest
@SuppressWarnings("all")
public class PatientMapperTest {

    @Inject
    private PatientMapper patientMapper;

    @Test
    public void testRelationOneToManySplitBy() {
//        QueryWrapper wrapper = QueryWrapper.create().orderBy(PatientVO1::getPatientId, false).limit(1);
//        PatientVO1 patientVO1 = patientMapper.selectOneWithRelationsByQueryAs(wrapper, PatientVO1.class);
//        System.out.println(JSON.toJSONString(patientVO1));
    }
}
