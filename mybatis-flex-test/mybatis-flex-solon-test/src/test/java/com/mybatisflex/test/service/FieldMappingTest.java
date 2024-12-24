package com.mybatisflex.test.service;

import com.mybatisflex.test.mapper.FieldMappingInnerMapper;
import com.mybatisflex.test.mapper.FieldMappingMapper;
import com.mybatisflex.test.model.FieldMapping;
import com.mybatisflex.test.model.FieldMappingInner;
import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;

import java.util.Date;

@SolonTest
public  class FieldMappingTest {
    @Inject
    FieldMappingMapper fieldMappingMapper;
    @Inject
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
