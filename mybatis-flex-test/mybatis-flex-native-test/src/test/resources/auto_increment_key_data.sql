-- 设置主键自增时，初始化数据不显式设置 ID

INSERT INTO tb_account(`user_name`, `age`, `sex`, `birthday`, `options`, `is_delete`)
VALUES ('张三', 18, 0,'2020-01-11', '{"key":"value1"}',0),
       ('王麻子叔叔', 19, 1, '2021-03-21', '{"key":"value2"}',0);


INSERT INTO tb_article(`account_id`, `title`, `content`, `is_delete`)
VALUES (1, '标题1', '内容1',0),
       (2, '标题2', '内容2',0),
       (1, '标题3', '内容3',0);
