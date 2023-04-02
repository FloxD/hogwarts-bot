create table spell
(
    id    bigint  not null primary key,
    name  varchar not null,
    level bigint  not null
);

create table user_spell
(
    user_id  bigint not null,
    spell_id bigint not null,
    primary key (user_id, spell_id),
    foreign key (user_id) references user (id) on delete cascade,
    foreign key (spell_id) references spell (id) on delete cascade
);
