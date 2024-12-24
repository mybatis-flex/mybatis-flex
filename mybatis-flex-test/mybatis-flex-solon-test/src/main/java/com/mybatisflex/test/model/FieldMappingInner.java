package com.mybatisflex.test.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.activerecord.Model;
import com.mybatisflex.core.keygen.KeyGenerators;

import java.util.Date;
import java.util.Objects;

@Table("field_mapping_inner")
public class FieldMappingInner extends Model<FieldMappingInner> {
    @Id(keyType = KeyType.Generator,value = KeyGenerators.snowFlakeId)
    private String id;
    private String outId;
    private Date createDate;

    public static FieldMappingInner create() {
        return new FieldMappingInner();
    }

    public String getId() {
        return id;
    }

    public FieldMappingInner setId(String id) {
        this.id = id;
        return this;
    }

    public String getOutId() {
        return outId;
    }

    public FieldMappingInner setOutId(String outId) {
        this.outId = outId;
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public FieldMappingInner setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldMappingInner that = (FieldMappingInner) o;
        return Objects.equals(id, that.id) && Objects.equals(outId, that.outId) && Objects.equals(createDate, that.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, outId, createDate);
    }

    @Override
    public String toString() {
        return "FieldMappingInner{" +
            "id='" + id + '\'' +
            ", outId='" + outId + '\'' +
            ", createDate=" + createDate +
            '}';
    }
}
