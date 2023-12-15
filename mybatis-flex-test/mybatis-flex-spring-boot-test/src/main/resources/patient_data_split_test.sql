SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_disease
-- ----------------------------
DROP TABLE IF EXISTS `tb_disease`;
CREATE TABLE `tb_disease`  (
                               `disease_id` varchar(32) CHARACTER SET utf8mb4 NOT NULL COMMENT 'ID',
                               `name` varchar(32) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '疾病名称',
                               PRIMARY KEY (`disease_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '疾病信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_disease
-- ----------------------------
INSERT INTO `tb_disease` VALUES ('1', '心脑血管疾病');
INSERT INTO `tb_disease` VALUES ('2', '消化系统疾病');
INSERT INTO `tb_disease` VALUES ('3', '神经系统疾病');
INSERT INTO `tb_disease` VALUES ('4', '免疫系统疾病');

-- ----------------------------
-- Table structure for tb_patient
-- ----------------------------
DROP TABLE IF EXISTS `tb_patient`;
CREATE TABLE `tb_patient`  (
                               `patient_id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                               `name` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '姓名',
                               `disease_ids` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '所患病症(对应字符串类型) 英文逗号 分割',
                               `tag_ids` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '患者标签(对应数字类型) / 分割',
                               PRIMARY KEY (`patient_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '患者信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_patient
-- ----------------------------
INSERT INTO `tb_patient` VALUES (1, '张三', '1', NULL);
INSERT INTO `tb_patient` VALUES (2, '李四', '1,2', '1');
INSERT INTO `tb_patient` VALUES (3, '王五', '1,2,3', '1/2');
INSERT INTO `tb_patient` VALUES (4, '赵六', '1,2,3,4', '1/2/3');

-- ----------------------------
-- Table structure for tb_tag
-- ----------------------------
DROP TABLE IF EXISTS `tb_tag`;
CREATE TABLE `tb_tag`  (
                           `tag_id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `name` varchar(32) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '标签名',
                           PRIMARY KEY (`tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_tag
-- ----------------------------
INSERT INTO `tb_tag` VALUES (1, 'VIP');
INSERT INTO `tb_tag` VALUES (2, 'JAVA开发');
INSERT INTO `tb_tag` VALUES (3, 'Web开发');

SET FOREIGN_KEY_CHECKS = 1;
