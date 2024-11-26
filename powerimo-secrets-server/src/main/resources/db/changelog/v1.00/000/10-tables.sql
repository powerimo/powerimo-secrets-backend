create table secret(
    id uuid not null,
    code varchar(255) not null,
    data text,
    hit_count int not null,
    hit_limit int,
    registrar_host varchar(255),
    registrar_agent varchar(255),
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    expires_at timestamp without time zone
);

alter table secret
    add constraint secret_pk primary key (id);

create index secret_ix01 on secret (code);

create table secret_action
(
    id uuid not null,
    secret_id uuid not null,
    action varchar(100),
    details text,
    host varchar(255),
    agent varchar(255),
    action_time timestamp without time zone
);

alter table secret_action
    add constraint secret_hit_pk primary key (id);

create index secret_action_ix01 on secret_action(secret_id);
