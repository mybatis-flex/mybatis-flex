CREATE TABLE IF NOT EXISTS `tb_account`
(
    `id`        INTEGER auto_increment,
    `user_name` VARCHAR(100),
    `age`       Integer
);


CREATE TABLE IF NOT EXISTS `tb_idcard`
(
    `account_id` Integer,
    `card_no`      VARCHAR(100),
    `content`    text
);


CREATE TABLE IF NOT EXISTS `tb_book`
(
    `id`        INTEGER auto_increment,
    `account_id` Integer,
    `title`      VARCHAR(100),
    `content`    text
);

CREATE TABLE IF NOT EXISTS `tb_role`
(
    `id`        INTEGER auto_increment,
    `name`      VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS `tb_role_mapping`
(
    `account_id`        INTEGER ,
    `role_id`      INTEGER
);

CREATE TABLE IF NOT EXISTS `tb_menu`
(
    `id`        INTEGER auto_increment,
    `parent_id`        INTEGER,
    `name`      VARCHAR(100)
);