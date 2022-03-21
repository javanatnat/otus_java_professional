create table blacklist
(
    id bigserial not null primary key,
    ip inet
);

create table whitelist
(
    id bigserial not null primary key,
    ip inet
);
