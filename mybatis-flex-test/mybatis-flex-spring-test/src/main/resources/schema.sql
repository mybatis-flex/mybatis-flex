CREATE TABLE IF NOT EXISTS `tb_account`
(
    `id`     INTEGER PRIMARY KEY,
    `user_name`  VARCHAR(100) NOT NULL,
    `age` Integer,
    `birthday` DATETIME,
    `sex` tinyint
);

CREATE TABLE IF NOT EXISTS `tb_class`
(
    `id`     INTEGER PRIMARY KEY,
    `user_id`  Integer NOT NULL,
    `class_name` VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS `tb_user`
(
    `id`     INTEGER PRIMARY KEY,
    `name` VARCHAR(100)
);
