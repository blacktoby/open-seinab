create table if not exists form_fee
(
	fee bigint not null,
	form_id bigint not null
		primary key
)
engine=InnoDb;

