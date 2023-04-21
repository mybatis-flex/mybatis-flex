CREATE TABLE IF NOT EXISTS `tb_account`
(
    `id`        INTEGER PRIMARY KEY auto_increment,
    `user_name` VARCHAR(100),
    `age`       Integer,
    `sex`       Integer,
    `birthday`  DATETIME,
    `options`   VARCHAR(1024),
    `is_delete` Integer
);


CREATE TABLE IF NOT EXISTS `tb_article`
(
    `id`         INTEGER PRIMARY KEY auto_increment,
    `account_id` Integer,
    `title`      VARCHAR(100),
    `content`    text,
    `is_delete` Integer
);