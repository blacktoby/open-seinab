create table if not exists confirmation_input
(
	id bigint auto_increment
		primary key,
	html_id varchar(255) not null,
	name varchar(255) not null,
	form_id bigint not null,
	constraint UKgwuv7onuhw1a7cxy9juwf41x1
		unique (form_id, html_id) using hash
)
engine=InnoDb;

