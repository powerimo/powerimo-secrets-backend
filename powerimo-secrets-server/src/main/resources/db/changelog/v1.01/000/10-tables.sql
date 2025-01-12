alter table secret
    add column link_password text;

comment on column secret.link_password is 'Password required for opening secret';