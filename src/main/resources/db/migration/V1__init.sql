create table main.audit
(
    id        bigint not null primary key,
    name      varchar,
    points    bigint not null,
    timestamp timestamp,
    userid    bigint not null,
    username  varchar
);

create table main.house
(
    id     bigint not null primary key,
    name   varchar,
    points bigint not null
);

INSERT INTO house (id, name, points) VALUES (1, 'Gryffindor', 0);
INSERT INTO house (id, name, points) VALUES (2, 'Hufflepuff', 0);
INSERT INTO house (id, name, points) VALUES (3, 'Ravenclaw', 0);
INSERT INTO house (id, name, points) VALUES (4, 'Slytherin', 0);

create table main.user
(
    id           bigint not null primary key,
    discord_id   varchar,
    discord_name varchar,
    exp          bigint not null,
    last_exp     timestamp
);
