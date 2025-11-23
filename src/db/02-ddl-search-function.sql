create or replace function search_for_customer_ids(queries text[])
returns table(id integer) as $$
	select id
	from customer
	where (
		select bool_and((
			   position(lower(q) in lower(first_name)) > 0
			or position(lower(q) in lower(last_name)) > 0
			or position(lower(q) in lower(date_of_birth::text)) > 0
			or position(lower(q) in lower(email)) > 0
			or position(lower(q) in lower(phone)) > 0
		)) from unnest(queries) as q
	);
$$ language sql;
