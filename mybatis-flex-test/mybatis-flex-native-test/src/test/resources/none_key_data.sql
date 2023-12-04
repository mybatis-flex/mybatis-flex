-- 没有主键生成策略，手动插入 ID

INSERT INTO tb_account6(`id`, `user_name`, `age`)
VALUES (1, '张三', 18),
       (2, '王麻子叔叔', 19);
