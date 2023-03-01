CREATE TABLE IF NOT EXISTS `tb_account`
(
    `id`     INTEGER PRIMARY KEY,
    `user_name`  VARCHAR(100) NOT NULL,
    `age` Integer,
    `birthday` DATETIME
);