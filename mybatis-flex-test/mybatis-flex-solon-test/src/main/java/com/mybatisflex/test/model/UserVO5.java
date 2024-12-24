//package com.mybatisflex.test.model;
//
//import com.mybatisflex.annotation.*;
//
//import java.io.Serializable;
//import java.util.List;
//
///**
// * 字段绑定测试
// * @author Ice 2023/09/16
// * @version 1.0
// */
//@Table("tb_user")
//public class UserVO5 implements Serializable {
//    private static final long serialVersionUID = 474700189859144273L;
//
//    @Id
//    private Integer userId;
//    private String userName;
//    private String password;
//
//    @RelationOneToOne(
//        selfField = "userId",
//        targetTable = "tb_id_card",
//        targetField = "id",
//        valueField = "idNumber"
//    )
//    private String idNumberCustomFieldName;
//
//    @RelationOneToMany(
//        selfField = "userId",
//        targetTable = "tb_user_order",
//        targetField = "userId",
//        valueField = "orderId"
//    )
//    private List<Integer> orderIdList;
//
//    @RelationManyToMany(
//        selfField = "userId",
//        targetTable = "tb_role",
//        targetField = "roleId",
//        valueField = "roleName",
//        joinTable = "tb_user_role",
//        joinSelfColumn = "user_id",
//        joinTargetColumn = "role_id"
//    )
//    private List<String> roleNameList;
//
//    public Integer getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Integer userId) {
//        this.userId = userId;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getIdNumberCustomFieldName() {
//        return idNumberCustomFieldName;
//    }
//
//    public void setIdNumberCustomFieldName(String idNumberCustomFieldName) {
//        this.idNumberCustomFieldName = idNumberCustomFieldName;
//    }
//
//    public List<Integer> getOrderIdList() {
//        return orderIdList;
//    }
//
//    public void setOrderIdList(List<Integer> orderIdList) {
//        this.orderIdList = orderIdList;
//    }
//
//    public List<String> getRoleNameList() {
//        return roleNameList;
//    }
//
//    public void setRoleNameList(List<String> roleNameList) {
//        this.roleNameList = roleNameList;
//    }
//
//    @Override
//    public String toString() {
//        return "UserVO5{" +
//            "userId=" + userId +
//            ", userName='" + userName + '\'' +
//            ", password='" + password + '\'' +
//            ", idNumberCustomFieldName='" + idNumberCustomFieldName + '\'' +
//            ", orderIdList=" + orderIdList +
//            ", roleNameList=" + roleNameList +
//            '}';
//    }
//}
