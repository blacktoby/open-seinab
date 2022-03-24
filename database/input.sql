create table if not exists input
(
	id bigint auto_increment
		primary key,
	default_value varchar(255) null,
	description varchar(255) null,
	html_id varchar(255) not null,
	name varchar(255) not null,
	position int not null,
	required tinyint(1) default 0 null,
	type varchar(255) not null,
	form_id bigint not null,
	input_group bigint null,
	constraint UK76pjax1a26fy1g992yrprm3jq
		unique (form_id, html_id) using hash
)
engine=InnoDb;

create index if not exists FK75cfalaucotje75a3vsw2dvj8
	on input (input_group);

