create table if not exists input_option
(
	id bigint auto_increment
		primary key,
	input_key varchar(255) null,
	position int not null,
	text varchar(255) null,
	value varchar(255) not null,
	input_id bigint null
)
engine=InnoDb;

create index if not exists FKj0001s7fyoyh1ol8np563hy7b
	on input_option (input_id);

