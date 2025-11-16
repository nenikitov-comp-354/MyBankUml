create domain non_empty_text as text check(value ~ '^\S(?:.*\S)?$');

create table bank(
    id integer primary key generated always as identity,
    name non_empty_text not null
);

create table branch(
    id serial primary key,
    address non_empty_text not null,
    bank_id integer references bank(id) not null
);

create domain social_insurance_number as char(11) check(value ~ '^\d{3}-\d{3}-\d{3}$');
create domain phone as varchar(15) check(value ~ '^\+[1-9]\d{1,14}$');
create domain email as varchar(256) check(value ~ '^.*@.*$');

create table customer(
    id integer primary key generated always as identity,
    first_name non_empty_text not null,
    last_name non_empty_text not null,
    date_of_birth date not null,
    social_insurance_number social_insurance_number not null unique,
    phone phone,
    email email not null
);

create table account(
    id integer primary key generated always as identity,
    name non_empty_text not null,
    is_locked boolean not null default false,
    customer_id integer references customer(id) not null
);

create table transaction(
    id integer primary key generated always as identity,
    account_id_source integer references account(id) not null,
    account_id_destination integer references account(id) not null,
    amount money not null check (amount > 0::money),
    time timestamp without time zone not null
);
