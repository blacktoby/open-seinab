create table if not exists abbreviated_key_value
(
	id bigint auto_increment
		primary key,
	value_key varchar(255) not null,
	value varchar(255) not null,
	abbreviated_input_id bigint not null,
	constraint UKelawaeceubxd920c4s2gbikeu
		unique (abbreviated_input_id, value_key) using hash
)
engine=InnoDb;

