package com.mybatisflex.loveqq.test.mapper;

import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.mybatisflex.loveqq.test.LoveqqExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.annotation.Resource;

/**
 * 患者相关测试
 *
 * @author Ice 2023/09/26
 * @version 1.0
 */
@Component
@ExtendWith(LoveqqExtension.class)
@SuppressWarnings("all")
public class PatientMapperTest {

    @Resource
    private PatientMapper patientMapper;

    @Test
    public void testRelationOneToManySplitBy() {
//        QueryWrapper wrapper = QueryWrapper.create().orderBy(PatientVO1::getPatientId, false).limit(1);
//        PatientVO1 patientVO1 = patientMapper.selectOneWithRelationsByQueryAs(wrapper, PatientVO1.class);
//        System.out.println(JSON.toJSONString(patientVO1));
    }
}
