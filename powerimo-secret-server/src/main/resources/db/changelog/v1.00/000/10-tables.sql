create table secret(
    id uuid not null,
    code varchar(255) not null,
    data text,
    hit_count int not null,
    hit_limit int,
    registrar_host varchar(255),
    registrar_browser_name varchar(255),
    registrar_browser_version varchar(255),
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    expires_at timestamp without time zone
);

alter table secret
    add constraint secret_pk primary key (id);

create index secret_ix01 on secret (code);

create table secret_hit(
    id uuid not null ,
    secret_id uuid not null,
    agent varchar(255),
    browser_name varchar(255),
    browser_version varchar(255),
    hit_time timestamp without time zone
);

alter table secret_hit
    add constraint secret_hit_pk primary key (id);

create index secret_hit_ix01 on secret_hit(secret_id);
