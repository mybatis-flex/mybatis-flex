CREATE TABLE IF NOT EXISTS `entity04`
(
    `id`        VARCHAR(100) PRIMARY KEY,
    `user_name` VARCHAR(100),
    `age`       Integer,
    `sex`       Integer,
    `birthday`  DATETIME,
    `options`   VARCHAR(1024),
    `is_delete` Integer
);