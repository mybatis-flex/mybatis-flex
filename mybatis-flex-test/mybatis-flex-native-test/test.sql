CREATE TABLE `demo`
(
    `id`          varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
    `first_field` decimal(10, 2)                          DEFAULT NULL,
    `foreign_id`  varchar(255) collate utf8mb4_general_ci default null,
    second_field  decimal(10, 2)                          DEFAULT NULL,
    user_id       varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


INSERT INTO `demo` (`id`, `first_field`, `foreign_id`, second_field, user_id)
values ('1', 10.12, '1', 12.2, '1');
INSERT INTO `demo` (`id`, `first_field`, `foreign_id`, second_field, user_id)
values ('2', 10.12, '2', 12.2, '1');
INSERT INTO `demo` (`id`, `first_field`, `foreign_id`, second_field, user_id)
values ('3', 10.12, '3', 12.2, '1');
INSERT INTO `demo` (`id`, `first_field`, `foreign_id`, second_field, user_id)
values ('4', 10.12, '4', 12.2, '1');
INSERT INTO `demo` (`id`, `first_field`, `foreign_id`, second_field, user_id)
values ('5', 10.12, '5', 12.2, '1');
INSERT INTO `demo` (`id`, `first_field`, `foreign_id`, second_field, user_id)
values ('6', 10.12, '6', NUll, '1');
INSERT INTO `demo` (`id`, `first_field`, `foreign_id`, second_field, user_id)
values ('7', NULL, '7', 12.2, '1');