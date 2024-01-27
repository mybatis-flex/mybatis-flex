package com.mybatisflex.test.service;

import com.mybatisflex.test.mapper.FieldMappingInnerMapper;
import com.mybatisflex.test.mapper.FieldMappingMapper;
import com.mybatisflex.test.model.FieldMapping;
import com.mybatisflex.test.model.FieldMappingInner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
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
