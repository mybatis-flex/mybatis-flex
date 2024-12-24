SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_account
-- ----------------------------
DROP TABLE IF EXISTS `tb_account`;
CREATE TABLE `tb_account`
(
    `id`        int NOT NULL AUTO_INCREMENT,
    `user_name` varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    `age`       int NULL DEFAULT NULL,
    `birthday`  datetime NULL DEFAULT NULL,
    `gender`    int NULL DEFAULT NULL,
    `is_delete` tinyint(3) UNSIGNED ZEROFILL NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  CHARACTER SET = utf8mb4
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_account
-- ----------------------------
INSERT INTO `tb_account`
VALUES (1, '张三123', 19, '2020-01-11 00:00:00', NULL, 000);
INSERT INTO `tb_account`
VALUES (2, '李四', 19, '2021-03-21 00:00:00', NULL, 000);
INSERT INTO `tb_account`
VALUES (5, '王五', 18, '2023-07-04 15:00:26', NULL, 000);

-- ----------------------------
-- Table structure for tb_article
-- ----------------------------
DROP TABLE IF EXISTS `tb_article`;
CREATE TABLE `tb_article`
(
    `id`         int NOT NULL AUTO_INCREMENT,
    `account_id` int NULL DEFAULT NULL,
    `title`      varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    `content`    text CHARACTER SET utf8mb4 NULL,
    `is_delete`  int NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  CHARACTER SET = utf8mb4
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_article
-- ----------------------------
INSERT INTO `tb_article`
VALUES (1, 1, '标题1', '内容1', 0);
INSERT INTO `tb_article`
VALUES (2, 2, '标题2', '内容2', 0);
INSERT INTO `tb_article`
VALUES (3, 1, '标题3', '内容3', 0);

-- ----------------------------
-- Table structure for tb_good
-- ----------------------------
DROP TABLE IF EXISTS `tb_good`;
CREATE TABLE `tb_good`
(
    `good_id` int NOT NULL AUTO_INCREMENT,
    `name`    varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    `price`   decimal(10, 2) NULL DEFAULT NULL,
    PRIMARY KEY (`good_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  CHARACTER SET = utf8mb4
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_good
-- ----------------------------
INSERT INTO `tb_good`
VALUES (1, '教育学', 45.00);
INSERT INTO `tb_good`
VALUES (2, '教育学基础', 34.00);
INSERT INTO `tb_good`
VALUES (3, '中国教育史', 56.00);
INSERT INTO `tb_good`
VALUES (4, '外国教育史', 43.00);
INSERT INTO `tb_good`
VALUES (5, '教育心理学', 65.00);
INSERT INTO `tb_good`
VALUES (6, '教育研究方法导论', 24.00);
INSERT INTO `tb_good`
VALUES (7, '化学教学论', 33.00);
INSERT INTO `tb_good`
VALUES (8, '化学学科知识与教学能力（初中）', 28.00);
INSERT INTO `tb_good`
VALUES (9, '化学学科知识与教学能力（高中）', 28.00);
INSERT INTO `tb_good`
VALUES (10, '近代化学导论', 67.00);

-- ----------------------------
-- Table structure for tb_id_card
-- ----------------------------
DROP TABLE IF EXISTS `tb_id_card`;
CREATE TABLE `tb_id_card`
(
    `id`        int NOT NULL AUTO_INCREMENT,
    `id_number` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_id_card
-- ----------------------------
INSERT INTO `tb_id_card`
VALUES (1, 'F281C807-C40B-472D-82F5-6130199C6328');
INSERT INTO `tb_id_card`
VALUES (2, '6176E9AD-36EF-4201-A5F7-CCE89B254952');
INSERT INTO `tb_id_card`
VALUES (3, 'A038E6EA-1FDE-4191-AA41-06F78E91F6C2');
INSERT INTO `tb_id_card`
VALUES (4, 'A33E8BAA-93F2-4E28-A161-15CF7D0AE6D0');

-- ----------------------------
-- Table structure for tb_inner
-- ----------------------------
DROP TABLE IF EXISTS `tb_inner`;
CREATE TABLE `tb_inner`
(
    `id`   int NOT NULL AUTO_INCREMENT,
    `type` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_inner
-- ----------------------------
INSERT INTO `tb_inner`
VALUES (2, 'inner type');

-- ----------------------------
-- Table structure for tb_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_order`;
CREATE TABLE `tb_order`
(
    `order_id`    int NOT NULL AUTO_INCREMENT,
    `create_time` datetime NULL DEFAULT NULL,
    PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  CHARACTER SET = utf8mb4
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_order
-- ----------------------------
INSERT INTO `tb_order`
VALUES (1, '2023-02-15 07:58:39');
INSERT INTO `tb_order`
VALUES (2, '2023-01-08 08:44:52');
INSERT INTO `tb_order`
VALUES (3, '2023-04-08 09:06:45');
INSERT INTO `tb_order`
VALUES (4, '2023-07-02 07:59:27');
INSERT INTO `tb_order`
VALUES (5, '2023-01-10 13:59:40');
INSERT INTO `tb_order`
VALUES (6, '2023-02-18 10:50:57');

-- ----------------------------
-- Table structure for tb_order_good
-- ----------------------------
DROP TABLE IF EXISTS `tb_order_good`;
CREATE TABLE `tb_order_good`
(
    `order_id` int NOT NULL,
    `good_id`  int NOT NULL
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_order_good
-- ----------------------------
INSERT INTO `tb_order_good`
VALUES (1, 1);
INSERT INTO `tb_order_good`
VALUES (2, 2);
INSERT INTO `tb_order_good`
VALUES (2, 3);
INSERT INTO `tb_order_good`
VALUES (3, 2);
INSERT INTO `tb_order_good`
VALUES (3, 3);
INSERT INTO `tb_order_good`
VALUES (3, 4);
INSERT INTO `tb_order_good`
VALUES (4, 4);
INSERT INTO `tb_order_good`
VALUES (4, 5);
INSERT INTO `tb_order_good`
VALUES (5, 6);
INSERT INTO `tb_order_good`
VALUES (6, 7);
INSERT INTO `tb_order_good`
VALUES (6, 8);
INSERT INTO `tb_order_good`
VALUES (6, 9);
INSERT INTO `tb_order_good`
VALUES (6, 10);

-- ----------------------------
-- Table structure for tb_outer
-- ----------------------------
DROP TABLE IF EXISTS `tb_outer`;
CREATE TABLE `tb_outer`
(
    `id`   int NOT NULL AUTO_INCREMENT,
    `name` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_outer
-- ----------------------------
INSERT INTO `tb_outer`
VALUES (1, 'outer 01');

-- ----------------------------
-- Table structure for tb_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_role`;
CREATE TABLE `tb_role`
(
    `role_id`   int NOT NULL AUTO_INCREMENT,
    `role_key`  varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    `role_name` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  CHARACTER SET = utf8mb4
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_role
-- ----------------------------
INSERT INTO `tb_role`
VALUES (1, 'common', '普通用户');
INSERT INTO `tb_role`
VALUES (2, 'vip', '贵族用户');
INSERT INTO `tb_role`
VALUES (3, 'svip', '超级贵族用户');
INSERT INTO `tb_role`
VALUES (4, 'admin', '管理员用户');

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`
(
    `user_id`   int NOT NULL AUTO_INCREMENT,
    `user_name` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    `password`  varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user`
VALUES (1, '张三', '12345678');
INSERT INTO `tb_user`
VALUES (2, '李四', '87654321');
INSERT INTO `tb_user`
VALUES (3, '王五', '09897654');
INSERT INTO `tb_user`
VALUES (4, '苏六', '45678345');

-- ----------------------------
-- Table structure for tb_user_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_order`;
CREATE TABLE `tb_user_order`
(
    `user_id`  int NOT NULL,
    `order_id` int NOT NULL
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_user_order
-- ----------------------------
INSERT INTO `tb_user_order`
VALUES (1, 1);
INSERT INTO `tb_user_order`
VALUES (2, 2);
INSERT INTO `tb_user_order`
VALUES (2, 3);
INSERT INTO `tb_user_order`
VALUES (3, 4);
INSERT INTO `tb_user_order`
VALUES (3, 5);
INSERT INTO `tb_user_order`
VALUES (3, 6);



-- ----------------------------
-- Table structure for tb_user_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_role`;
CREATE TABLE `tb_user_role`
(
    `user_id` int NOT NULL,
    `role_id` int NOT NULL
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_user_role
-- ----------------------------
INSERT INTO `tb_user_role`
VALUES (1, 1);
INSERT INTO `tb_user_role`
VALUES (2, 1);
INSERT INTO `tb_user_role`
VALUES (2, 2);
INSERT INTO `tb_user_role`
VALUES (3, 1);
INSERT INTO `tb_user_role`
VALUES (3, 2);
INSERT INTO `tb_user_role`
VALUES (3, 3);
INSERT INTO `tb_user_role`
VALUES (4, 1);
INSERT INTO `tb_user_role`
VALUES (4, 2);
INSERT INTO `tb_user_role`
VALUES (4, 3);
INSERT INTO `tb_user_role`
VALUES (4, 4);
INSERT INTO `tb_user_role`
VALUES (5, 1);

SET
FOREIGN_KEY_CHECKS = 1;
