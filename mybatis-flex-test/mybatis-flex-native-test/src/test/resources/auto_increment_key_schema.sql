CREATE TABLE IF NOT EXISTS `tb_account`
(
    `id`        INTEGER AUTO_INCREMENT,
    `user_name` VARCHAR(100),
    `age`       Integer,
    `sex`       Integer,
    `birthday`  DATETIME,
    `options`   VARCHAR(1024),
    `is_delete` Integer,
    PRIMARY KEY(id)
);


CREATE TABLE IF NOT EXISTS `tb_article`
(
    `id`         INTEGER AUTO_INCREMENT,
    `account_id` Integer,
    `title`      VARCHAR(100),
    `content`    text,
    `is_delete`  Integer,
    PRIMARY KEY(id)
);
