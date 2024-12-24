-- ----------------------------
-- Table structure for tb_unmapped_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_unmapped_user`;
CREATE TABLE `tb_unmapped_user`
(
    `id`        bigint primary key AUTO_INCREMENT,
    `name`      varchar(100)  NULL DEFAULT NULL,
    `age`       int NULL DEFAULT NULL,
    `code`      int NULL DEFAULT NULL
)

-- ----------------------------
-- Records of tb_unmapped_user
-- ----------------------------
INSERT INTO `tb_unmapped_user`
VALUES (1, '张三', 28, 64);
INSERT INTO `tb_unmapped_user`
VALUES (2, '李四', 15, 128);
INSERT INTO `tb_unmapped_user`
VALUES (3, '王五', 9,  256);
