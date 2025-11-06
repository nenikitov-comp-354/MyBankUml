create table bank(
    id serial primary key,
    name varchar(128) not null
);

create table branch(
    id serial primary key,
    address varchar(128) not null,
    bank serial references bank(id) not null
);

create domain social_insurance_number as char(11) check(value ~ '^\d{3}-\d{3}-\d{3}$');
create domain phone as vachar(15) check(value ~ '^\+[1-9]\d{1,14}$');
create domain email as vachar(256) check(value ~ '^.*@.*$');

create table customer(
    id serial primary key,
    first_name varchar(64) not null,
    last_name varchar(64) not null,
    date_of_birth date not null,
    social_insurance_number social_insurance_number not null unique,
    phone phone,
    email email not null,
);

create table account(
    id serial primary key,
    name varchar(64) not null,
    is_locked boolean not null default false,
    customer serial references customer(id) not null
);

create table transaction(
    id serial primary key,
    from serial references account(id) not null,
    to serial references account(id) not null,
    amount money not null check (money > 0),
    date date not null
);
