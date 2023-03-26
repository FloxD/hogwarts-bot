create temporary table user_backup
(
    id           bigint not null primary key,
    discord_id   varchar,
    discord_name varchar,
    exp          bigint not null,
    last_exp     timestamp
);

insert into user_backup select * from user;

drop table user;
create table user
(
    id          bigint not null primary key,
    discord_id  varchar,
    twitch_name varchar,
    exp         bigint not null,
    last_exp    timestamp
);

insert into user select id, discord_id, null, exp, last_exp from user_backup;
drop table user_backup;

create table link
(
    id            bigint not null primary key,
    initiator     integer,
    discord_id    varchar,
    twitch_name   varchar,
    creation_date timestamp
);

