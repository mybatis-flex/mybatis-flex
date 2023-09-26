package com.mybatisflex.test.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;

/**
 * 疾病
 *
 * @author Ice 2023/09/26
 * @version 1.0
 */
@Table(value = "tb_disease")
public class Disease implements Serializable {
    private static final long serialVersionUID = -3195530228167432902L;

    /**
     * ID
     */
    @Id(keyType = KeyType.Generator, value = "uuid")
    private String diseaseId;

    /**
     * 疾病名称
     */
    private String name;

    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
