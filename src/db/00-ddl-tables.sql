create table bank(
    id bigint primary key generated always as identity,
    name varchar(128) not null
);

create table branch(
    id serial primary key,
    address varchar(128) not null,
    bank bigint references bank(id) not null
);

create domain social_insurance_number as char(11) check(value ~ '^\d{3}-\d{3}-\d{3}$');
create domain phone as varchar(15) check(value ~ '^\+[1-9]\d{1,14}$');
create domain email as varchar(256) check(value ~ '^.*@.*$');

create table customer(
    id bigint primary key generated always as identity,
    first_name varchar(64) not null,
    last_name varchar(64) not null,
    date_of_birth date not null,
    social_insurance_number social_insurance_number not null unique,
    phone phone,
    email email not null
);

create table account(
    id bigint primary key generated always as identity,
    name varchar(64) not null,
    is_locked boolean not null default false,
    customer bigint references customer(id) not null
);

create table transaction(
    id bigint primary key generated always as identity,
    source bigint references account(id) not null,
    destination bigint references account(id) not null,
    amount money not null check (amount > 0::money),
    date date not null
);
