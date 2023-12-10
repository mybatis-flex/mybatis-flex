-- 使用主键生成器生成主键，初始化数据时仍然手动设置 ID

INSERT INTO tb_account7(`id`, `user_name`, `age`)
VALUES (1, '张三', 18),
       (2, '王麻子叔叔', 19);
