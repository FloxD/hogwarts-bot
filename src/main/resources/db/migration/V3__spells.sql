create table spell
(
    id        bigint    not null primary key,
    name      varchar   not null,
    user_id   bigint    not null,
    level     bigint    not null,
    last_cast timestamp not null,
    foreign key (user_id) references user (id) on delete cascade
);

create table main.effect
(
    id                bigint not null primary key,
    spell             varchar,
    user_id           bigint not null,
    last_applied      timestamp,
    duration_in_hours bigint not null,
    foreign key (user_id) references user (id) on delete cascade
)
