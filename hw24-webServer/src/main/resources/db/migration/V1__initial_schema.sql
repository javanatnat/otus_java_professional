create sequence address_sequence start with 1 increment by 1;

create table address
(
    id     bigint not null primary key,
    street varchar(50)
);

-- Для @GeneratedValue(strategy = GenerationType.SEQUENCE)
create sequence hibernate_sequence start with 1 increment by 1;

create table client
(
    id         bigint not null primary key,
    name       varchar(50),
    address_id bigint,
    FOREIGN KEY (address_id) REFERENCES address(id)
);

create sequence phone_sequence start with 1 increment by 1;

create table phone
(
    id        bigint not null primary key,
    number    varchar(20),
    client_id bigint,
    FOREIGN KEY (client_id) REFERENCES client(id)
);

insert into address(id, street) values(nextval('address_sequence'), 'Street');
insert into client(id, name, address_id) values(nextval('hibernate_sequence'), 'Gosha', 1);
insert into phone(id, number, client_id) values(nextval('phone_sequence'), '12-11-40', 1);
