CREATE TABLE IF NOT EXISTS `tb_account`
(
    `id`        INTEGER PRIMARY KEY auto_increment,
    `user_name` VARCHAR(100) NOT NULL,
    `age`       Integer,
    `birthday`  DATETIME
);