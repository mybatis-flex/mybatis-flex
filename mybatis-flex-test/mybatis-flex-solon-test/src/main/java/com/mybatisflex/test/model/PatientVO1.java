package com.mybatisflex.test.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.RelationOneToMany;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 患者VO
 *
 * @author Ice 2023/09/26
 * @version 1.0
 */
@Table(value = "tb_patient")
public class PatientVO1 implements Serializable {
    private static final long serialVersionUID = -2298625009592638988L;

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

    @RelationOneToMany(
        selfField = "diseaseIds",
        selfValueSplitBy = ",", //使用 , 进行分割
        targetTable = "tb_disease", //只获取某个字段值需要填入目标表名
        targetField = "diseaseId", //测试目标字段是字符串类型是否正常转换
        valueField = "name" //测试只获取某个字段值是否正常
    )
    private List<String> diseaseNameList;

    @RelationOneToMany(
        selfField = "tagIds",
        selfValueSplitBy = "/", //使用 / 进行分割
        targetField = "tagId" //测试目标字段是数字类型是否正常转换
    )
    private List<Tag> tagList;

    @RelationOneToMany(
        selfField = "diseaseIds",
        selfValueSplitBy = ",", //使用 , 进行分割
        targetField = "diseaseId", //测试目标字段是字符串类型是否正常转换
        mapKeyField = "diseaseId" //测试Map映射
    )
    private Map<String, Disease> diseaseMap;

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

    public List<String> getDiseaseNameList() {
        return diseaseNameList;
    }

    public void setDiseaseNameList(List<String> diseaseNameList) {
        this.diseaseNameList = diseaseNameList;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public Map<String, Disease> getDiseaseMap() {
        return diseaseMap;
    }

    public void setDiseaseMap(Map<String, Disease> diseaseMap) {
        this.diseaseMap = diseaseMap;
    }
}
