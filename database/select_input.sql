create table if not exists select_input
(
	default_text varchar(255) null,
	id bigint not null
		primary key,
	options_key_input_id bigint null
)
engine=InnoDb;

create index if not exists FKptqxqtqyv6qeugqs5w7uym2s6
	on select_input (options_key_input_id);

