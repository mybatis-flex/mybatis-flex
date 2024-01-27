-- Alisa Test

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `dept_name`   varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    `create_time` datetime NULL DEFAULT NULL,
    `update_time` datetime NULL DEFAULT NULL,
    `create_by`   varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    `update_by`   varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept`
VALUES (1, '开发岗', NULL, NULL, 'DEPT', 'DEPT');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `role_key`    varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    `role_name`   varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    `create_time` datetime NULL DEFAULT NULL,
    `update_time` datetime NULL DEFAULT NULL,
    `create_by`   varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    `update_by`   varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role`
VALUES (1, 'ROOT', '超级管理员', '2000-11-17 22:15:20', '2000-11-17 22:54:14', 'ROLE', 'ROLE');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `user_name`   varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    `age`         int NULL DEFAULT NULL,
    `birthday`    datetime NULL DEFAULT NULL,
    `create_time` datetime NULL DEFAULT NULL,
    `update_time` datetime NULL DEFAULT NULL,
    `create_by`   varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    `update_by`   varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user`
VALUES (1, '张三', 18, '2023-11-17 22:14:40', '2023-11-17 22:54:34', '2023-11-21 22:14:54', 'USER', 'USER');

SET
FOREIGN_KEY_CHECKS = 1;
