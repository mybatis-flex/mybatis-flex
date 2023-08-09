CREATE TABLE IF NOT EXISTS `tb_account`
(
    `id`     INTEGER PRIMARY KEY,
    `u_name`  VARCHAR(100) NOT NULL,
    `age` INTEGER,
    `birthday` DATETIME
);

CREATE TABLE IF NOT EXISTS `tb_account1`
(
	`id`     INTEGER PRIMARY KEY,
	`u_name`  VARCHAR(100) NOT NULL,
	`age` INTEGER,
	`birthday` DATETIME
);
