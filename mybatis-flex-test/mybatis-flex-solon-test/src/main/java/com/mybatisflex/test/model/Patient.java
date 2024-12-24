package com.mybatisflex.test.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.activerecord.Model;

import java.io.Serializable;

/**
 * 患者信息
 *
 * @author Ice 2023/09/26
 * @version 1.0
 */
@Table(value = "tb_patient")
public class Patient extends Model<Patient> implements Serializable {
    private static final long serialVersionUID = 5117723684832788508L;


    /**
     * ID
     */
    @Id
    private Integer patientId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 所患病症(对应字符串类型) 英文逗号 分割
     */
    private String diseaseIds;

    /**
     * 患者标签(对应数字类型) / 分割
     */
    private String tagIds;

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiseaseIds() {
        return diseaseIds;
    }

    public void setDiseaseIds(String diseaseIds) {
        this.diseaseIds = diseaseIds;
    }

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }
}
