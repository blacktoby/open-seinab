create table if not exists reference_input
(
	reference_position int not null,
	input_id bigint not null
		primary key,
	reference_settings_id bigint not null
)
engine=InnoDb;

create index if not exists FKefmkwqqj0ruk38d4wmjt7y4a8
	on reference_input (reference_settings_id);

