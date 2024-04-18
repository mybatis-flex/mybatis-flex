CREATE TABLE IF NOT EXISTS `tb_relation_account`
(
    `id`        INTEGER,
    `user_name` VARCHAR(100),
    `age`       Integer,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS `tb_idcard`
(
    `account_id` Integer,
    `card_no`      VARCHAR(100),
    `content`    text
);

CREATE TABLE IF NOT EXISTS `tb_idcard_mapping`
(
    `account_id` Integer,
    `idcard_id`      Integer
);

CREATE TABLE IF NOT EXISTS `tb_book`
(
    `id`        INTEGER,
    `account_id` Integer,
    `title`      VARCHAR(100),
    `content`    text,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS `tb_role`
(
    `id`        INTEGER,
    `name`      VARCHAR(100),
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS `tb_role_mapping`
(
    `account_id`   INTEGER ,
    `role_id`      INTEGER
);

CREATE TABLE IF NOT EXISTS `tb_menu`
(
    `id`        INTEGER,
    `parent_id` INTEGER,
    `name`      VARCHAR(100),
    PRIMARY KEY(id)
);
