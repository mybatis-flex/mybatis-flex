package com.mybatisflex.test.model;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.activerecord.Model;
import com.mybatisflex.core.keygen.KeyGenerators;

import java.util.Date;
import java.util.Objects;


@Table("field_mapping")
public class FieldMapping extends Model<FieldMapping> {
    @Id(keyType = KeyType.Generator,value = KeyGenerators.snowFlakeId)
    private String id;
    @Column(ignore = true)
    private Date innerDate;

    public String getId() {
        return id;
    }

    public static FieldMapping create() {
        return new FieldMapping();
    }

    public FieldMapping setId(String id) {
        this.id = id;
        return this;
    }

    public Date getInnerDate() {
        return innerDate;
    }

    public FieldMapping setInnerDate(Date innerDate) {
        this.innerDate = innerDate;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldMapping that = (FieldMapping) o;
        return Objects.equals(id, that.id) && Objects.equals(innerDate, that.innerDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, innerDate);
    }

    @Override
    public String toString() {
        return "FieldMapping{" +
            "id='" + id + '\'' +
            ", innerDate=" + innerDate +
            '}';
    }

}

