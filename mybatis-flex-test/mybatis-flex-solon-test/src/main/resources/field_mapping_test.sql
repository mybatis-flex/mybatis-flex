create table field_mapping
(
    id varchar(255) not null
        primary key
);
create table field_mapping_inner
(
    id          varchar(255) not null
        primary key,
    out_id      varchar(255) null,
    create_date timestamp null
);
