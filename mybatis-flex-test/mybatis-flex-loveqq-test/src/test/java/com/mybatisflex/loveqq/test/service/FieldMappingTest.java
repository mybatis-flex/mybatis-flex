package com.mybatisflex.loveqq.test.service;

import com.kfyty.loveqq.framework.core.autoconfig.annotation.Autowired;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.mybatisflex.loveqq.test.LoveqqExtension;
import com.mybatisflex.loveqq.test.mapper.FieldMappingInnerMapper;
import com.mybatisflex.loveqq.test.mapper.FieldMappingMapper;
import com.mybatisflex.loveqq.test.model.FieldMapping;
import com.mybatisflex.loveqq.test.model.FieldMappingInner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Date;

@Component
@ExtendWith(LoveqqExtension.class)
class FieldMappingTest {
    @Autowired
    FieldMappingMapper fieldMappingMapper;

    @Autowired
    FieldMappingInnerMapper fieldMappingInnerMapper;

    @Test
    void testFieldMapping() {
        String fieldId = FieldMapping.create().saveOpt().get().getId();
        FieldMappingInner.create().setCreateDate(new Date()).setOutId(fieldId).save();
        FieldMapping.create()
            .withFields()
            .fieldMapping(FieldMapping::getInnerDate, con ->
                FieldMappingInner.create().select(FieldMappingInner::getCreateDate).where(FieldMappingInner::getOutId).eq(con.getId()).toQueryWrapper()
            )
            .list().forEach(System.out::println);
    }
}
