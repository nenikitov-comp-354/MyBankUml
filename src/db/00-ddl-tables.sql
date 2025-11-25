create domain non_empty_text as text check(value ~ '^\S(?:.*\S)?$');
create domain positive_money as numeric(12, 2) check(value >= 0);

create table bank(
    id integer primary key generated always as identity,
    name non_empty_text not null
);

create table branch(
    id integer primary key generated always as identity,
    address non_empty_text not null,
    bank_id integer references bank(id) not null
);

create domain social_insurance_number as char(11) check(value ~ '^\d{3}-\d{3}-\d{3}$');
create domain phone as varchar(15) check(value ~ '^\+[1-9]\d{1,14}$');
create domain email as varchar(256) check(value ~ '^.+@.+$');

create table customer(
    id integer primary key generated always as identity,
    first_name non_empty_text not null,
    last_name non_empty_text not null,
    date_of_birth date not null,
    social_insurance_number social_insurance_number not null unique,
    phone phone,
    email email not null,
    branch_id integer references branch(id)
);

create table customer_login (
    customer_id integer primary key references customer(id) on delete cascade,
    password text not null
);

create table account(
    id integer primary key generated always as identity,
    name non_empty_text not null,
    is_locked boolean not null default false,
    customer_id integer references customer(id) not null
);

create table account_checking(
    id integer not null references account(id) on delete cascade,
    monthly_fee positive_money not null default 0
);

create table account_savings(
    id integer not null references account(id) on delete cascade,
    interest_rate numeric(7, 4) not null check(interest_rate >= 0)
);

create table account_credit(
    id integer not null references account(id) on delete cascade,
    credit_limit positive_money not null,
    payment_grace_days integer not null default 21 check(payment_grace_days >= 0)
);

create table transaction(
    id integer primary key generated always as identity,
    account_id_source integer references account(id) not null,
    account_id_destination integer references account(id) not null,
    amount positive_money not null check(amount > 0),
    time timestamp without time zone not null,

    constraint ck_source_is_not_destination check(account_id_source <> account_id_destination)
);
