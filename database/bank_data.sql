create table if not exists bank_data
(
	id bigint auto_increment
		primary key,
	bic varchar(255) null,
	iban varchar(255) null,
	name varchar(255) null,
	event_group_id bigint not null,
	constraint UK_6d6bk31vpu8mc1uanyo63quq8
		unique (event_group_id)
)
engine=InnoDb;

